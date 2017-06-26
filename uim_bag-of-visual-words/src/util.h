/*
 * util.h
 */

#ifndef UTIL_H_
#define UTIL_H_

#include <vector>
#include <string>

#include <opencv2/core/core.hpp>

void initCUDA();

void checkCUDAError(const char* functionName);

float** malloc2D(int rows, int cols);

float** matToPtr(cv::Mat *features);

void printHistogram(int* histogram, int length);

std::vector<std::string> split(const std::string s, char delim);

template<typename T>
void print_array(T& array, int m, int n);

double time();

#endif /* UTIL_H_ */
