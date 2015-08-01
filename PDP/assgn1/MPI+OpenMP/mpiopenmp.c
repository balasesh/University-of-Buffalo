#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include "mpi.h"
#include <omp.h>

int main( argc, argv )
int  argc;
char **argv;
{
	long int num = atoi(argv[1]);
	long int partnum, biggest,part_big;
	long int count, part_count, sqrnum, i, j, k;
	int flag = 0;
	clock_t begin, end;
	int rank, size;
	biggest = 2;
	part_big = 2;
	count = 1;
	part_count = 0;
	MPI_Init( &argc, &argv );
	MPI_Comm_size( MPI_COMM_WORLD, &size );
	MPI_Comm_rank( MPI_COMM_WORLD, &rank );
	begin = clock();
	partnum = 1+ ceil(num/size);
	for(i = rank*partnum; i < (rank*partnum)+partnum && i<= num ;i = i+ partnum)
	{
#pragma omp parallel shared (partnum, part_big,part_count,i) private(k, j,flag, sqrnum)
		{
#pragma omp for reduction( + : part_count)
			for (k = i ; k< i+ partnum && k <= num; k++)
			{
				if(k % 2 == 0)
					flag = 1;
				else
				{
					sqrnum = ceil(sqrt(k));
					flag = 0;
					for (j = 3 ; j<= sqrnum; j = j+2)
					{

						if(k % j == 0)
						{
							flag = 1;
							break;
						}
					}
				}
				if (flag == 0)
				{
					if (k == 0 || k == 1 || k == 2)
					{
						//Do nothing
					}
					else
					{
						part_count++;
						if(k > part_big)
						{
							part_big = k;
						}
					}
				}
			}
		}
	}
	MPI_Reduce(&part_big,&biggest,1, MPI_LONG,MPI_MAX, 0, MPI_COMM_WORLD);
	MPI_Reduce(&part_count, &count, 1,MPI_LONG, MPI_SUM, 0, MPI_COMM_WORLD );
	end = clock();
	if (rank == 0)
	{
		printf("The largest prime is : %ld\n",  biggest);
		printf("The number of prime numbers are %ld\n", count+1);
		printf("The time is: %f\n", (double)(end - begin) / CLOCKS_PER_SEC);
	}
	MPI_Finalize();
}

