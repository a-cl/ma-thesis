/*
 * histo_cpu.cu
 */

#include <iostream>
#include <stdio.h>

#include "kmeans_cpu.h"

using namespace std;

// function definitions

void histo_cpu(float **features, float** clusters, float* histo, const unsigned int k, const long count,
		const unsigned int size) {
	for (int i = 0; i < count; i++) {
		float *descriptor = features[i];
		int index = nearest_cluster_cpu(descriptor, size, clusters, k);
		histo[index] = histo[index] + 1;
	}

	for (int i = 0; i < k; i++) {
		histo[i] = histo[i] / count;
	}
}
