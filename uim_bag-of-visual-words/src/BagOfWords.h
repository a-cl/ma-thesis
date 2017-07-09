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

#include "Test.h"

using namespace std;
using namespace cv;

// feature size
const unsigned int SIFT_SIZE = 128;

class BagOfWords {
public:
	BagOfWords(string path, const unsigned int k);

	virtual ~BagOfWords();

	/**
	 * Creates a new model based in the training data located at this <path> + train.txt. The model will k
	 * many clusters.
	 */
	void createModel();

	/**
	 * Writes a model file to this <path> + model. The first line contains the number of clusters k and the
	 * feature dimensions size. Each following row represents a cluster. clusters has to contain k many clusters
	 * with SIFT_SIZE many components.
	 */
	void writeModel();

	/*
	 * Reads a model file located at this <path> + model. The first line of the model contains the number of
	 * clusters k and the feature dimension dim. The rest of the file contains k rows, the clusters, with dim
	 * many float values, the cluster coordinates.
	 */
	void readModel();

	/**
	 * Reads all tests specified at this <path> + tests.txt and executes them against this model. The results
	 * will be saved to disk afterwards.
	 */
	void runTests ();

	/**
	 * Set k to a new value. This does not effect the current model, but the test run on the current model.
	 */
	void setK(unsigned int k);

	/**
	 * Set the mode to GPU (1) or CPU (0);.
	 */
	void setMode(unsigned int mode);

	/**
	 * Set the feature size, default 128 for SIFT.
	 */
	void setSize(unsigned int size);

private:
	// path to test and train data
	string path;

	// CPU or GPU mode
	unsigned int mode;

	// number of clusters
	unsigned int k;

	// number of feature components
	unsigned int size;

	// number of features of the model
	unsigned long count;

	// true if a model is present
	bool modelInitialized;

	// the model
	float** clusters;

	// membership of features to clusters
	int* membership;

	// Optional: Tests to execute
	vector<Test> tests;

	/**
	 * Extracts all SIFT features from the image located at <imagePath> and returns them as a Matrix of
	 * size <numberOfFeatures> * 128 (SIFT dimension).
	 */
	Mat extractFeature(string imagePath);

	/**
	 * Labels the image located at <imagePath>, based on the model <clusters>. First the features of the
	 * image get extracted, next a histogram of the visual words is computed to obtain the frequencies.
	 * <clusters> has to contain <k> many elements, each with a <FEATURE_SIZE> components.
	 */
	float* computeVisualWords(string imagePath);

	/**
	 * Reads in the test data specified at <path> + tests.txt. The file is in the format:
	 * <path_to_image1> <path_to_image2> <sameClass>
	 *
	 * where <sameClass> is a "+" to indicate a same and "-" to indicate a different class.
	 */
	void readTestData ();

	/**
	 * Executes the test initialized by readTestData. Stores the results in the internal tests vector.
	 */
	void executeTests ();

	/**
	 * Writes the tests contained in the internal test vector to this <path> + tests<k>.txt where <k> is
	 * the number of clusters of this bag of visual words.
	 */
	void writeTestResults ();

	/**
	 * Calculates the mean squared error from <histo1> and <histo2> and returns the result as a float.
	 */
	float calculateSimilarity (float *histo1, float *histo2);
};

#endif /* BAGOFWORDS_H_ */
