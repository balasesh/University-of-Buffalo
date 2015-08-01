#include <cuda.h>
#include <stdio.h>
#include <math.h>
 
#define NUM_BLOCKS 1024

__global__ void primecuda( int *dev_num, int *arr) 
{
	int blckid = blockIdx.x;	//Handle the data at the block index
	int tid = threadIdx.x;
	int gid = 1+(*dev_num/1024);
	int bid = (blckid*gid) + tid;
	int flag, i;
	flag = 0;
	if(bid <= *dev_num)
	{
		if(bid % 2 != 0)			
		{
			for (i = 3 ; i<= sqrtf(bid); i++)
			{
				if(bid % i == 0)
				{
					flag = 1;
					break;
				}
			}
			if (flag == 0)
			{
				if (bid == 0 || bid == 1 || bid == 2)
				{
					//Do nothing
				}
				else
				{
					arr[bid] = bid;
				}
			}
		}
	}		
}

int main(int argc, char **argv)
{
	if(strcmp("-t",argv[1])!=0)
	{
		printf("Error\n");
		return 0;
	}
	
	int *dev_num;
	int num,count,biggest;
	num = atoi(argv[2]);
	int i;
	int *dev_arr;
	int arr[num];
	int NUM_THREADS;
	clock_t begin, end;
	
	begin = clock();
	
	for(i = 0;i<num;i++)
	{
		arr[i] = 0;
		
	}
	NUM_THREADS = 1+(num/NUM_BLOCKS);
	count = 1;
	biggest = 2;
	
	cudaMalloc ( (void**)&dev_num, sizeof (int) );
	cudaMalloc ( (void**)&dev_arr, num * sizeof (int) );
	
	cudaMemcpy( dev_num, &num, sizeof(int), cudaMemcpyHostToDevice);
	cudaMemcpy( dev_arr, arr, num * sizeof(int), cudaMemcpyHostToDevice);
	
	primecuda<<<NUM_BLOCKS, NUM_THREADS>>> (dev_num, dev_arr);
	
	cudaMemcpy( arr, dev_arr, num * sizeof(int), cudaMemcpyDeviceToHost);
	
	
	
	for(i = 0; i<num;i++)
	{
		if(arr[i]> 0)
		{
			//printf("%d	",arr[i]);
			count++;
			if(arr[i]>biggest)
				biggest = arr[i];
		}
	}
	end = clock();
	printf("The largest prime number is: %d\n",biggest);
	printf("The number of prime numbers are: %d\n",count);
	printf("The time is: %f\n", (double)(end - begin) / CLOCKS_PER_SEC);
	
	cudaFree( dev_num );
	cudaFree( dev_arr );
	
	return 0;
}