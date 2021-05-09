#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <semaphore.h>
#include <unistd.h>
#include "pool.h"

int add(int a, int b)
{
	return (a) + (b);
}
int mul(int a, int b)
{
	return (a) * (b);
}
int sub(int a, int b)
{
	return a - b;
}

void *compute(void *args)
{
	Args *arg = args;
	int *result = malloc(sizeof(int));
	*result = (arg->operation->op(arg->operation->a, arg->operation->b));
	arg->is_complete = true;
	return result;
}

bool read_operation(char *file_name, Queue *queue)
{
	if (file_name == NULL || queue == NULL)
	{
		return false;
	}
	FILE *f = fopen(file_name, "r");
	if (f == NULL)
	{
		return false;
	}
	char str[50];
	while (fgets(str, 50, f) != NULL)
	{
		int op, a, b;
		sscanf(str, "%d %d %d", &op, &a, &b);
		// printf("%d*%d*%d\n", op, a, b);

		Operation *operation = malloc(sizeof(Operation));
		operation->a = a;
		operation->b = b;
		if (op == 0)
		{
			operation->op = add;
		}
		else if (op == 1)
		{
			operation->op = sub;
		}
		else if (op == 2)
		{
			operation->op = mul;
		}
		else
		{
			operation->op = add;
		}
		queue_enqueue(queue, operation);
	}
	fclose(f);
	return true;
}

ArrayList *execute_thread_pool(char *file_name, int pool_size)
{
	Queue *queue = queue_initialize(sizeof(Operation), "Operation");
	bool res = read_operation(file_name, queue);

	if (!res || pool_size <= 0 || queue_size(queue) <= 0)
	{
		return NULL;
	}
	int task_size = queue_size(queue);
	ArrayList *array_list = alist_initialize(100, sizeof(int), "int");
	pthread_t *threads = malloc(sizeof(pthread_t) * pool_size);
	Args *args = malloc(sizeof(Args) * pool_size);
	int i = 0;
	while (i < pool_size && queue_size(queue) > 0)
	{
		Operation *operation = queue_dequeue(queue);
		args[i].operation = operation;
		args[i].is_complete = false;

		pthread_create(&threads[i], NULL, compute, &args[i]);
		i++;
	}

	if (i < pool_size)
	{
		bool is_finish = false;
		while (!is_finish)
		{
			for (int j = 0; j < i; j++)
			{
				if (args[j].is_complete)
				{
					int *result = NULL;
					pthread_join(threads[j], (void **)&result);

					int *r = malloc(sizeof(int));
					*r = *result;
					alist_add(array_list, r);
					// printf("result = %d\n", (*r));
					if (array_list->size >= task_size)
					{
						is_finish = true;
						break;
					}
				}
			}
		}
	}
	if (queue_size(queue) > 0)
	{
		bool is_finish = false;
		while (!is_finish || queue_size(queue) > 0)
		{
			for (int j = 0; j < pool_size; j++)
			{
				if (args[j].is_complete)
				{
					int *result = NULL;
					pthread_join(threads[j], (void **)&result);
					int *r = malloc(sizeof(int));
					*r = *result;
					alist_add(array_list, r);
					// printf("result = %d\n", (*r));
					if (array_list->size >= task_size)
					{
						is_finish = true;
						break;
					}
					if (queue_size(queue) > 0)
					{
						Operation *operation = queue_dequeue(queue);
						args[j].operation = operation;
						args[j].is_complete = false;
						pthread_create(&threads[j], NULL, compute, &args[j]);
					}
				}
			}
		}
	}
	return array_list;
}

void print_sorted(ArrayList *array_list)
{
	if (array_list == NULL)
	{
		return;
	}
	for (int i = 0; i < array_list->size; i++)
	{
		int *min = array_list->arr[i];
		int index = i;
		for (int j = i + 1; j < array_list->size; j++)
		{
			int *current = array_list->arr[j];
			if (*current < *min)
			{
				min = current;
				index = j;
			}
		}
		if (index != i)
		{
			int *temp1 = array_list->arr[i];
			int value1 = *temp1;
			int *temp2 = array_list->arr[index];
			int value2 = *temp2;
			*temp1 = value2;
			*temp2 = value1;
		}
	}
	for (int i = 0; i < array_list->size; i++)
	{
		int *value = array_list->arr[i];
		printf("%d ", *value);
	}
	printf("\n");
}