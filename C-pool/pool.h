
#ifndef __POOL_HEADER
#define __POOL_HEADER
#include<stdlib.h>
#include<stdio.h>
#include<stdbool.h>
#include<string.h>
#include"queue.h"
#include"linked_list.h"
#include"array_list.h"
typedef struct _Operation {
	int (*op)(int, int);
	int a;
	int b;
} Operation;
typedef struct _Args
{
	Operation* operation;
	bool is_complete;
} Args;

void* compute(void*);
bool read_operation(char*, Queue*);

ArrayList* execute_thread_pool(char*, int);
void print_sorted(ArrayList*);


#endif