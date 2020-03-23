#include <iostream>
#include <ctime>
#include <string>
#include <cmath>
#include <vector>
#include <sstream>

typedef long long llong;
using namespace std;

class Prime {
public:
    Prime(llong limit, llong page, int m);
    llong get(int index);
    void add(llong p);
    void putSequence(llong beginNo);
    void putInterval(llong inter);
    void freeUp();
    void printTable();
    static string dfString(llong l);
    void generateResults(llong inter);
    llong maxInd = 0;
    llong maxPrime = 0;

private:
    llong _maxKeep = 0;//80000;
    llong _offSet = 0;
    vector<string> seqList;
    vector<string> interList;
    int mode;
    llong _prevNo = 0;
    llong *_prime;//[150000];
};

void primeByEuler(int page, Prime *prime) {
    bool num[page];
    for (int i = 0; i < page; i++) num[i] = false;
    for (int i = 2; i < page; i++) {
        if (!num[i]) prime->add(i);
        for (int j = 0; j < prime->maxInd && i * prime->get(j) < page; j++) {
            num[i * prime->get(j)] = true;
            if (i % prime->get(j) == 0) break;
        }
    }
}

void PrimeByEratosthenes(llong pos, int page, Prime *prime) {
    bool num[page];
    for (int i = 0; i < page; i++) num[i] = false;
    for (int i = 0; prime->get(i) < sqrt(pos + page); i++) {
        llong p = prime->get(i);
        for (llong j = ceil(pos * 1.0 / p) * p; j < pos + (llong) page; j += p)
            num[(int) (j - pos)] = true;
    }
    for (int i = 0; i < page; i++)
        if (!num[i]) prime->add(pos + i);
}

int main(int argc, char *argv[]) {
    cout << "Hello Prime! I'm C++ :-)" << endl;
    const llong LIMIT = atoll(argv[1]);
    const llong PAGE = atoll(argv[2]);
    const int MODE = atoi(argv[3]);
    Prime prime(LIMIT,PAGE,MODE);

    cout << "使用分区埃拉托色尼筛选法计算" << Prime::dfString(LIMIT) << "以内素数：" << endl;
    llong startTime = clock();primeByEuler(PAGE, &prime);
    prime.generateResults(PAGE);

    for (int i = 1; i < LIMIT/PAGE; i++) {
        llong pos = PAGE * (llong) i;
        PrimeByEratosthenes(pos, PAGE, &prime);
        prime.generateResults(pos + PAGE);
    }
    llong totalTime = clock() - startTime;
    prime.printTable();
    printf("C++ finished within %.0e; the %lldth prime is %lld; time cost: %lld ms \n",
            (double)LIMIT, prime.maxInd, prime.maxPrime, totalTime);
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

void Prime::add(llong p) {
    _prime[maxInd++ - _offSet] = p;
}

void Prime::generateResults(llong inter) {
    if (mode > 0){
        putSequence(_prevNo);
        putInterval(inter);
        _prevNo = maxInd;
    }
    maxPrime = _prime[maxInd - _offSet - 1];
    freeUp();
}

void Prime::putSequence(llong beginNo) {
    string s;
    for (int i = lLength(beginNo) - 1; i <= lLength(maxInd) - 1; i++) {
        for (int j = 1; j < 10; j++) {
            llong seq = j * pow10(i);
            if (seq < beginNo) continue;
            if (seq >= maxInd) return;
            llong l = _prime[maxInd - _offSet - 1L - (maxInd - seq)];
            s = dfString(seq) + "|" + to_string(l);
            seqList.push_back(s);
            if (mode > 1) printf("==>[No:] %s \n", s.c_str());
        }
    }
}

void Prime::putInterval(llong inter) {
    stringstream ss;
    if (inter % pow10(lLength(inter) - 1) == 0) {
        ss << dfString(inter) << "|" << maxInd << "|" << _prime[maxInd - _offSet - 1];
        interList.push_back(ss.str());
        if (mode > 1) cout << "[In:]" << ss.str() << endl;
    }
}

void Prime::freeUp() {
    if (maxInd > _maxKeep) _offSet = maxInd - _maxKeep;
}

void Prime::printTable() {
    if (mode < 1) return;
    cout << "## 素数序列表" << endl;
    cout << "序号|数值" << endl;
    cout << "---|---" << endl;
    for (const string &s:seqList) cout << s << endl;
    cout << "## 素数区间表" << endl;
    cout << "区间|个数|最大值" << endl;
    cout << "---|---|---" << endl;
    for (const string &s:interList) cout << s << endl;
}

Prime::Prime(llong limit, llong page, int m) {
    mode = m;
    _maxKeep = (llong) (sqrt(limit) / log(sqrt(limit)) * 1.3);
    auto reserve = (llong) ((sqrt(limit) + page) / log(sqrt(limit) + page) * 1.3);
    _prime = new llong[reserve];
    if (mode > 0) cout << "内存分配：" << _maxKeep << " - " << reserve << endl;
}