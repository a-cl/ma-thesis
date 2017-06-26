/*
 * BagOfWords.cpp
 *
 *  Created on: 23.05.2017
 *      Author: root
 */

#include "BagOfWords.h"

#include <stdlib.h>
#include <sys/stat.h>
#include <dirent.h>

#include <iomanip>
#include <iostream>
#include <fstream>

#include <vector>
#include <string>

using namespace std;

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/xfeatures2d.hpp>

using namespace cv;

#include "histo_gpu.h"
#include "histo_cpu.h"
#include "kmeans_cpu.h"
#include "kmeans_gpu.h"
#include "util.h"

BagOfWords::BagOfWords(unsigned int k) {
	this->mode = 0;
	this->k = k;
	this->count = -1;
	this->size = SIFT_SIZE;
	this->initialized = false;
	this->clusters = NULL;
	this->membership = NULL;
}

BagOfWords::~BagOfWords() {
	free(this->clusters);
	free(this->membership);
}

void BagOfWords::createModel(vector<string> directories) {
	cout << "Generating model ";
	cout << (mode == 0 ? " (GPU)" : " (CPU)") << endl;
	cout << "Extracting features" << endl;

	Mat features;

	for (int i = 0; i < directories.size(); i++) {
		vector<string> images = this->readDir(directories[i]);
		Mat imageFeatures = this->extractFeatures(images);
		features.push_back(imageFeatures);
		this->count += imageFeatures.rows;

		cout << "Extraction of directory " << directories[i];
		cout << " completed (" << imageFeatures.rows << " features)." << endl;
	}

	this->clusters = malloc2D(k, this->size);
	this->membership = (int *) malloc(this->count * sizeof(int*));
	cout << "Clustering features" << endl;

	if (this->mode == 0) {
		kmeans_gpu(matToPtr(&features), this->clusters, this->membership, this->count, this->size, this->k);
	} else {
		kmeans_cpu(matToPtr(&features), this->clusters, this->membership, this->count, this->size, this->k);
	}
	this->initialized = true;
	cout << "Clustering completed" << endl;
}

void BagOfWords::writeModel() {
	if (!this->initialized) {
		cerr << "Cannot write model: No model provided." << endl;
		return;
	}

	cout << "Writing model" << endl;

	ofstream file;
	file.open("model");
	file << this->k << " " << this->size << "\n";

	for (int i = 0; i < this->k; i++) {
		for (int j = 0; j < this->size; j++) {
			file << this->clusters[i][j] << " ";
		}
		file << "\n";
	}
	file.close();

	cout << "Writing membership" << endl;

	file.open("membership");
	file << count << "\n";

	for (int i = 0; i < this->count; i++) {
		file << this->membership[i] << "\n";
	}
	file.close();
}

void BagOfWords::readModel(string modelPath) {
	cout << "Reading model: " << modelPath << endl;

	string line;
	ifstream file;
	file.open(modelPath.c_str());
	int i = 0;

	getline(file, line);
	vector<string> first = split(line, ' ');
	istringstream(first[0]) >> this->k;
	istringstream(first[1]) >> this->size;

	//free(this->clusters);
	this->clusters = (float**) malloc(this->k * sizeof(float**));

	while (getline(file, line)) {
		clusters[i] = (float*) malloc(this->size * sizeof(float));
		vector<string> tokens = split(line, ' ');

		for (int j = 0; j < tokens.size(); j++) {
			istringstream(tokens[j]) >> this->clusters[i][j];
		}
		i++;
	}

	// TODO: membership + count

	file.close();
	this->initialized = true;
}

void BagOfWords::labelImage(string imagePath) {
	if (!this->initialized) {
		cerr << "Cannot label image: No model provided." << endl;
		return;
	}

	const Mat img = imread(imagePath, 0);
	cv::Ptr<Feature2D> f2d = xfeatures2d::SIFT::create();
	vector<KeyPoint> keypoints;
	Mat features;

	f2d->detect(img, keypoints);
	f2d->compute(img, keypoints, features);

	const unsigned long count = features.rows;
	float *histo = (float*) calloc(k, sizeof(float));

	cout << "Computing histogram for " << imagePath << "" << endl;

	if (this->mode == 0) {
		histo_gpu(matToPtr(&features), this->clusters, histo, this->k, count, this->size);
	} else {
		histo_cpu(matToPtr(&features), this->clusters, histo, this->k, count, this->size);
	}

	cout << "Word frequencies for " << imagePath << ": " << endl;

	for (int i = 0; i < k; i++) {
		cout << "bin " << i << ": " << histo[i] << endl;
	}

	free(histo);
}

void BagOfWords::setMode(unsigned int mode) {
	this->mode = mode;
}

void BagOfWords::setSize(unsigned int size) {
	this->size = size;
}

vector<string> BagOfWords::readDir(string directory) {
	DIR *dir;
	vector<string> out;

	class dirent *ent;
	class stat st;

	dir = opendir(directory.c_str());
	while ((ent = readdir(dir)) != NULL) {
		const string file_name = ent->d_name;
		const string full_file_name = directory + "/" + file_name;

		if (file_name[0] == '.')
			continue;

		if (stat(full_file_name.c_str(), &st) == -1)
			continue;

		const bool is_directory = (st.st_mode & S_IFDIR) != 0;

		if (is_directory)
			continue;

		out.push_back(full_file_name);
	}
	closedir(dir);
	return out;
}

Mat BagOfWords::extractFeatures(vector<string> imagePaths) {
	Mat features;

	for (int i = 0; i < imagePaths.size(); i++) {
		const Mat img = imread(imagePaths[i], 0);
		cv::Ptr<Feature2D> f2d = xfeatures2d::SIFT::create();
		vector<KeyPoint> keypoints;
		Mat descriptors;

		f2d->detect(img, keypoints);
		f2d->compute(img, keypoints, descriptors);
		features.push_back(descriptors);
		cout << "  Extracted " << descriptors.rows << " features from " << imagePaths[i] << endl;
	}

	return features;
}
