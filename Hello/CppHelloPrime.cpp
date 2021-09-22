#include <iostream>
#include <iomanip>
#include <cmath>
#include <vector>
#include <thread>
#include <atomic>
typedef long long llong;
using namespace std;

struct Result {llong maxInd,maxPrime;};

void primeByEuler(int page, vector<llong> &primeArray) {
    bool *sieve = new bool[page];
    for (int i = 0; i < page; i++) sieve[i] = false;
    for (int i = 2; i < page; i++) {
        if (!sieve[i]) primeArray.push_back(i);
        for (int j = 0; i * primeArray[j] < page; j++) {
            sieve[i * primeArray[j]] = true;
            if (i % primeArray[j] == 0) break;
        }
    }
    delete[] sieve;
}

Result primeByEratosthenes(llong pos, int page, vector<llong> &primeArray) {
    bool *sieve = new bool[page];
    llong maxInd = 0, maxPrime = 0;
    for (int i = 0; i < page; i++) sieve[i] = false;
    for (int i = 1; i < primeArray.size() && primeArray[i] * primeArray[i] < pos + page; i++) {
        llong p = primeArray[i];
        for (llong j = ceil(pos * 1.0 / p) * p; j < pos + (llong) page; j += p)
            sieve[(int) (j - pos)] = true;
    }
    for (int i = 1; i < page; i += 2)
        if (!sieve[i]) {
            maxPrime = pos + i;
            maxInd++;
        }
    delete[] sieve;
    return Result{maxInd, maxPrime};
}

Result calculate(llong limit, int page, int threadNumber) {
    vector<llong> primerList;
    int n = 1;
    while (page*n < sqrt(limit)) n++;
    cout << "init n page: " << n << endl;
    primeByEuler(page*n, primerList);
    atomic<llong> maxInd;
    maxInd.store(primerList.size());
    llong maxPrime = primerList[primerList.size() - 1];
    auto *task = new thread[threadNumber];

    for (int i = 0; i < threadNumber; i++) {
        task[i] = thread([i, &limit, &page, &threadNumber, &primerList, &maxPrime, &maxInd, &n]() {
            llong localMaxPrime = 0, localMaxInd = 0;
            for (int j = i + n; j < limit / page; j += threadNumber) {
                Result rs = primeByEratosthenes(page * (llong) j, page, primerList);
                localMaxPrime = rs.maxPrime;
                localMaxInd += rs.maxInd;
            }
            if ((i + 1) % threadNumber == ((limit / page) - n) % threadNumber) maxPrime = localMaxPrime;
            maxInd.fetch_add(localMaxInd);
        });
    }
    for (int i = 0; i < threadNumber; i++) task[i].join();
    return Result{maxInd.load(), maxPrime};
}

int main(int argc, char *argv[]) {
    cout << "Hello Prime! I'm C++ :-)" << endl;
    const llong LIMIT = atoll(argv[1]);
    const int PAGE = atoi(argv[2]);
    const int threadNumber = atoi(argv[4]);
    cout << "Calculate prime numbers up to " << LIMIT << " using partitioned Eratosthenes calculate" << endl;
    llong startTime = clock();
    Result r = calculate(LIMIT, PAGE, threadNumber);
    llong totalTime = double(clock() - startTime) / CLOCKS_PER_SEC * 1000;
    cout << "C++ on " << threadNumber << " thread(s) finished within " << scientific << setprecision(0)
         << (double) LIMIT << " the " << r.maxInd << "th prime is " << r.maxPrime << ", time cost: " << totalTime
         << " ms" << endl;
    return 0;
}