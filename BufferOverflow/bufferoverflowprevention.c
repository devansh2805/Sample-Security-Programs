#include<stdio.h>
#include<stdlib.h>
#include<string.h>

int main() {
	char *point;
	char *danger_point;
	point = (char *)malloc(5*sizeof(char));
	danger_point = (char *)malloc(5*sizeof(char));
	printf("Address of pointer point: %u\n", point);
	printf("Address of pointer danger_point: %u\n", danger_point);
	printf("Enter String 5 Chars: ");
	fgets(point, 5, stdin);
	system(danger_point);
	return 0;
}