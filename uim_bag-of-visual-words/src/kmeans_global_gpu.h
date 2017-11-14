/*
 * kmeans_gpu.h
 */

#ifndef KMEANS_GLOBAL_GPU_H_
#define KMEANS_GLOBAL_GPU_H_

/*
 * Perform a kmeans clustering for all objects in <points>. <points> contains <count> many objects. The result
 * will be stored in <clusters>, where <k> is the number of clusters. Each object and cluster has <size> many
 * dimensions. The index of each cluster an object belongs to will be stored in <membership>.
 */
float** kmeans_global_gpu(float **points, float **clusters, int *membership, const long count, const unsigned int size,
		const unsigned int k);

#endif /* KMEANS_GLOBAL_GPU_H_ */
