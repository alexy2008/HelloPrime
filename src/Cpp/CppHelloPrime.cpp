#include <iostream>
#include <ctime>
#include <string>
#include <cmath>
#include "Prime.h"

typedef long long llong;
using namespace std;

const llong PAGE = 100 * 10000;
const llong REPEAT = 100000;

Prime prime(PAGE * REPEAT);

int PrimeByEratosthenesInterval(llong pos, int limit) {
    int top = 0;
    bool num[PAGE];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 0; prime.get(i) < sqrt(pos + limit); i++) {
        llong p = prime.get(i);
        for (llong j = ceil(pos * 1.0 / p) * p; j < pos + (llong) limit; j += p)
            num[(int) (j - pos)] = true;
    }
    for (int i = 0; i < limit; i++) if (!num[i]) {
        prime.add(pos + i);
        top++;
    }
    return top;
}

int primeByEuler(int limit) {
    int top = 0;
    bool num[PAGE];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 2; i < limit; i++) {
        if (!num[i])  {
            prime.add(i);
            top++;
        }
        for (int j = 0; j < prime.size() && i * prime.get(j) < limit; j++) {
            num[i * prime.get(j)] = true;
            if (i % prime.get(j) == 0) break;
        }
    }
    return top;
}

int main() {
    cout << "Hello, Mr.Prime! I'm C++ :-)"<< endl;
    llong top = 0;

    cout << "使用分区埃拉托色尼筛选法计算" << prime.dfString(PAGE * REPEAT) << "以内素数：" << endl;
    llong startTime = clock();
    top += primeByEuler(PAGE);
    prime.generateResults(PAGE, top);

    for (int i = 1; i < REPEAT; i++) {
        llong pos = PAGE * (llong)i;
        top += PrimeByEratosthenesInterval(pos, PAGE);
        prime.generateResults(pos + PAGE, top);
    }
    llong totalTime = clock() - startTime;
    prime.printTable();
    cout << prime.dfString(PAGE * REPEAT) << "以内计算完毕。累计耗时：" << totalTime << "毫秒" << endl;
    return 0;
}

