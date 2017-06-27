/*
 * util.cpp
 */

#include <dirent.h>
#include <stdlib.h>
#include <fstream>
#include <iomanip>
#include <iostream>
#include <math.h>
#include <sstream>
#include <iterator>
#include <vector>
#include <sys/time.h>
#include <sys/stat.h>
#include <stdio.h>

#include <opencv2/core/core.hpp>

#include "Test.h"

using namespace std;

// functions

void initCUDA() {
	int deviceCount = 0;
	cudaGetDeviceCount(&deviceCount);
	if (deviceCount == 0) {
		cout << "Error: no CUDA device found.\n" << endl;
		exit(1);
	}

	cudaSetDevice(0);
}

void checkCUDAError(const char* functionName) {
	cudaError_t error = cudaGetLastError();
	if (error != cudaSuccess) {
		cout << "CUDA error in " << functionName << ": " << cudaGetErrorString(error) << endl;
		exit(1);
	}
}

float** malloc2D(int rows, int cols) {
	float** array = (float **) malloc(rows * sizeof(float*));
	array[0] = (float *) malloc(rows * cols * sizeof(float));
	for (int i = 1; i < rows; i++)
		array[i] = array[i - 1] + cols;
	return array;
}

float** matToPtr(cv::Mat *features) {
	float **points = (float**) malloc(features->rows * sizeof(float*));

	for (int i = 0; i < features->rows; i++) {
		points[i] = (float*) malloc(sizeof(float) * features->cols);
		float *row = features->ptr<float>(i);

		for (int j = 0; j < features->cols; j++) {
			points[i][j] = row[j];
		}
	}

	return points;
}

template<typename T>
void print_array(T& array, int m, int n) {
	for (int i = 0; i < m; i++) {
		for (int j = 0; j < n; j++) {
			typename T::value_type value = array[i * n + j];
			std::cout << value << " ";
		}
		std::cout << std::endl;
	}
}

void printHistogram(string imagePath, float *histo, int k) {
	cout << "Word frequencies for " << imagePath << ": " << endl;

	for (int i = 0; i < k; i++) {
		cout << "bin " << i << ": " << histo[i] << endl;
	}
}

vector<string> split(const string s, char delim) {
	vector<string> result;
	stringstream ss;
	ss.str(s);
	string item;

	while (getline(ss, item, delim)) {
		result.push_back(item);
	}
	return result;
}

vector<string> readDir(string directory) {
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

double time () {
    struct timeval start;
    struct timezone timezone;

    if (gettimeofday(&start, &timezone) == -1) {
        perror("Error: calling gettimeofday() not successful.\n");
    }
    		/* in seconds */			/* in microseconds */
    return ((double) start.tv_sec) + ((double) start.tv_usec) / 1000000.0;
}
