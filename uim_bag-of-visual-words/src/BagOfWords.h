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
#include <opencv2/opencv.hpp>

#include "Test.h"

using namespace std;
using namespace cv;

// feature size
const unsigned int FEATURE_SIZE = 128;

class BagOfWords {
public:
	BagOfWords(const unsigned int k);

	virtual ~BagOfWords();

	/**
	 * Creates a new model based on the training data located at <modelPath>.
	 */
	void createModel(const string modelPath);

	/**
	 * Writes a model file to <modelPath>. The first line contains the number of clusters k and the
	 * feature dimensions size. Each following row represents a cluster. clusters has to contain k
	 * many clusters with FEATURE_SIZE many components.
	 */
	void writeModel(const string modelPath);

	/*
	 * Reads a model file located at <modelPath>. The first line of the model contains the number of
	 * clusters k and the feature dimension dim. The rest of the file contains k rows, the clusters,
	 * with dim many float values, the cluster centroid's coordinates.
	 */
	void readModel(const string modelPath);

	/**
	 * Reads all tests specified at <testSourcePath> and executes them against this model. The results
	 * will be saved to disk at <testTargetPath> afterwards.
	 */
	void runTests(const string testSourcePath, const string testTargetPath);

	/**
	 * Set k to a new value. This does not effect the current model, but the test run on the current model.
	 */
	void setK(unsigned int k);

	/**
	 * Set the mode to CPU (0) or GPU (1).
	 */
	void setMode(unsigned int mode);

	/**
	 * Set the GPU mode to global (0) or shared (1) memory.
	 */
	void setVariant(unsigned int variant);

	/**
	 * Set the feature size, default 128 for SIFT.
	 */
	void setSize(unsigned int size);

private:
	// CPU or GPU mode
	unsigned int mode;

	// global or shared memory variant
	unsigned int variant;

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
	 * Reads all features from the image located at <featuresPath> and returns them as a Matrix of
	 * size <numberOfFeatures> * FEATURE_SIZE.
	 */
	Mat readFeatures(const string featuresPath);

	/**
	 * Labels the image located at <imagePath>, based on the model <clusters>. First the features of the
	 * image get extracted, next a histogram of the visual words is computed to obtain the frequencies.
	 * <clusters> has to contain <k> many elements, each with a <FEATURE_SIZE> components.
	 */
	float* computeVisualWords(Mat features);

	/**
	 * Reads in the test data specified at <path> + tests.txt. The file is in the format:
	 * <path_to_image1> <path_to_image2> <sameClass>
	 *
	 * where <sameClass> is a "+" to indicate a same and "-" to indicate a different class.
	 */
	void readTestData(const string testPath);

	/**
	 * Executes the test initialized by readTestData. Stores the results in the internal tests vector.
	 */
	void executeTests();

	/**
	 * Writes the tests contained in the internal test vector to <testPath> + tests<k> where <k> is
	 * the number of clusters of this Bag of Visual Words.
	 */
	void writeTestResults(const string testPath);

	/**
	 * Calculates the mean squared error from <histo1> and <histo2> and returns the result as a float.
	 */
	float calculateSimilarity(float *histo1, float *histo2);
};

#endif /* BAGOFWORDS_H_ */
