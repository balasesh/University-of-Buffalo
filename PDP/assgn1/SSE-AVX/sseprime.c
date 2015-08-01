#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <xmmintrin.h>
#include <emmintrin.h>
#include <pmmintrin.h>
#include <smmintrin.h>
#include <nmmintrin.h>
#include <time.h>

int main(int argc, char** argv)
{
	long int i,j, sqrnum;
	long int num = atoi(argv[1]);
	__m128 X,Y,rem; 	//128-bit values
	float temp[4];
	__m128 acc = _mm_set_ps(0,0,0,0);
	long int biggest = 2;
	long int count = 1;
	int flag = 0;
	clock_t begin, end;

	begin = clock();
	for(i = 3 ; i<=num ; i=i+2)
	{
		flag = 0;
		sqrnum = ceil(sqrt(i));
		if(i<=16777216)
		{
			for (j = 3; j+8<=sqrnum; j = j+8)
			{
				X = _mm_set_ps(i,i,i,i);
				Y = _mm_set_ps(j,j+2,j+4,j+6);

				rem = _mm_sub_ps(X,_mm_mul_ps(Y , _mm_floor_ps(_mm_div_ps(X,Y))));
				_mm_store_ps(&temp[0], rem);
				if(temp[0]== 0 || temp[1] == 0 || temp[2] == 0 || temp[3] == 0)
				{
					flag = 1;
					//printf("Not Prime is : %d\n", i);
					break;
				}
			}
			for(j = 3; j<=sqrnum; j = j+2)
			{
				if(i % j == 0)
				{
					flag = 1;
					//printf("Not Prime is : %d\n", i);
					break;
				}
			}
		}
		else		// i>16777216
		{
			for(j = 3; j<=sqrnum; j = j+2)
			{
				if(i % j == 0)
				{
					flag = 1;
					break;
				}
			}
		}

		if (flag == 0)
		{
			count++;
			if(i > biggest)
				biggest = i;

			//printf("Prime is : %d\n", i);
		}
	}

	end = clock();
	printf("The time is: %f\n", (double)(end - begin) / CLOCKS_PER_SEC);
	printf("The number of prime number are %ld\n", count);
	printf("The Biggest prime is %ld\n",biggest);

}
