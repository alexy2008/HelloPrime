#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <math.h>
#include <string.h>

typedef long long llong;

llong _maxInd = 0;
llong _maxPrime = 0;
llong _maxKeep = 80000;
llong _offSet = 0;
char seqList[200][40];
int seqIndx = 0;
char interList[200][40];
int interIndx = 0;
int mode;
llong _prevNo = 0;
llong _prime[150000];

int lLength(llong l) {
    llong sizeTable[12] = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, 9999999999, 99999999999,
                           999999999999};
    for (int i = 0;; i++) if (l <= sizeTable[i]) return i + 1;
    return 13;
}

llong pow10(int n) {
    llong ll = 1;
    for (int i = 0; i < n; i++) ll *= 10;
    return ll;
}

char *dfString(llong l) {
    static char s[13];
    char s1[13] = "";
    sprintf(s,"%lld",l);
//    itoa(l, s, 10);
    if (l % 1000000000000L == 0) {
        strncpy(s1, s, strlen(s) - 12);
        strcat(s1, "万亿");
    } else if (l % 100000000L == 0) {
        strncpy(s1, s, strlen(s) - 8);
        strcat(s1, "亿");
    } else if (l % 10000 == 0) {
        strncpy(s1, s, strlen(s) - 4);
        strcat(s1, "万");
    } else {
        return s;
    }
    strcpy(s, s1);
    return s;
}

void add(llong p) {
    _prime[_maxInd++ - _offSet] = p;
}

void putSequence(llong beginNo) {
    char s[13];
    for (int i = lLength(beginNo) - 1; i <= lLength(_maxInd) - 1; i++) {
        for (int j = 1; j < 10; j++) {
            llong seq = j * pow10(i);
            if (seq < beginNo) continue;
            if (seq >= _maxInd) return;
            llong l = _prime[_maxInd - _offSet - 1L - (_maxInd - seq)];
            sprintf(s, "%s|%lld", dfString(seq), l);
            strcpy(seqList[seqIndx++], s);
            if (mode > 1) printf("==>[No:] %s \n", s);
        }
    }
}

void putInterval(llong inter) {
    char s[40] = "";
    if (inter % pow10(lLength(inter) - 1) == 0) {
        sprintf(s, "%s|%lld|%lld", dfString(inter), _maxInd, _prime[_maxInd - _offSet - 1]);
        strcpy(interList[interIndx++], s);
        if (mode > 1) printf("[In:] %s \n", s);
    }
}

void freeUp() {
    if (_maxInd > _maxKeep) _offSet = _maxInd - _maxKeep;
}

void generateResults(llong inter) {
    if (mode > 0) {
        putSequence(_prevNo);
        putInterval(inter);
        _prevNo = _maxInd;
    }
    _maxPrime = _prime[_maxInd - _offSet - 1];
    freeUp();
}

void printTable() {
    if (mode < 1) return;
    printf("## 素数序列表\n");
    printf("序号|数值\n");
    printf("---|---\n");
    for (int i = 0; i < seqIndx; i++)  printf("%s\n", seqList[i]);
    printf("## 素数区间表\n");
    printf("区间|个数|最大值\n");
    printf("---|---|---\n");
    for (int i = 0; i < interIndx; i++)  printf("%s\n", interList[i]);
}

void primeByEuler(int page) {
    bool num[page];
    for (int i = 0; i < page; i++) num[i] = false;
    for (int i = 2; i < page; i++) {
        if (!num[i]) add(i);
        for (int j = 0; j < _maxInd && i * _prime[j] < page; j++) {
            num[i * _prime[j]] = true;
            if (i % _prime[j] == 0) break;
        }
    }
}

void PrimeByEratosthenes(llong pos, int page) {
    bool num[page];
    for (int i = 0; i < page; i++) num[i] = false;
    for (int i = 0; _prime[i] < sqrt(pos + page); i++) {
        llong p = _prime[i];
        for (llong j = ceil(pos * 1.0 / p) * p; j < pos + (llong) page; j += p)
            num[(int) (j - pos)] = true;
    }
    for (int i = 0; i < page; i++)
        if (!num[i]) add(pos + i);
}

int main(int argc, char *argv[]) {
    printf("Hello, Mr.Prime! I'm C\n");
    const llong LIMIT = atoll(argv[1]);
    const llong PAGE = atoll(argv[2]);
    mode = atoi(argv[3]);

    printf("使用分区埃拉托色尼筛选法计算 %s 以内素数：\n", dfString(LIMIT));
    llong startTime = clock();
    primeByEuler(PAGE);
    generateResults(PAGE);

    for (int i = 1; i < LIMIT/PAGE; i++) {
        llong pos = PAGE * (llong) i;
        PrimeByEratosthenes(pos, PAGE);
        generateResults(pos + PAGE);
    }
    llong totalTime = clock() - startTime;
    printTable();
    printf("C finished within %.0e; the %lldth prime is %lld; time cost: %lld ms \n",
           (double)LIMIT, _maxInd, _maxPrime, totalTime);
    return 0;
}