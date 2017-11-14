/*
 * BagOfWords.cpp
 *
 *  Created on: 23.05.2017
 *      Author: root
 */

#include "BagOfWords.h"

#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <iomanip>
#include <iostream>
#include <fstream>

#include <opencv2/opencv.hpp>
#include <opencv2/highgui.hpp>
#include <vector>
#include <string>
#include <sstream>

using namespace std;
using namespace cv;

#include "histo_gpu.h"
#include "histo_cpu.h"
#include "kmeans_cpu.h"
#include "kmeans_gpu.h"
#include "kmeans_global_gpu.h"
#include "util.h"

string intToString (int a) {
    ostringstream temp;
    temp<<a;
    return temp.str();
}

BagOfWords::BagOfWords(unsigned int k) {
	this->mode = 0;
	this->variant = 0;
	this->k = k;
	this->count = -1;
	this->size = FEATURE_SIZE;
	this->tests = vector<Test>();
	this->modelInitialized = false;
	this->clusters = NULL;
	this->membership = NULL;

	srand(time(NULL));
}

BagOfWords::~BagOfWords() {
	free(this->clusters);
	free(this->membership);
}

void BagOfWords::createModel(const string modelPath) {
	cout << "Generating model ";
	cout << (mode == 0 ? "(GPU)" : "(CPU)") << endl;
	cout << "Extracting features" << endl;

	Mat features = this->readFeatures(modelPath);
	cout << "Extraction completed (" << features.rows << " features)" << endl;

	this->clusters = malloc2D(k, this->size);
	this->membership = (int *) malloc(this->count * sizeof(int*));
	cout << "Clustering features with k = " << intToString(this->k) << endl;

	if (this->mode == 0) {
		if (this->variant == 0) {
			kmeans_global_gpu(matToPtr(&features), this->clusters, this->membership, this->count, this->size, this->k);
		} else {
			kmeans_gpu(matToPtr(&features), this->clusters, this->membership, this->count, this->size, this->k);
		}
	} else {
		kmeans_cpu(matToPtr(&features), this->clusters, this->membership, this->count, this->size, this->k);
	}
	this->modelInitialized = true;
	cout << "Clustering completed" << endl;
}

void BagOfWords::writeModel(const string modelPath) {
	if (!this->modelInitialized) {
		cerr << "Cannot write model: No model created." << endl;
		return;
	}

	cout << "Writing model and membership" << endl;

	ofstream file;
	file.open((modelPath + "/" + intToString(this->k)).c_str());
	file << this->k << " " << this->size << "\n";

	for (int i = 0; i < this->k; i++) {
		for (int j = 0; j < this->size; j++) {
			file << this->clusters[i][j] << " ";
		}
		file << "\n";
	}
	file.close();

	/*
	string memberFile = "/membership" + intToString(this->k);
	file.open((this->path + memberFile).c_str());
	file << count << "\n";

	for (int i = 0; i < this->count; i++) {
		file << this->membership[i] << "\n";
	}
	file.close();
	*/
}

void BagOfWords::readModel(const string modelPath) {
	cout << "Reading model" << endl;

	string line;
	ifstream file;
	file.open((modelPath + "/" + intToString(this->k)).c_str());
	int i = 0;

	getline(file, line);
	vector<string> first = split(line, ' ');
	istringstream(first[0]) >> this->k;
	istringstream(first[1]) >> this->size;

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
	this->modelInitialized = true;
}

void BagOfWords::runTests (const string testSourcePath, const string testTargetPath) {
	this->readTestData(testSourcePath);
	this->executeTests();
	this->writeTestResults(testTargetPath);
}

void BagOfWords::readTestData (const string testPath) {
	cout << "Reading tests" << endl;

	this->tests = vector<Test>();
	string line;
	ifstream file;
	file.open(testPath.c_str());

	while (getline(file, line)) {
		vector<string> tokens = split(line, ' ');
		string img1 = tokens[0];
		string img2 = tokens[1];
		bool sameClass = tokens[2] == "-" ? false : true;
		this->tests.push_back(Test(img1, img2, sameClass));
	}

	file.close();
}

void BagOfWords::executeTests() {
	cout << "Executing tests" << endl;

	float *histo1 = (float*) calloc(this->k, sizeof(float));
	float *histo2 = (float*) calloc(this->k, sizeof(float));

	for (int i = 0; i < this->tests.size(); i++) {
		if (i > 0 && (i + 1) % 100 == 0) {
			cout << "Finished " << (i + 1) << " tests" << endl;
		}

		Test test = this->tests[i];
		histo1 = computeVisualWords(this->readFeatures(test.getImage1()));
		histo2 = computeVisualWords(this->readFeatures(test.getImage2()));
		this->tests[i].setSimilarity(calculateSimilarity(histo1, histo2));
	}

	free(histo1);
	free(histo2);
}

void BagOfWords::writeTestResults(const string testPath) {
	cout << "Writing tests" << endl;

	ofstream file;
	string fileName = testPath + "/" + intToString(this->k) + ".txt";
	file.open(fileName.c_str());

	for (int i = 0; i < this->tests.size(); i++) {
		Test test = tests[i];
		String sameClass = test.isSameClass() ? "+" : "-";

		file << test.getImage1() << " " << test.getImage2() << " " << sameClass << " "
			 << std::fixed << std::setprecision(8) << test.getSimilarity() << endl;
	}
	file.close();
}

float* BagOfWords::computeVisualWords(Mat features) {
	if (!this->modelInitialized) {
		cerr << "Cannot label image: No model provided." << endl;
		return (float*) calloc(k, sizeof(float));
	}

	const unsigned long count = features.rows;
	float *histo = (float*) calloc(k, sizeof(float));

	if (this->mode == 0) {
		histo_gpu(matToPtr(&features), this->clusters, histo, this->k, count, this->size);
	} else {
		histo_cpu(matToPtr(&features), this->clusters, histo, this->k, count, this->size);
	}

	return histo;
}

float BagOfWords::calculateSimilarity (float *histo1, float *histo2) {
	float mse = 0;

	for (int i = 0; i < this->k; i++) {
		float err = histo2[i] - histo1[i];
		mse += err * err;
	}

	return mse / this->k;
}

// TODO: check
Mat BagOfWords::readFeatures(const string featuresPath) {
	ifstream file;
	string line;
	file.open(featuresPath.c_str());
	Mat descriptors;

	while (getline(file, line)) {
		vector<string> tokens = split(line, ' ');
		vector<float> values;

		for (int i = 0; i < tokens.size(); i++) {
			values.push_back(atof(tokens[i].c_str()));
		}

		Mat1f mat(Mat1f(values).t());
		descriptors.push_back(mat);
	}
	file.close();

	// TODO: set dim
	return descriptors;
}

void BagOfWords::setK(unsigned int k) {
	this->k = k;
}

void BagOfWords::setMode(unsigned int mode) {
	this->mode = mode;
}

void BagOfWords::setVariant(unsigned int variant) {
	this->variant = variant;
}

void BagOfWords::setSize(unsigned int size) {
	this->size = size;
}
