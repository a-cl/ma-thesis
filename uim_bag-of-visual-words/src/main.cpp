/*
 * main.cpp
 */

#include <stdlib.h>
#include <iomanip>
#include <iostream>
#include <string>

#include "BagOfWords.h"

using namespace std;

// declarations

// generate model: 0, label image: 1
const unsigned int TASK = 1;

// function definitions

int main() {
	BagOfWords *bow = new BagOfWords(5);
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
		bow->readModel("model");
		bow->labelImage("caltech101/dolphin/image_0001.jpg");
	}

	delete bow;
	return 0;
}
