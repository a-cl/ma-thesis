NVCC = nvcc
CPPFLAGS =
CUFLAGS = -arch=compute_11 -code=sm_11
LDFLAGS =

BINS = cuda_test_kernel

all: $(BINS)

cuda_test_kernel: main.o kernel.o
	$(NVCC) $(LDFLAGS) $^ -o $@

%.o: %.cpp
	$(NVCC) $(CPPFLAGS) -c $< -o $@

%.o: %.cu
	$(NVCC) $(CUFLAGS) -c $< -o $@

cleano:
	rm -f *.o

clean: cleano
	rm -f $(BINS)
