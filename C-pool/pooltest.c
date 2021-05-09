#include"pool.h"

int main(int argc, char const *argv[])
{
	ArrayList* array = execute_thread_pool("operations", 40);
	print_sorted(array);
	/* code */
	return 0;
}
