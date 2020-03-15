#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <math.h>

typedef long long llong;

llong _maxInd = 0;
llong _maxKeep = 80000;
llong _offSet = 0;
llong _prime[150000];
llong _maxPrime;

void primeByEuler(int limit) {
    bool num[limit];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 2; i < limit; i++) {
        if (!num[i]) _prime[_maxInd++ - _offSet] = i;
        for (int j = 0; j < _maxInd && i * _prime[j] < limit; j++) {
            num[i * _prime[j]] = true;
            if (i % _prime[j] == 0) break;
        }
    }
}

void PrimeByEratosthenes(llong pos, int limit) {
    bool num[limit];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 0; _prime[i] < sqrt(pos + limit); i++) {
        llong p = _prime[i];
        for (llong j = ceil(pos * 1.0 / p) * p; j < pos + (llong) limit; j += p)
            num[(int) (j - pos)] = true;
    }
    for (int i = 0; i < limit; i++)
        if (!num[i]) _prime[_maxInd++ - _offSet] = pos + i;
}

int main(int argc, char *argv[]) {
    printf("Hi Prime! I'm C\n");
    const llong PAGE = atoi(argv[1]);
    const llong REPEAT = atoi(argv[2]);
    printf("Calculate prime numbers up to %lld using partitioned Eratosthenic sieve \n", PAGE * REPEAT);
    llong startTime = clock();
    primeByEuler(PAGE);
    for (int i = 1; i < REPEAT; i++) {
        PrimeByEratosthenes(PAGE * (llong) i, PAGE);
        if (_maxInd > _maxKeep) {
            _maxPrime = _prime[(int) (_maxInd - _offSet - 1)];
            _offSet = _maxInd - _maxKeep;
        }
    }
    llong totalTime = clock() - startTime;
    printf("C finished within %.0e; the %lldth prime is %lld, time cost: %lld ms \n",
           (double) (PAGE * REPEAT), _maxInd, _maxPrime, totalTime);
    return 0;
}