#include<stdio.h>
#include<math.h>
#include <time.h>

int main(int argc, char **argv)
{
	if(strcmp("-t",argv[1])!=0)
	{
		printf("Error\n");
		return 0;
	}

	long int num = atoi(argv[2]);
	long int sqrnum, cur, biggest =0;
	int flag=0;
	long int i,j;
	clock_t begin, end;
	cur = 1;
	begin = clock();
	for (i = 3; i<= num; i = i+2)
	{
		flag = 0;
		sqrnum = ceil(sqrt(i));
		for(j = 3; j<=sqrnum; j = j+2){
			if(i % j == 0)	// Composite Number
			{
				flag = 1;
				break;
			}
		}
		if(flag == 0)
		{
			cur++;
			if (i>biggest)
				biggest = i;
		}
	}
	end = clock();
	printf("The Largest prime is %ld\n",biggest);
	printf("The Number of prime numbers are %ld\n", cur);
	printf("The time is: %f\n", (double)(end - begin) / CLOCKS_PER_SEC);
}
