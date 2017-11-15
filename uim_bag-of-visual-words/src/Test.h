/*
 * Test.h
 */

#ifndef TEST_H_
#define TEST_H_

#include <string>
#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

class Test {
public:
	Test(string img1, Mat features1, string img2, Mat features2, bool sameClass);

	virtual ~Test();

	string getImage1();

	Mat getFeatures1();

	string getImage2();

	Mat getFeatures2();

	bool isSameClass();

	float getSimilarity();

	void setSimilarity(float sim);

private:
	string img1;

	Mat features1;

	string img2;

	Mat features2;

	bool sameClass;

	float similarity;
};

#endif
