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

// declarations

// generate model: 0, label image: 1
const unsigned int TASK = 1;

const int k = 5;

// function definitions

int main() {
	BagOfWords *bow = new BagOfWords(k);
	bow->setMode(0);

	if (TASK == 0) {
		vector<string> files = vector<string>();
		//files.push_back("caltech101/airplanes");
		files.push_back("caltech101/dolphin");
		files.push_back("caltech101/strawberry");
		files.push_back("caltech101/sunflower");
		files.push_back("caltech101/bass");
		files.push_back("caltech101/chair");

		bow->createModel(files);
		bow->writeModel();
	} else if (TASK == 1) {
		bow->readTests("tests.txt");
		bow->readModel("model");
		bow->executeTests();
	}

	delete bow;
	return 0;
}
