/*
 * histo_kernel.cu
 */

#include <cuda_runtime_api.h>
#include <iostream>
#include <stdio.h>

#include "util.h"

using namespace std;

// declarations

// Number of Threads per Block
int NumberOfThreadsPerBlock = 1024;

/*
 * The histogram kernel computes the index of the nearest cluster of <clusters> for each feature of <features> on the
 * GPU. The value of <histogram> at the computed index is incremented. <bins> is the number of bins of the histogram,
 * <count> is the number of features and <size> the dimension of each feature / cluster.
 */
__global__ void histo_kernel(float *features, float *clusters, unsigned int *histo, const unsigned int bins,
		const long count, const unsigned int size);

/*
 * Computes the euclidean distance between <point1> and <point2> on the GPU and returns it as a float. Both points
 * must have <size> many dimensions.
 */
__device__ float euclidean_distance_2_gpu(float *point1, float *point2, const unsigned int size);

/**
 * Computes the index of nearest cluster of <clusters> to <point> on the GPU. Both the clusters and the point must have
 * <size> many dimensions. <clusters> is an array of size <k>. To obtain the nearest cluster, the euclidean distance is
 * measured.
 */
__device__ int nearest_cluster_gpu(float *point, const unsigned int size, float *clusters, const unsigned int k);

// function definitions

void histo_gpu(float **features, float **clusters, float *histo, const unsigned int k, const long count,
		const unsigned int size) {
	int numberOfBlocksInGrid = count / NumberOfThreadsPerBlock;
	dim3 dimGrid(numberOfBlocksInGrid, 1);
	dim3 dimBlock(NumberOfThreadsPerBlock, 1);

	// Use an integer histo to sum the total counts for each bin
	unsigned int *intHisto = (unsigned int*) new unsigned int[k];

	// Arrays on the GPU
	unsigned int *deviceHisto = 0;
	float *deviceFeatures = 0;
	float *deviceClusters = 0;

	// Calculate memory needed for data on the GPU
	size_t featureMem = sizeof(float) * count * size;
	size_t clusterMem = sizeof(float) * k * size;
	size_t histoMem = sizeof(unsigned int) * k;
	size_t sharedMem = sizeof(float) * k;

	// Convert the two dimensional clusters and features array to a one dimensional array.
	float *featureArray = (float *) new float[count * size];
	float *clusterArray = (float *) new float[k * size];

	for (int i = 0; i < count; i++) {
		for (int j = 0; j < size; j++)
			featureArray[i * size + j] = features[i][j];
	}

	for (int i = 0; i < k; i++) {
		for (int j = 0; j < size; j++)
			clusterArray[i * size + j] = clusters[i][j];
	}

	// Assure at least one block in the grid
	if (numberOfBlocksInGrid < 1) {
		numberOfBlocksInGrid = 1;
	}

	// Assume we have a cuda device...
	cudaSetDevice(0);

	// Allocate memory on the GPU and copy data. Important: Set deviceHisto to all 0!
	cudaMalloc((void**) &deviceFeatures, featureMem);
	cudaMalloc((void**) &deviceClusters, clusterMem);
	cudaMalloc((void**) &deviceHisto, histoMem);
	cudaMemcpy(deviceFeatures, featureArray, featureMem, cudaMemcpyHostToDevice);
	cudaMemcpy(deviceClusters, clusterArray, clusterMem, cudaMemcpyHostToDevice);
	cudaMemset(deviceHisto, 0, histoMem);

	// TODO: DEBUG
	//cout << "Starting histogram kernel" << endl;
	//cout << "  Blocks: " << numberOfBlocksInGrid << endl;
	//cout << "  Threads: " << NumberOfThreadsPerBlock << endl;

	// kernel invocation
	histo_kernel<<<numberOfBlocksInGrid, dimBlock, sharedMem>>>(deviceFeatures, deviceClusters, deviceHisto, k, count,
			size);
	checkCUDAError("createHistogram - Kernel");
	cudaDeviceSynchronize();

	// Copy results from device to host
	cudaMemcpy(intHisto, deviceHisto, histoMem, cudaMemcpyDeviceToHost);

	// Compute frequencies as floats from the total counts in intHisto
	for (int i = 0; i < k; i++) {
		histo[i] = ((float) intHisto[i]) / count;
	}

	// Free everything
	cudaFree((void*) deviceFeatures);
	cudaFree((void*) deviceClusters);
	cudaFree((void*) deviceHisto);
	free(featureArray);
	free(clusterArray);
	free(intHisto);

	deviceFeatures = 0;
	deviceClusters = 0;
	deviceHisto = 0;
	featureArray = 0;
	clusterArray = 0;
	intHisto = 0;
}

__global__ void histo_kernel(float *features, float *clusters, unsigned int *histo, unsigned int bins, long count,
		unsigned int size) {
	// Allocate shared memory per block for private histogram values
	extern __shared__ int sharedMemory[];
	unsigned int *histo_private = (unsigned int *) sharedMemory;

	if (threadIdx.x < bins) {
		histo_private[threadIdx.x] = 0;
	}

	__syncthreads();

	// Compute global index and offset (size of block)
	unsigned int i = threadIdx.x + blockIdx.x * blockDim.x;
	unsigned int stride = blockDim.x * gridDim.x;

	// Compute for the nearsest cluster and therefore the nearest bin and increment the
	// corresponding bin in the private histogram
	while (i < count) {
		float *feature = &features[i * size];
		int bin = nearest_cluster_gpu(feature, size, clusters, bins);
		atomicAdd(&(histo_private[bin]), 1);
		i += stride;
	}

	__syncthreads();

	// The first bin threads are the "master" threads and accumulate the private values
	// of the shared histograms into the global
	if (threadIdx.x < bins) {
		atomicAdd(&(histo[threadIdx.x]), histo_private[threadIdx.x]);
	}
}

__device__ float euclidean_distance_2_gpu(float *point1, float *point2, const unsigned int size) {
	float dist = 0.f;

	for (int i = 0; i < size; i++) {
		dist += (point1[i] - point2[i]) * (point1[i] - point2[i]);
	}
	return dist;
}

__device__ int nearest_cluster_gpu(float *point, const unsigned int size, float *clusters, const unsigned int k) {
	int index = 0;
	float minDist = euclidean_distance_2_gpu(point, clusters, size);

	for (int i = 1; i < k; i++) {
		float dist = euclidean_distance_2_gpu(point, &clusters[i * size], size);

		if (dist < minDist) { // square root?
			minDist = dist;
			index = i;
		}
	}
	return index;
}
