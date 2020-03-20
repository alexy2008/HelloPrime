#include <iostream>
#include <ctime>
#include <string>
#include <cmath>
#include <vector>
#include <sstream>
#include <cstring>

typedef long long llong;
using namespace std;

class Prime {
public:
    llong get(int index);
    llong size();
    void add(llong p);
    void putSequence(llong beginNo, llong endNo);
    void putInterval(llong inter);
    void freeUp();
    void printTable();
    explicit Prime(llong page, llong repeat, bool isDbg);
    static string dfString(llong l);
    void generateResults(llong inter, llong endNo);

private:
    llong _maxInd = 0;
    llong _maxKeep = 0;//80000;
    llong _offSet = 0;
    vector<string> seqList;
    vector<string> interList;
    bool isDebug;
    llong _prevNo = 0;
    llong *_prime;//[150000];

};

int primeByEuler(int limit, Prime *prime) {
    int top = 0;
    bool num[limit];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 2; i < limit; i++) {
        if (!num[i]) {
            prime->add(i);
            top++;
        }
        for (int j = 0; j < prime->size() && i * prime->get(j) < limit; j++) {
            num[i * prime->get(j)] = true;
            if (i % prime->get(j) == 0) break;
        }
    }
    return top;
}

int PrimeByEratosthenes(llong pos, int limit, Prime *prime) {
    int top = 0;
    bool num[limit];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 0; prime->get(i) < sqrt(pos + limit); i++) {
        llong p = prime->get(i);
        for (llong j = ceil(pos * 1.0 / p) * p; j < pos + (llong) limit; j += p)
            num[(int) (j - pos)] = true;
    }
    for (int i = 0; i < limit; i++)
        if (!num[i]) {
            prime->add(pos + i);
            top++;
        }
    return top;
}

int main(int argc, char *argv[]) {
    cout << "Hello Prime! I'm C++ :-)" << endl;
    const llong PAGE = atoll(argv[1]);
    const llong REPEAT = atoll(argv[2]);
    const bool isDebug = (strcmp(argv[3], "true") == 0);
    Prime prime(PAGE, REPEAT, isDebug);

    llong top = 0;

    cout << "使用分区埃拉托色尼筛选法计算" << Prime::dfString(PAGE * REPEAT) << "以内素数：" << endl;
    llong startTime = clock();
    top += primeByEuler(PAGE, &prime);
    prime.generateResults(PAGE, top);

    for (int i = 1; i < REPEAT; i++) {
        llong pos = PAGE * (llong) i;
        top += PrimeByEratosthenes(pos, PAGE, &prime);
        prime.generateResults(pos + PAGE, top);
    }
    llong totalTime = clock() - startTime;
    prime.printTable();
    printf("C++ finished within %.0e; time cost: %lld ms \n" , (double)(PAGE * REPEAT) , totalTime);
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

string Prime::dfString(llong l) {
    string s = to_string(l);
    if (l >= 10000) s = s.substr(0, s.length() - 4) + "万";
    if (l % (10000 * 10000) == 0) s = s.substr(0, s.length() - 6) + "亿";
    else if (l >= 10000 * 10000) s = s.substr(0, s.length() - 6) + "亿" + s.substr(s.length() - 5);
    return s;
}

llong Prime::get(int index) {
    return _prime[index];
}

llong Prime::size() {
    return _maxInd;
}

void Prime::add(llong p) {
    _prime[_maxInd++ - _offSet] = p;
}

void Prime::generateResults(llong inter, llong endNo) {
    putSequence(_prevNo, endNo);
    putInterval(inter);
    _prevNo = endNo;
    freeUp();
}

void Prime::putSequence(llong beginNo, llong endNo) {
    string s;
    for (int i = lLength(beginNo) - 1; i <= lLength(endNo) - 1; i++) {
        for (int j = 1; j < 10; j++) {
            llong seq = j * pow10(i);
            if (seq < beginNo) continue;
            if (seq >= endNo) return;
            llong l = _prime[_maxInd - _offSet - 1L - (endNo - seq)];
            s = dfString(seq) + "|" + to_string(l);
            seqList.push_back(s);
            if (isDebug) printf("==>[No:] %s \n", s.c_str());
        }
    }
}

void Prime::putInterval(llong inter) {
    stringstream ss;
    if (inter % pow10(lLength(inter) - 1) == 0) {
        ss << dfString(inter) << "|" << _maxInd << "|" << _prime[_maxInd - _offSet - 1];
        interList.push_back(ss.str());
        if (isDebug) cout << "[In:]" << ss.str() << endl;
    }
}

void Prime::freeUp() {
    if (_maxInd > _maxKeep) _offSet = _maxInd - _maxKeep;
}

void Prime::printTable() {
    cout << "## 素数序列表" << endl;
    cout << "序号|数值" << endl;
    cout << "---|---" << endl;
    for (const string &s:seqList) cout << s << endl;
    cout << "## 素数区间表" << endl;
    cout << "区间|个数|最大值" << endl;
    cout << "---|---|---" << endl;
    for (const string &s:interList) cout << s << endl;
}

Prime::Prime(llong page, llong repeat, bool isDbg) {
    isDebug = isDbg;
    _maxKeep = (llong) (sqrt(page * repeat) / log(sqrt(page * repeat)) * 1.3);
    auto reserve = (llong) ((sqrt(page * repeat) + page) / log(sqrt(page * repeat) + page) * 1.3);
    _prime = new llong[reserve];
    cout << "内存分配：" << _maxKeep << " - " << reserve << endl;
}