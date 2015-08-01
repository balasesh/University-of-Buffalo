#include <cuda.h>
#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<time.h>
#include<stdint.h>
#include<string.h>
#include"ziggurat.h"
#include"rnd.h"
#include"ziggurat.c"
#include"rnd.c"

#define NUM_THREADS 8

__global__ void sortcuda( float *arr, int *num, float *bucket)
{
	int size = *num;
	int tid = threadIdx.x;
	int range,i;
	int count = 0;
	int localcount;
	int val = tid*size;
	
	range = (1+(*num/NUM_THREADS));
	
	for(i=0 ;i<*num ; i++)
	{
		if(floor(arr[i]/range) == tid)
		{
			localcount = count;
			while(localcount > 0 && bucket[val+localcount -1] > arr[i] )
			{
				bucket[val+localcount] = bucket[val+localcount -1];
				localcount--;
			}
			bucket[val+localcount]=arr[i];
			count++;
		}
	}
}

int main(int argc, char **argv)
{
	clock_t begin, end;
	begin = clock();
	if(strcmp("-t",argv[1])!=0)
	{
		printf("Error\n");
		return 0;
	}
	int num,max_number;
	//int j;float temp;
	int i;
	num = atoi(argv[2]);
	max_number = atoi(argv[2]);
	float bucket[num*NUM_THREADS];
	float arr[num];
	float arr1[num];
	
	for(i = 0;i<num;i++)
	{
		bucket[i] = 0;
	}

	float *dev_bucket;
	float *dev_arr;
	int *dev_num;

	random_number_generator_simple(arr, num, max_number);
	random_number_generator_simple(arr1, num, max_number);	

	cudaMalloc ((void**)&dev_arr, num*sizeof(float));
	cudaMalloc ((void**)&dev_bucket, num*NUM_THREADS*sizeof(float));
	cudaMalloc ((void**)&dev_num, sizeof (int));
	
	
	cudaMemcpy( dev_arr, arr, num*sizeof(float), cudaMemcpyHostToDevice);
	cudaMemcpy( dev_bucket, bucket, num*NUM_THREADS* sizeof(float), cudaMemcpyHostToDevice);
	cudaMemcpy( dev_num, &num, sizeof(int), cudaMemcpyHostToDevice);
	
	sortcuda<<<1,NUM_THREADS>>>(dev_arr,dev_num,dev_bucket);
	
	cudaMemcpy( bucket, dev_bucket, num*NUM_THREADS* sizeof(float), cudaMemcpyDeviceToHost);
	
	end = clock();
	printf("\nAll Values Sorted\n");
	printf("The time is: %f\n", (double)(end - begin) / CLOCKS_PER_SEC);

	cudaFree( dev_num );
	cudaFree( dev_arr );
	cudaFree( dev_bucket );
	return 0;
}
