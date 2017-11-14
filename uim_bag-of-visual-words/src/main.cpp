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
	const string trainPath = "data/1/train36.txt";
	const string modelPath = "data/1/model.txt";
	const string testSourcePath = "data/1/test36.txt";
	const string testTargetPath = "data/1/test36_res.txt";
	const int k = 20;

	BagOfWords *bow = new BagOfWords(k);
	bow->setMode(1);

	if (TASK == 0) {
		bow->createModel(trainPath);
		bow->writeModel(modelPath);
	} else if (TASK == 1) {
		bow->readModel(modelPath);
		bow->runTests(testSourcePath, testTargetPath);
	} else if (TASK == 2) {
		bow->createModel(modelPath);
		bow->runTests(testSourcePath, testTargetPath);
	}

	delete bow;
	return 0;
}
