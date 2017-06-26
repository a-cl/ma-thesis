#include <iostream>
#include <iomanip>
#include <cstdlib>

#include "util.h"

using namespace std;

// definitions

float euclidean_distance_cpu(float *point1, float *point2, const unsigned int size);

// functions

float euclidean_distance_cpu(float *point1, float *point2, const unsigned int size) {
	float dist = 0.f;

	for (int i = 0; i < size; i++) {
		dist += (point1[i] - point2[i]) * (point1[i] - point2[i]);
	}
	return dist;
}

int nearest_cluster_cpu(float *point, const unsigned int size, float **clusters, const unsigned int k) {
	int index = 0;
	float minDist = euclidean_distance_cpu(point, clusters[0], size);

	for (int i = 1; i < k; i++) {
		float dist = euclidean_distance_cpu(point, clusters[i], size);

		if (dist < minDist) { // square root?
			minDist = dist;
			index = i;
		}
	}
	return index;
}

void kmeans_cpu(float **points, float **clusters, int *membership, const long count, const unsigned int size,
		const unsigned int k) {
	float treshold = 0.0002;
	float delta = 0.0;
	int loop = 0;

	int *newClusterSize = (int*) calloc(k, sizeof(int));
	float **newClusters = malloc2D(k, size);

	double startTime = time();

	// initial clusters are the first points
	for (int i = 0; i < k; i++) {
		for (int j = 0; j < size; j++) {
			clusters[i][j] = points[i][j];
		}
	}

	// No point is assigned by now
	for (int i = 0; i < count; i++) {
		membership[i] = -1;
	}

	do {
		delta = 0.0;

		// Compute the nearest cluster for each point, update the newClusters and
		// assign membership. The delta increases for each changed membership by 1.
		for (int i = 0; i < count; i++) {
			int index = nearest_cluster_cpu(points[i], size, clusters, k);

			if (membership[i] != index) {
				delta += 1.0;
			}

			membership[i] = index;

			newClusterSize[index]++;
			for (int j = 0; j < size; j++) {
				newClusters[index][j] += points[i][j];
			}
		}

		// Adjust the clusters based on the changes in newClusters. Reset newClusters
		// afterwards for the next iteration.
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < size; j++) {
				if (newClusterSize[i] > 0) {
					clusters[i][j] = newClusters[i][j] / newClusterSize[i];
				}
				newClusters[i][j] = 0.0;
			}
			newClusterSize[i] = 0;
		}

		// The difference is the percentage of objects changed (from all objects)
		delta /= count;
	} while (delta > treshold && loop++ < 500);

	double endTime = time() - startTime;
	cout << "Time: " << endTime << " with " << loop << " iterations (";
	cout << std::setprecision(2) << (delta * 100) << "% changed)" << endl;

	free(newClusters[0]);
	free(newClusters);
	free(newClusterSize);
}
