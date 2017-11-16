/*
 * main.cpp
 */
#include <stdlib.h>
#include <iomanip>
#include <iostream>
#include <string>

#include "BagOfWords.h"
#include "Test.h"
#include "util.h"

using namespace std;

// generate model: 0, run tests: 1, make model and run test: 2
const unsigned int TASK = 2;

int main(int argc, char *argv[]) {
	const string trainPath = "data/2/train128.txt";
	const string modelPath = "data/2/model";
	const string testSourcePath = "data/2/test128.txt";
	const string testTargetPath = "data/2/test128_k";
	const int k = 500;

	BagOfWords *bow = new BagOfWords(k);
	bow->setMode(1);
	bow->setVariant(0);

	if (TASK == 0) {
		bow->createModel(trainPath);
		bow->writeModel(modelPath);
	} else if (TASK == 1) {
		bow->readModel(modelPath);
		bow->runTests(testSourcePath, testTargetPath);
	} else if (TASK == 2) {
		bow->createModel(trainPath);
		bow->runTests(testSourcePath, testTargetPath);
	}

	delete bow;
	return 0;
}
