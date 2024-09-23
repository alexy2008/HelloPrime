#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include <stdbool.h>

typedef long long llong;

llong _maxInd = 0;
llong _maxPrime;
llong *primeArray = NULL;

void primeByEuler(int page) {
    bool *sieve = (bool *)malloc(page * sizeof(bool));
    for (int i = 0; i < page; i++) {
        sieve[i] = false;
    }

    primeArray = (llong *)malloc(page * sizeof(llong));
    int primeCount = 0;

    for (int i = 2; i < page; i++) {
        if (!sieve[i]) {
            primeArray[primeCount++] = i;
        }
        for (int j = 0; j < primeCount && i * primeArray[j] < page; j++) {
            sieve[i * primeArray[j]] = true;
            if (i % primeArray[j] == 0) break;
        }
    }
    _maxInd = primeCount;
    _maxPrime = primeArray[primeCount - 1];

    free(sieve);
}

void primeByEratosthenes(llong pos, int page) {
    bool *sieve = (bool *)malloc(page * sizeof(bool));
    for (int i = 0; i < page; i++) {
        sieve[i] = false;
    }
    llong sqrtLimit = (llong)ceil(sqrt(pos + page));

    for (int i = 0; i < _maxInd && primeArray[i] < sqrtLimit; i++) {
        llong p = primeArray[i];
        for (llong j = ((pos + p - 1) / p) * p; j < pos + page; j += p) {
            sieve[(int)(j - pos)] = true;
        }
    }
    for (int i = 1; i < page; i += 2) {
        if (!sieve[i]) {
            _maxPrime = pos + i;
            _maxInd++;
        }
    }

    free(sieve);
}

void calculate(llong limit, int page) {
    int n = 1;
    while (page * n < sqrt(limit)) n++;
    primeByEuler(page * n);

    for (int i = n; i < limit / page; i++) {
        primeByEratosthenes(page * (llong)i, page);
    }
}

int main(int argc, char *argv[]) {
    printf("Hi Prime! I'm C :-)\n");
    if (argc < 3) {
        printf("Usage: %s <LIMIT> <PAGE>\n", argv[0]);
        return 1;
    }
    const llong LIMIT = atoll(argv[1]);
    const int PAGE = atoi(argv[2]);
    printf("Calculate prime numbers up to %lld using partitioned Eratosthenes calculate\n", LIMIT);

    clock_t startTime = clock();
    calculate(LIMIT, PAGE);
    clock_t endTime = clock();
    
    double duration = (double)(endTime - startTime) / CLOCKS_PER_SEC * 1000; // milliseconds
    printf("C finished; the %lldth prime is %lld, time cost: %.0f ms\n", _maxInd, _maxPrime, duration);

    free(primeArray);
    return 0;
}