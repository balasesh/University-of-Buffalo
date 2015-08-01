#include<stdlib.h>
#include<stdio.h>
#include<math.h>
#include<time.h>
#include <stdint.h>

#include"ziggurat.h"
#include"rnd.h"
#include"ziggurat.c"
#include"rnd.c"

int partition(float bucket[],int low, int high);
int mergesort(float bucket[],int low, int mid, int high);
int bucket(float *arr, int size, int max_number);

int main(int argc, char *argv[])
{
	int i,size, max_number;
	if(strcmp("-t",argv[1])!=0)
	{
		printf("Error\n");
		return 0;
	}
	size = atoi(argv[2]);
	max_number = atoi(argv[2]);
	float arr[size];
	random_number_generator_simple(arr, size, max_number);
	bucket(arr, size, max_number);
}

bucket(float *arr, int size, int max_number)
{
	int i,range,j,bukrow;
	int low, high;
	static float bucket[10][1000000];
	clock_t begin, end;
	for(i = 0;i<10;i++)
		for (j = 0;j<1000000;j++ )
			bucket[i][j]=0;
	range = 1+(size/10);
	begin = clock();
	for (i = 0; i< size; i++)
	{
		bukrow = floor(arr[i]/range);
		j = 0;
		while(bucket[bukrow][j]> 0)
		{
			j++;
		}
		bucket[bukrow][j]=arr[i];
	}

	for (i = 0 ;i<10;i++)
	{
		low = 0;
		high = 0;	//Sizeof(bucket[i])
		while(bucket[i][high]>0)
			high++;
		if(high == 0)
		{
			//do nothing
		}
		else{
			partition(bucket[i],low,high-1);
		}
	}
	end = clock();
	printf("The time is: %f\n", (double)(end - begin) / CLOCKS_PER_SEC);

}
partition(float bucket[],int low, int high)
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

mergesort(float bucket[],int low, int mid, int high)
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
