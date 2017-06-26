/*
 * kmeans_cpu.h
 */

#ifndef KMEANS_CPU_H_
#define KMEANS_CPU_H_

/**
 * Perform a kmeans clustering for all objects in <points>. <points> contains <count> many objects. The result
 * will be stored in <clusters>, where <k> is the number of clusters. Each object and cluster has <size> many
 * dimensions. The index of each cluster an object belongs to will be stored in <membership>.
 */
void kmeans_cpu(float **points, float **clusters, int *membership, const long count, const unsigned int size,
		const unsigned int k);

int nearest_cluster_cpu(float *point, const unsigned int size, float **clusters, const unsigned int k);

#endif /* KMEANS_CPU_H_ */
