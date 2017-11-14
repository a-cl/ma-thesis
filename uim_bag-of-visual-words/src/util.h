/*
 * util.h
 */

#ifndef UTIL_H_
#define UTIL_H_

#include <vector>
#include <string>
#include <opencv2/opencv.hpp>

#include "Test.h"

void initCUDA();

void checkCUDAError(const char* functionName);

float** malloc2D(int rows, int cols);

float** matToPtr(cv::Mat *features);

void printHistogram(string imagePath, float *histo, int k);

std::vector<std::string> split(const std::string s, char delim);

template<typename T>
void print_array(T& array, int m, int n);

vector<string> readDir(string directory);

double time();

#endif /* UTIL_H_ */
