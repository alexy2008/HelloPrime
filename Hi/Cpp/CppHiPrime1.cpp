#include <iostream>
#include <cmath>
#include <vector>
#include <chrono>
typedef long long llong;
using namespace std;

llong _maxInd = 0;
llong _maxPrime;
vector<llong> primeArray;

void primeByEuler(int page) {
    bool* sieve = new bool[page] ;
    for (int i = 0; i < page; i++) sieve[i] = false;
    for (int i = 2; i < page; i++) {
        if (!sieve[i]) primeArray.push_back(i);
        for (int j = 0; j * primeArray[j] < page; j++) {
            sieve[i * primeArray[j]] = true;
            if (i % primeArray[j] == 0) break;
        }
    }
    _maxInd = primeArray.size();
    _maxPrime = primeArray[_maxInd - 1];
    // delete[] sieve;
}

void primeByEratosthenes(llong pos, int page) {
    bool* sieve = new bool[page] ;
    for (int i = 0; i < page; i++) sieve[i] = false;
    llong sqrtLimit = (llong) ceil(sqrt(pos + page));
    for (int i = 0; primeArray[i] < sqrtLimit; i++) {
        llong p = primeArray[i];
        for (llong j = ((pos + p - 1) / p ) * p; j < pos + page; j += p)
            sieve[(int) (j - pos)] = true;
    }
    for (int i = 1; i < page; i+=2)
        if (!sieve[i]) {
            _maxPrime = pos + i;
            _maxInd++;
        }
    // delete[] sieve;
}

void calculate(llong limit, int page){
    int n = 1;
    while (page*n < sqrt(limit)) n++;
    primeByEuler(page * n);
    for (int i = 1; i < limit/page; i++) primeByEratosthenes(page * (llong) i, page);
}

int main(int argc, char *argv[]) {
    cout << "Hi Prime! I'm C++ :-)" << endl;
    const llong LIMIT = 10000000;
    const llong PAGE = 10000;
    cout << "Calculate prime sievebers up to " << LIMIT << " using partitioned Eratosthenes sieve" << endl;
    auto startTime = chrono::system_clock::now();
    calculate(LIMIT, PAGE);
    auto endTime = chrono::system_clock::now();
    auto duration = chrono::duration_cast<chrono::milliseconds>(endTime - startTime);
     printf("C++ finished within %.0e; the %lldth prime is %lld, time cost: %lld ms \n",
            (double) LIMIT, _maxInd, _maxPrime, duration.count());
    return 0;
}