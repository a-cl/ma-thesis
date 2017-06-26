/*
 * BagOfWords.h
 *
 *  Created on: 23.05.2017
 *      Author: root
 */

#ifndef BAGOFWORDS_H_
#define BAGOFWORDS_H_

#include <vector>
#include <string>
#include <opencv2/core/core.hpp>

using namespace std;
using namespace cv;

// feature size
const unsigned int SIFT_SIZE = 128;

class BagOfWords {
public:
	BagOfWords(const unsigned int k);

	virtual ~BagOfWords();

	/**
	 * TODO
	 */
	void createModel(vector<string> directories);

	/**
	 * Labels the image located at <imagePath>, based on the model <clusters>. First the features of the
	 * image get extracted, next a histogram of the visual words is computed to obtain the frequencies.
	 * <clusters> has to contain <k> many elements, each with a <FEATURE_SIZE> components.
	 */
	void labelImage(string imagePath);

	/**
	 * Writes a model file named <model>. The first line contains the number of clusters <k> and the feature
	 * dimensions <size>. Each following row represents a cluster. <clusters> has to contain <k> many clusters
	 * with <FEATURE_SIZE> many components.
	 */
	void writeModel();

	/*
	 * Reads a model file located at <modelPath>. The first line of the model contains the number of clusters k
	 * and the feature dimension dim. The rest of the file contains <k> rows, the clusters, with <dim> many float
	 * values, the cluster coordinates.
	 */
	void readModel(string modelPath);

	/**
	 * Set the mode to GPU (1) or CPU (0);.
	 */
	void setMode(unsigned int mode);

	/**
	 * Set the feature size, default 128 for SIFT.
	 */
	void setSize(unsigned int size);

private:
	// CPU or GPU mode
	unsigned int mode;

	// number of clusters
	unsigned int k;

	// number of feature components
	unsigned int size;

	// number of features of the model
	unsigned long count;

	// true if a model is present
	bool initialized;

	// the model
	float** clusters;

	// membership of features to clusters
	int* membership;

	/**
	 * Helper function that returns a vector of all file names in <directory>.
	 */
	vector<string> readDir(string directory);

	/**
	 * Extracts all SIFT features from each image in <imagePaths> and returns them as a single Matrix of
	 * size <numberOfFeatures> * 128 (SIFT dimension).
	 */
	Mat extractFeatures(vector<string> imagePaths);
};

#endif /* BAGOFWORDS_H_ */
