#include "queue.h"
#include <stdlib.h>

Queue* queue_initialize(int typeSize, char* typeName)
{
	LinkedList* list = llist_initialize(typeSize, typeName);

	if(list == NULL)
		return NULL;

	Queue* queue = malloc(sizeof(*queue));

	if(queue == NULL)
	{
		llist_destroy(list);
		return NULL;
	}
	
	queue->queue = list;

	return queue;	
}

bool queue_enqueue(Queue* queue, void* element)
{
	if(queue == NULL)
		return false;
	
	return llist_add_last(queue->queue, element);
}

void* queue_dequeue(Queue* queue)
{
	if(queue == NULL)
		return NULL;
	
	void* str = llist_remove_first(queue->queue);

	char* S = (char*)str;

	return str;
}

void* queue_peek(Queue* queue)
{
	return llist_get(queue->queue, 0);
}

int queue_size(Queue* queue)
{
	if(queue == NULL)
		return -1;
	
	return queue->queue->size;
}

bool queue_contains(Queue* queue, void* element)
{
	if(queue == NULL)
		return NULL;
	
	int index = llist_index_of(queue->queue, element);

	return (index >= 0 ? true : false);
}

bool queue_destroy(Queue* queue)
{
	if(queue == NULL)
		return false;

	llist_destroy(queue->queue);

	free(queue);

	return true;
}
