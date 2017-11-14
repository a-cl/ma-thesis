/*
 * kmeans_gpu.cu
 */

#include <cuda_runtime_api.h>
#include <iostream>
#include <iomanip>
#include <stdio.h>

#include "util.h"

using namespace std;

// declarations

static int ThreadsPerBlock = 512;

static inline int nextPowerOfTwo(int n);

__device__ inline static float euclid_dist_2(const unsigned int size, const long count, const unsigned int k,
		float *points, float *clusters, int oId, int cId);

__global__ void compute_delta(int *deviceIntermediates, int numIntermediates, int numIntermediates2);

__global__ void nearest_cluster(const unsigned int size, const long count, const unsigned int k, float *points,
		float *deviceClusters, int *membership, int *intermediates);

// function definitions

static inline int nextPowerOfTwo(int n) {
	n--;

	n = n >> 1 | n;
	n = n >> 2 | n;
	n = n >> 4 | n;
	n = n >> 8 | n;
	n = n >> 16 | n;
//  n = n >> 32 | n

	return ++n;
}

__device__ inline static
float euclid_dist_2(const unsigned int size, const long count, const unsigned int k, float *points, float *clusters,
		int oId, int cId) {
	float ans = 0.0;

	for (int i = 0; i < size; i++) {
		ans += (points[count * i + oId] - clusters[k * i + cId]) * (points[count * i + oId] - clusters[k * i + cId]);
	}

	return ans;
}

__global__
void compute_delta(int *deviceIntermediates, int numIntermediates, int numIntermediates2) {
	extern __shared__ unsigned int intermediates[];
	intermediates[threadIdx.x] = (threadIdx.x < numIntermediates) ? deviceIntermediates[threadIdx.x] : 0;

	__syncthreads();

	for (unsigned int s = numIntermediates2 / 2; s > 0; s >>= 1) {
		if (threadIdx.x < s) {
			intermediates[threadIdx.x] += intermediates[threadIdx.x + s];
		}
		__syncthreads();
	}

	if (threadIdx.x == 0) {
		deviceIntermediates[0] = intermediates[0];
	}
}

__global__ void nearest_cluster(const unsigned int size, const long count, const unsigned int k, float *points,
		float *deviceClusters, int *membership, int *intermediates) {
	extern __shared__ char sharedMemory[];
	unsigned char *membershipChanged = (unsigned char *) sharedMemory;
	int objectId = blockDim.x * blockIdx.x + threadIdx.x;
	float *clusters = deviceClusters;

	membershipChanged[threadIdx.x] = 0;

	if (objectId < count) {
		int index = 0;
		float dist;
		float min_dist = euclid_dist_2(size, count, k, points, clusters, objectId, 0);

		for (int i = 1; i < k; i++) {
			dist = euclid_dist_2(size, count, k, points, clusters, objectId, i);

			if (dist < min_dist) {
				min_dist = dist;
				index = i;
			}
		}

		if (membership[objectId] != index) {
			membershipChanged[threadIdx.x] = 1;
		}

		membership[objectId] = index;

		__syncthreads();    //  For membershipChanged[]

		for (unsigned int s = blockDim.x / 2; s > 0; s >>= 1) {
			if (threadIdx.x < s) {
				membershipChanged[threadIdx.x] += membershipChanged[threadIdx.x + s];
			}
			__syncthreads();
		}

		if (threadIdx.x == 0) {
			intermediates[blockIdx.x] = membershipChanged[0];
		}
	}
}

void kmeans_global_gpu(float **points, float ** clusters, int *membership, const long count, const unsigned int size,
		const unsigned int k) {
	int index, loop = 0;
	int *newClusterSize = (int*) calloc(k, sizeof(int));
	float delta;
	float threshold = 0.0002;
	float **dimObjects = malloc2D(size, count);
	float **dimClusters = malloc2D(size, k);
	float **newClusters = malloc2D(size, k);

	float *deviceObjects;
	float *deviceClusters;
	int *deviceMembership;
	int *deviceIntermediates;

	double startTime = time();

	cudaSetDevice(0);

	for (int i = 0; i < size; i++) {
		for (int j = 0; j < count; j++)
			dimObjects[i][j] = points[j][i];
		for (int j = 0; j < k; j++)
			dimClusters[i][j] = dimObjects[i][j];
	}

	for (int i = 0; i < count; i++) {
		membership[i] = -1;
	}

	memset(newClusters[0], 0, size * k * sizeof(float));

	const unsigned int numBlocks = (count + ThreadsPerBlock - 1) / ThreadsPerBlock;
	const unsigned int clusterBlockSharedDataSize = numBlocks * sizeof(unsigned char);

	cout << "  Blocks: " << numBlocks << endl;
	cout << "  Threads: " << ThreadsPerBlock << endl;

	const unsigned int numReductionThreads = nextPowerOfTwo(numBlocks);
	const unsigned int reductionBlockSharedDataSize = numReductionThreads * sizeof(unsigned int);

	cudaMalloc((void **) &deviceClusters, k * size * sizeof(float));
	cudaMalloc((void **) &deviceObjects, count * size * sizeof(float));
	cudaMalloc((void **) &deviceMembership, count * sizeof(int));
	cudaMalloc((void **) &deviceIntermediates, numReductionThreads * sizeof(unsigned int));

	cudaMemcpy(deviceObjects, dimObjects[0], count * size * sizeof(float), cudaMemcpyHostToDevice);
	cudaMemcpy(deviceMembership, membership, count * sizeof(int), cudaMemcpyHostToDevice);

	do {
		cudaMemcpy(deviceClusters, dimClusters[0], k * size * sizeof(float), cudaMemcpyHostToDevice);

		nearest_cluster<<<numBlocks, ThreadsPerBlock, clusterBlockSharedDataSize>>>(size, count, k, deviceObjects,
				deviceClusters, deviceMembership, deviceIntermediates);

		compute_delta<<<1, numReductionThreads, reductionBlockSharedDataSize>>>(deviceIntermediates, numBlocks,
				numReductionThreads);

		cudaDeviceSynchronize();

		cudaMemcpy(membership, deviceMembership, count * sizeof(int), cudaMemcpyDeviceToHost);

		for (int i = 0; i < count; i++) {
			index = membership[i];

			newClusterSize[index]++;
			for (int j = 0; j < size; j++) {
				newClusters[j][index] += points[i][j];
			}
		}

		for (int i = 0; i < k; i++) {
			for (int j = 0; j < size; j++) {
				if (newClusterSize[i] > 0) {
					dimClusters[j][i] = newClusters[j][i] / newClusterSize[i];
				}
				newClusters[j][i] = 0.0;
			}
			newClusterSize[i] = 0;
		}

		int d;
		cudaMemcpy(&d, deviceIntermediates, sizeof(int), cudaMemcpyDeviceToHost);
		delta = (float) d / count;
	} while (delta > threshold && loop++ < 500);

	for (int i = 0; i < k; i++) {
		for (int j = 0; j < size; j++) {
			clusters[i][j] = dimClusters[j][i];
		}
	}

	double endTime = time() - startTime;
	cout << "Time: " << endTime << " with " << loop << " iterations (";
	cout << std::setprecision(2) << (delta * 100) << "% changed)" << endl;

	cudaFree(deviceObjects);
	cudaFree(deviceClusters);
	cudaFree(deviceMembership);
	cudaFree(deviceIntermediates);

	free(dimObjects[0]);
	free(dimClusters[0]);
	free(newClusters[0]);

	free(dimObjects);
	free(dimClusters);
	free(newClusters);
	free(newClusterSize);
}
