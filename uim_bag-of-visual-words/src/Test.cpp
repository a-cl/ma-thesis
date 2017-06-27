/*
 * Test.cpp
 */

#include "Test.h"

#include <string>

using namespace std;

Test::Test(string img1, string img2, bool sameClass) {
	this->img1 = img1;
	this->img2 = img2;
	this->sameClass = sameClass;
	this->similarity = 0.f;
}

Test::~Test() {}

string Test::getImage1() {
	return this->img1;
}

string Test::getImage2() {
	return this->img2;
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
