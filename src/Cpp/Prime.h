#ifndef HELLOPRIME_PRIME_H
#define HELLOPRIME_PRIME_H

#include <vector>
typedef long long llong;
using namespace std;

class Prime {
private:
    llong* _prime;
    llong _maxInd; //用来存储当前计算的最大素数的序号
    int _maxKeep; //允许在内存中保留的素数数量
    llong _offSet;
    vector<string> seqList;
    vector<string> interList;
    bool isDebug = true;
    llong _prevNo = 0;
public:
    llong get(int index);
    llong size();
    void add(llong p);
    void outputSequence(llong beginNo, llong endNo);
    void outputInterval(llong inter);
    void freeUp();
    void printTable();
    explicit Prime(llong);
    string dfString(llong l);

    void generateResults(long inter, long endNo);

    void generateResults(llong inter, llong endNo);


};
#endif //HELLOPRIME_PRIME_H
