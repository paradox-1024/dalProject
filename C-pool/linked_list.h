#ifndef __LINKEDLIST_HEADER
#define __LINKEDLIST_HEADER
#include <stdbool.h>

typedef struct _Node
{
	void* data;
	struct _Node* next;
	struct _Node* prev;
} Node;

typedef struct _LList
{
	Node* first;
	Node* last;
	int size;
	int itemSize;
	char* type;	
} LinkedList;

LinkedList* llist_initialize(int, char*);
bool        llist_add_at(LinkedList*, int, void*);
bool        llist_add_first(LinkedList*, void*);
bool        llist_add_last(LinkedList*, void*);
void*       llist_get(LinkedList*, int);
int         llist_index_of(LinkedList*, void*);
void*       llist_remove(LinkedList*, int);
void*       llist_remove_first(LinkedList*);
void*       llist_remove_last(LinkedList*);
bool        llist_destroy(LinkedList*);

#endif
