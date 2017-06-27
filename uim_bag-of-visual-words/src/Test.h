/*
 * Test.h
 */

#ifndef TEST_H_
#define TEST_H_

#include <string>

using namespace std;

class Test {
public:
	Test(string img1, string img2, bool sameClass);

	virtual ~Test();

	string getImage1();

	string getImage2();

	bool isSameClass();

	float getSimilarity();

	void setSimilarity(float sim);

private:
	string img1;

	string img2;

	bool sameClass;

	float similarity;
};

#endif
