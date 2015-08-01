#include<stdlib.h>
#include<stdio.h>
#include<math.h>
#include<time.h>
#include <stdint.h>
#include<string.h>
#include "mpi.h"
#include <omp.h>
#include"ziggurat.h"
#include"rnd.h"
#include"ziggurat.c"
#include"rnd.c"

int partition(float bucket[],int low, int high);
int mergesort(float bucket[],int low, int mid, int high);
int bucket(float *arr, int size, int max_number);

int rank, Node_size;

int main(int argc, char *argv[])
{
	int i,range,j,bukrow;
	int low, high;
	int size, max_number;
	int Node_size,rank;
	clock_t begin, end;

	if(strcmp("-t",argv[1])!=0)
	{
		printf("Error\n");
		return 0;
	}
	size = atoi(argv[2]);
	max_number = atoi(argv[2]);
	static float arr[1000000];
	static float bucket[20][1000000];
	for(i = 0;i<20;i++)
	{
		for(j = 0; j<size;j++)
			bucket[i][j] = 0;
	}
	random_number_generator_simple(arr, size, max_number);
	begin = clock();
	MPI_Init( &argc, &argv );
	MPI_Comm_size( MPI_COMM_WORLD, &Node_size );
	MPI_Comm_rank( MPI_COMM_WORLD, &rank );

	range = 1+floor(size/Node_size);
#pragma omp parallel shared(bucket) private(i)
	{
#pragma omp for
		for(i=0, j = 0;j < size && i<size ; i++)
		{
			if(floor(arr[i]/range) == rank)
			{
				bucket[rank][j] = arr[i];
				j++;
			}
		}
	}
	low = 0;
	high = j-1;
	if(high <= 0)// || high != 1)
	{

	}
	else
		partition(bucket[rank],low,high);

	end = clock();
	printf("\n");
	printf("\n");
	if (rank == 0)
		printf("The time is: %f\n", (double)(end - begin) / (CLOCKS_PER_SEC));
	MPI_Finalize();
	return 0;
}
int partition(float bucket[],int low, int high)
{
	int mid;

	if(low<high)
	{
		mid=(low+high)/2;
		partition(bucket, low, mid);
		partition(bucket, mid+1, high);
		mergesort(bucket, low, mid, high);
	}
}

int mergesort(float bucket[],int low, int mid, int high)
{

	int i = 0;
	int low1 = low;
	int low2 = mid+1;
	int j = 0;
	float temp[high];

	while(low1 <= mid && low2 <= high)
	{
		if(bucket[low1]<= bucket[low2])		//Left section
		{
			temp[i]= bucket[low1];
			i++; low1++;
		}
		else								//Right section
		{
			temp[i]= bucket[low2];
			i++; low2++;
		}
	}
	while(low1<= mid)
	{
		temp[i] = bucket[low1];
		i++; low1++;
	}
	while(low2<=high)
	{
		temp[i] = bucket[low2];
		i++; low2++;
	}
	for(j = 0; j< i;j++)		// Merging all values together
		bucket[j+low] = temp[j];

}
