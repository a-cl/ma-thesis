/*
 * Test.cpp
 */

#include "Test.h"

#include <string>
#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

Test::Test(string img1, Mat features1, string img2, Mat features2, bool sameClass) {
	this->img1 = img1;
	this->features1 = features1;
	this->img2 = img2;
	this->features2 = features2;
	this->sameClass = sameClass;
	this->similarity = 0.f;
}

Test::~Test() {}

string Test::getImage1() {
	return this->img1;
}

Mat Test::getFeatures1() {
	return this->features1;
}

string Test::getImage2() {
	return this->img2;
}

Mat Test::getFeatures2() {
	return this->features2;
}

bool Test::isSameClass() {
	return this->sameClass;
}

float Test::getSimilarity() {
	return this->similarity;
}

void Test::setSimilarity(float similarity) {
	this->similarity = similarity;
}
