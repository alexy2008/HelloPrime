#include <iostream>
#include <sstream>
#include <ctime>
#include <string>
#include <cmath>
#include <vector>

typedef long long ll;
using namespace std;

const ll STEP = 10*10000;
const ll REPEAT = 10000;

class Prime {
private:
    ll* _prime;
    ll _maxInd; //用来存储当前计算的最大素数的序号
    int _maxKeep; //允许在内存中保留的素数数量
    ll _offSet;
    vector<string> seqList;
    vector<string> interList;
    bool isDebug = false;
public:
    ll get(int index);
    ll size();
    void add(ll p);
    void outputSequence(ll beginNo,ll endNo);
    void outputInterval(ll inter);
    void freeUp();
    void printTable();
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
    return index > _maxInd - 1 ? _prime[index - _offSet] : _prime[index];
}

ll Prime::size() {
    return  _maxInd;
}

void Prime::add(ll p) {
    _prime[_maxInd - _offSet] = p;
    _maxInd++;
}

void Prime::outputSequence(ll beginNo,ll endNo){
    string s;
    for (int i = to_string(beginNo).length()-1; i <= to_string(endNo).length()-1 ; i++) {
        for (int j = 1; j < 10 ; j++) {
            ll seq =  ll((j*pow(10,i))+0.5);
            if (seq < beginNo) continue;
            if (seq >= endNo) return;
            ll l = _prime[ _maxInd -_offSet - 1 - (endNo - seq)];
            s = dfString(seq) + "|" + to_string(l) ;
            seqList.push_back(s);
            if (isDebug) printf("==>[No:] %s \n",s.c_str());
        }
    }
}

void Prime:: outputInterval(ll inter) {
    stringstream ss;
    if (inter % ll(pow(10, to_string(inter).length() - 1)+0.5) == 0) {
        ss  << dfString(inter) << "|" << _maxInd << "|" << _prime[_maxInd - _offSet - 1] ;
        interList.push_back(ss.str());
        if (isDebug) cout << "[In:]" << ss.str() << endl;
    }
}

void Prime:: freeUp() {
    if (_maxInd > _maxKeep) _offSet = _maxInd-_maxKeep;
}

void Prime:: printTable(){
    cout << "## 素数区间表" << endl;
    cout <<"区间|个数|最大值"<< endl;
    cout <<"---|---|---"<< endl;
    for ( const string& s:interList) cout << s << endl;

    cout <<"## 素数序列表"<< endl;
    cout <<"序号|数值"<< endl;
    cout <<"---|---"<< endl;
    for ( const string& s:seqList) cout << s << endl;
}

Prime::Prime() {
    ll lim = STEP * REPEAT;
    _maxKeep = sqrt(lim) / log(sqrt(lim)) * 1.3 ;
    _maxInd = 0;
    _offSet = 0;
    _prime = new ll[_maxKeep +  200000];
    cout << "内存使用：" << _maxKeep << endl;
};

Prime prime;

int PrimeByEratosthenesInterval(ll pos, int limit) {
    int top = 0;
    bool num[STEP];
    for (int i = 0; i < limit; i++) num[i] = false;
    for (int i = 0; prime.get(i) < sqrt(pos + limit); i++) {
        ll p = prime.get(i);
        for (ll j = ceil(pos * 1.0 / p) * p; j < pos + (ll) limit; j += p)
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
    bool num[STEP];
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
    ll startTime;
    ll top = 0;
    cout << "使用分区埃拉托色尼筛选法计算" << dfString(STEP * REPEAT) << "以内素数：" << endl;
    startTime = clock();
    ll n = primeByEuler(STEP);
    top += n;
    prime.outputSequence(0,top);
    prime.outputInterval(STEP);

    for (int i = 1; i < REPEAT; i++) {
        ll pos = STEP * (ll)i;
        n = PrimeByEratosthenesInterval(pos, STEP);
        top += n;
        prime.outputSequence(top-n,top);
        prime.outputInterval(pos+STEP);
        prime.freeUp();
    }
    cout << dfString(STEP * REPEAT) << "以内计算完毕。累计耗时："  << clock() - startTime << "毫秒" << endl;
    prime.printTable();
    return 0;
}

