#include <iostream>
#include <ctime>
#include <string>
#include <cmath>
//#include <algorithm>

typedef long long ll;
using namespace std;

const ll STEP = 10*10000;
const ll REPEAT = 10000;
bool isSilent= false;

class Prime {
private:
    ll* _prime;
    ll _maxInd; //用来存储当前计算的最大素数的序号
    int _maxKeep; //允许在内存中保留的素数数量
public:
    ll get(int index);
    ll getlast();
    int size();
    void add(ll p);
    Prime();
};

int llSize(ll l){
    ll  sizeTable[12] = {9,99,999,9999,99999,999999,9999999,99999999,999999999,9999999999,9999999999};
    for (int i = 0;  ; i++) if (l<= sizeTable[i]) return i+1;
}

string dfString(ll l) {
    string s = to_string(l);
    if (l >= 10000) s = s.substr(0, s.length() - 4) + "万";
    if (l % (10000*10000) == 0) s = s.substr(0, s.length() - 6) + "亿";
    else if (l >= 10000*10000) s = s.substr(0, s.length() - 6) + "亿" + s.substr(s.length() - 5);
    return s;
}

ll Prime::get(int index) {
    return index > _maxInd - 1 ? getlast() : _prime[index];
}

ll Prime::getlast() {
    return _maxInd > _maxKeep ? _prime[_maxKeep - 1] : _prime[_maxInd - 1];
}

int Prime::size() {
    return (int) _maxInd;
}

void Prime::add(ll p) {
    if (_maxInd < _maxKeep) _prime[_maxInd] = p;
    else _prime[_maxKeep - 1] = p;

    if (!isSilent && _maxInd + 1 == lround(pow(10, llSize(_maxInd + 1)-1)))
        cout << "==>第" << dfString(_maxInd + 1) << "个素数为：" << p  << endl;
    _maxInd++;
}

Prime::Prime() {
    ll lim = STEP * REPEAT;
    _maxKeep = sqrt(lim) / log(sqrt(lim)) * 1.5;
    _maxInd = 0;
    _prime = new ll[_maxKeep];
    cout << "内存使用：" << _maxKeep << endl;
};

Prime prime;

void PrimeByEratosthenesInterval(ll pos, int limit) {
    bool num[STEP];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 0; prime.get(i) < sqrt(pos + limit); i++) {
        ll p = prime.get(i);
        for (ll j = ceil(pos * 1.0 / p) * p; j < pos + (ll) limit; j += p)
            num[(int) (j - pos)] = true;
    }
    for (int i = 0; i < limit; i++) if (!num[i]) prime.add(pos + i);
}

void primeByEuler(int limit) {
    bool num[STEP];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 2; i < limit; i++) {
        if (!num[i])  prime.add(i);
        for (int j = 0; j < prime.size() && i * prime.get(j) < limit; j++) {
            num[i * prime.get(j)] = true;
            if (i % prime.get(j) == 0) break;
        }
    }
}

int main() {
    cout << "Hello, Mr.Prime! I'm C++ :-)"<< endl;
    ll startTime;
    cout << "使用分区埃拉托色尼筛选法计算" << dfString(STEP * REPEAT) << "以内素数：" << endl;
    startTime = clock();
    primeByEuler(STEP);

    for (int i = 1; i < REPEAT; i++) {
        ll pos = STEP * (ll)i;
        if (!isSilent && pos == lround(pow(10, llSize(pos) - 1)))
            cout  << dfString(pos) << "以内,共" << prime.size() << "个,最大素数为：" << prime.getlast()  << endl;
        PrimeByEratosthenesInterval(pos, STEP);
    }
    cout << dfString(STEP * REPEAT) << "以内,共" << prime.size() << "个,最大素数为：" << prime.getlast() << endl;
    cout <<"累计耗时：" << clock() - startTime << "毫秒" << endl;
//    for (int i = 0; i< prime.size();i++) std::cout << prime.get(i) << ',';
    return 0;
}

