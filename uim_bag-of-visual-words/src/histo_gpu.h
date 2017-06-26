/*
 * histo_gpu.h
 */

#ifndef HISTO_GPU_H_
#define HISTO_GPU_H_

void histo_gpu(float **features, float **clusters, float *histo, const unsigned int k, const long count,
		const unsigned int size);

#endif /* HISTO_GPU_H_ */
