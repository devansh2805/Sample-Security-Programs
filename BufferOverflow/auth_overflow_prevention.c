#include<stdio.h>
#include<stdlib.h>
#include<string.h>

void main() {
	char pass_buffer[11];
	int flag = 0;
	printf("Enter Password: ");
	fgets(pass_buffer, 11, stdin);
	if(strcmp(pass_buffer, "helloworld") == 0) {
		printf("Password Correct\n");
		flag = 1;
	} else {
		printf("Password Incorrect\n");
	}
	if(flag) {
		printf("Root Privileges Granted\n");
	}	
}