#include<stdio.h>
#include <stdlib.h>
#include<math.h>
#include <omp.h>
#include<time.h>

int main(int argc, char **argv)
{
	long int num = atoi(argv[1]);
	long int sqrnum, cur = 1, biggest =0;
	int flag=0;
	long int i,j;
	clock_t begin, end;
	begin = clock();
#pragma omp parallel shared (num, biggest,cur) private(i, j,flag, sqrnum)
	{
#pragma omp for reduction( + : cur)
		for (i = 3; i< num; i = i+2)
		{
			flag = 0;
			sqrnum = ceil(sqrt(i));
			j = 0;
			for(j = 3; j<=sqrnum; j = j+2){
				if(i % j == 0)	// Composite Number
				{
					flag = 1;
					break;
				}
			}
			if(flag == 0)
			{
				cur = cur + 1;
				if (i>biggest)
					biggest = i;
			}
		}
	}
	end = clock();
	printf("The Largest prime is %ld\n",biggest);
	printf("The Number of prime numbers are %ld\n", cur);
	printf("The time is: %f\n", (double)(end - begin) / (10 * CLOCKS_PER_SEC));
}
