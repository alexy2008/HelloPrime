#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <math.h>
#include <string.h>

typedef long long llong;

llong _maxInd = 0;
llong _maxKeep = 80000;
llong _offSet = 0;
char seqList[200][40];
int seqIndx = 0;
char interList[200][40];
int interIndx = 0;
bool isDebug;
llong _prevNo = 0;
llong _prime[150000];

llong get(int index);
llong size();
void add(llong p);
void printTable();
static char *dfString(llong l);
void generateResults(llong inter, llong endNo);

int primeByEuler(int limit) {
    int top = 0;
    bool num[limit];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 2; i < limit; i++) {
        if (!num[i]) {
            add(i);
            top++;
        }
        for (int j = 0; j < size() && i * get(j) < limit; j++) {
            num[i * get(j)] = true;
            if (i % get(j) == 0) break;
        }
    }
    return top;
}

int PrimeByEratosthenesInterval(llong pos, int limit) {
    int top = 0;
    bool num[limit];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 0; get(i) < sqrt(pos + limit); i++) {
        llong p = get(i);
        for (llong j = ceil(pos * 1.0 / p) * p; j < pos + (llong) limit; j += p)
            num[(int) (j - pos)] = true;
    }
    for (int i = 0; i < limit; i++)
        if (!num[i]) {
            add(pos + i);
            top++;
        }
    return top;
}

int main(int argc, char *argv[]) {
    printf("Hello, Mr.Prime! I'm C\n");
    const llong PAGE = atoi(argv[1]);
    const llong REPEAT = atoi(argv[2]);
    isDebug = (strcmp(argv[3], "true") == 0);
    llong top = 0;

    printf("使用分区埃拉托色尼筛选法计算 %s 以内素数：\n", dfString(PAGE * REPEAT));
    llong startTime = clock();
    top += primeByEuler(PAGE);
    generateResults(PAGE, top);

    for (int i = 1; i < REPEAT; i++) {
        llong pos = PAGE * (llong) i;
        top += PrimeByEratosthenesInterval(pos, PAGE);
        generateResults(pos + PAGE, top);
    }
    llong totalTime = clock() - startTime;
    printTable();
    printf("C finished within %.0e; time cost: %lld ms \n", (double) (PAGE * REPEAT), totalTime);
    return 0;
}

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
    _prime[_maxInd - _offSet] = p;
    _maxInd++;
}

llong get(int index) {
    return index > _maxInd - 1 ? _prime[index - _offSet] : _prime[index];
}

llong size() {
    return _maxInd;
}

void putSequence(llong beginNo, llong endNo) {
    char s[13];
    for (int i = lLength(beginNo) - 1; i <= lLength(endNo) - 1; i++) {
        for (int j = 1; j < 10; j++) {
            llong seq = j * pow10(i);
            if (seq < beginNo) continue;
            if (seq >= endNo) return;
            llong l = _prime[_maxInd - _offSet - 1L - (endNo - seq)];
            sprintf(s, "%s|%lld", dfString(seq), l);
            strcpy(seqList[seqIndx++], s);
            if (isDebug) printf("==>[No:] %s \n", s);
        }
    }
}

void putInterval(llong inter) {
    char s[40] = "";
    if (inter % pow10(lLength(inter) - 1) == 0) {
        sprintf(s, "%s|%lld|%lld", dfString(inter), _maxInd, _prime[_maxInd - _offSet - 1]);
        strcpy(interList[interIndx++], s);
        if (isDebug) printf("[In:] %s \n", s);
    }
}

void freeUp() {
    if (_maxInd > _maxKeep) _offSet = _maxInd - _maxKeep;
}

void generateResults(llong inter, llong endNo) {
    putSequence(_prevNo, endNo);
    putInterval(inter);
    _prevNo = endNo;
    freeUp();
}

void printTable() {
    printf("## 素数序列表\n");
    printf("序号|数值\n");
    printf("---|---\n");
    for (int i = 0; i < seqIndx; i++) {
        printf("%s\n", seqList[i]);
    }
    printf("## 素数区间表\n");
    printf("区间|个数|最大值\n");
    printf("---|---|---\n");
    for (int i = 0; i < interIndx; i++) {
        printf("%s\n", interList[i]);
    }
}
