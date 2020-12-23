#include <iostream>
#include <ctime>
#include <cmath>

typedef long long llong;
using namespace std;

llong _maxInd = 0;
llong _maxKeep = 80000;
llong _offSet = 0;;
llong _prime[150000];
llong _maxPrime;

void primeByEuler(int page) {
    bool* num = new bool[page] ;
    for (int i = 0; i < page; i++) num[i] = false;
    for (int i = 2; i < page; i++) {
        if (!num[i]) {
            _maxPrime = i;
            _prime[_maxInd++ - _offSet] = _maxPrime;
        }
        for (int j = 0; j < _maxInd && i * _prime[j] < page; j++) {
            num[i * _prime[j]] = true;
            if (i % _prime[j] == 0) break;
        }
    }
    delete[] num;
}

void primeByEratosthenes(llong pos, int page) {
    bool* num = new bool[page] ;
    for (int i = 0; i < page; i++) num[i] = false;
    for (int i = 0; _prime[i] < sqrt(pos + page); i++) {
        llong p = _prime[i];
        for (llong j = ceil(pos * 1.0 / p) * p; j < pos + (llong) page; j += p)
            num[(int) (j - pos)] = true;
    }
    for (int i = 0; i < page; i++)
        if (!num[i]) {
            _maxPrime = pos + i;
            _maxInd++;
        }
    delete[] num;
}

void sieve(llong limit, int page){
    primeByEuler(page);
    for (int i = 1; i < limit/page; i++) primeByEratosthenes(page * (llong) i, page);
}

int main(int argc, char *argv[]) {
    cout << "Hi Prime! I'm C++ :-)" << endl;
    const llong LIMIT = atoll(argv[1]);
    const llong PAGE = atoll(argv[2]);
    cout << "Calculate prime numbers up to " << LIMIT << " using partitioned Eratosthenic sieve" << endl;
    llong startTime = clock();
    sieve(LIMIT, PAGE);
    llong totalTime = double(clock() - startTime) / CLOCKS_PER_SEC * 1000 ;
    printf("C++ finished within %.0e; the %lldth prime is %lld, time cost: %lld ms \n",
           (double) LIMIT, _maxInd, _maxPrime, totalTime);
    return 0;
}