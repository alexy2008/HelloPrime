import math
import sys
import multiprocessing
import time
from typing import List, NamedTuple


class Result(NamedTuple):
    max_ind: int
    max_prime: int


def generate_primes_up_to(up_to: int) -> List[int]:
    sieve = [False] * up_to
    prime_array = []
    for i in range(2, up_to):
        if not sieve[i]:
            prime_array.append(i)
        for j in range(len(prime_array)):
            if i * prime_array[j] >= up_to:
                break
            sieve[i * prime_array[j]] = True
            if i % prime_array[j] == 0:
                break
    return prime_array


def find_primes_in_range(pos: int, page: int, prime_array: List[int]) -> Result:
    sieve = [False] * page
    max_ind, max_prime = 0, 0
    sqrt_limit = math.ceil(math.sqrt(pos + page))
    
    for prime in prime_array:
        if prime >= sqrt_limit:
            break
        for j in range((pos + prime - 1) // prime * prime, pos + page, prime):
            sieve[j - pos] = True

    for i in range(1, page, 2):
        if not sieve[i]:
            max_prime = pos + i
            max_ind += 1

    return Result(max_ind, max_prime)


def calculate(limit: int, page: int) -> Result:
    n = math.ceil(math.sqrt(limit) / page)
    prime_list = generate_primes_up_to(page * n)
    max_ind = len(prime_list)
    max_prime = prime_list[-1]
    
    for i in range(n, limit // page):
        rs = find_primes_in_range(page * i, page, prime_list)
        max_prime = rs.max_prime
        max_ind += rs.max_ind

    return Result(max_ind, max_prime)


def thread_task(i, limit, page, thread_number, prime_list, max_ind, max_prime, n):
    local_max_prime, local_max_ind = 0, 0
    for j in range(i + n, limit // page, thread_number):
        rs = find_primes_in_range(page * j, page, prime_list)
        local_max_prime = rs.max_prime
        local_max_ind += rs.max_ind
    
    with max_ind.get_lock():
        max_ind.value += local_max_ind
    if (i + 1) % thread_number == (limit // page - n) % thread_number:
        max_prime.value = local_max_prime


def calculate_with_threads(limit: int, page: int, thread_number: int) -> Result:
    n = math.ceil(math.sqrt(limit) / page)
    prime_list = generate_primes_up_to(page * n)
    max_ind = multiprocessing.Value('i', len(prime_list))
    max_prime = multiprocessing.Value('i', prime_list[-1])
    
    processes = []
    for i in range(thread_number):
        p = multiprocessing.Process(target=thread_task, args=(i, limit, page, thread_number, prime_list, max_ind, max_prime, n))
        processes.append(p)
        p.start()

    for p in processes:
        p.join()

    return Result(max_ind.value, max_prime.value)


if __name__ == "__main__":
    print("Hello Prime! I'm Python :-)")
    limit = int(sys.argv[1])
    page = int(sys.argv[2])
    thread_number = int(sys.argv[4])

    print(f"Calculate prime numbers up to {limit} using partitioned Eratosthenes sieve")
    start_time = time.time()

    if thread_number == 1:
        result = calculate(limit, page)
    else:
        result = calculate_with_threads(limit, page, thread_number)

    end_time = time.time()
    print(f"Python on {thread_number} thread(s) finished within {limit:.0e}; the {result.max_ind}th prime is {result.max_prime}, time cost: {int((end_time - start_time) * 1000)} ms")