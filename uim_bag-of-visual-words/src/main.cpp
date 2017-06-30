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
	const string path = "data/";//argv[0];
	const int k = 20;//argv[1]

	BagOfWords *bow = new BagOfWords(path, k);
	bow->setMode(1);

	if (TASK == 0) {
		bow->createModel();
		bow->writeModel();
	} else if (TASK == 1) {
		bow->readModel();
		bow->runTests();
	} else if (TASK == 2) {
		bow->createModel();
		bow->runTests();
	}

	delete bow;
	return 0;
}
