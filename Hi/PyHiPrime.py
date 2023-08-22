import math
import sys
import time

def prime_by_euler(page):
    prime_array = []
    num = [False] * page
    for i in range(2, page):
        if not num[i]:
            prime_array.append(i)
        for j in range(0, len(prime_array)):
            if i * prime_array[j] >= page: break
            num[i * prime_array[j]] = True
            if i % prime_array[j] == 0: break
    
    return prime_array


def prime_by_eratosthenes_interval(pos, page,prime_array):
    max_ind = 0
    max_prime = 0

    num = [False] * page
    for i in range(0, len(prime_array)):
        p = prime_array[i]
        if p * p >= pos + page: break
        for j in range(math.ceil(pos / p), int((pos + page - 1) / p) + 1):
            num[j * p - pos] = True

    for i in range(0, page):
        if not num[i]:
            max_prime = pos + i
            max_ind = max_ind + 1
    
    return max_ind,max_prime


def sieve(limit, page):
    prime_array = prime_by_euler(page)
    max_ind = len(prime_array)
    max_prime = prime_array[max_ind - 1]
    for i in range(1, int(limit / page)):
        lind,lp = prime_by_eratosthenes_interval(page * i, page, prime_array)
        max_ind = max_ind + lind
        max_prime = lp
    
    return max_ind,max_prime


def main():
    print("Hi Prime! I'm Python :-)", flush=True)
    limit = int(sys.argv[1])
    page = int(sys.argv[2])
    print("Calculate prime numbers up to ", limit, " using partitioned Eratosthenes sieve", flush=True)

    start_time = time.time()
    max_ind, max_prime = sieve(limit, page)
    total_time = round((time.time() - start_time) * 1000)
    print("Python finished within %.0e" % float(limit), "the %d" % max_ind, 'th prime is %d' % max_prime,
          'time cost: %d' % total_time, 'ms', flush=True)


if __name__ == '__main__':
    main()
