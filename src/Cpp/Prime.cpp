#include <iostream>
#include <sstream>
#include <string>
#include <cmath>
#include "Prime.h"

typedef long long llong;
using namespace std;

int lLength(llong l){
    llong  sizeTable[12] = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, 9999999999, 9999999999};
    for (int i = 0;  ; i++) if (l<= sizeTable[i]) return i+1;
}

string Prime::dfString(llong l) {
    string s = to_string(l);
    if (l >= 10000) s = s.substr(0, s.length() - 4) + "万";
    if (l % (10000*10000) == 0) s = s.substr(0, s.length() - 6) + "亿";
    else if (l >= 10000*10000) s = s.substr(0, s.length() - 6) + "亿" + s.substr(s.length() - 5);
    return s;
}

llong Prime::get(int index) {
    return index > _maxInd - 1 ? _prime[index - _offSet] : _prime[index];
}

llong Prime::size() {
    return  _maxInd;
}

void Prime::add(llong p) {
    _prime[_maxInd - _offSet] = p;
    _maxInd++;
}

void Prime::generateResults (llong inter, llong endNo){
    outputSequence(_prevNo,endNo);
    outputInterval(inter);
    _prevNo = endNo;
    freeUp();
}

void Prime::outputSequence(llong beginNo, llong endNo){
    string s;
    for (int i = lLength(beginNo) - 1; i <= lLength(endNo) - 1 ; i++) {
        for (int j = 1; j < 10 ; j++) {
            llong seq =  llong((j * pow(10, i)) + 0.5);
            if (seq < beginNo) continue;
            if (seq >= endNo) return;
            llong l = _prime[_maxInd - _offSet - 1 - (endNo - seq)];
            s = dfString(seq) + "|" + to_string(l) ;
            seqList.push_back(s);
            if (isDebug) printf("==>[No:] %s \n",s.c_str());
        }
    }
}

void Prime:: outputInterval(llong inter) {
    stringstream ss;
    if (inter % llong(pow(10, lLength(inter) - 1) + 0.5) == 0) {
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

Prime::Prime(llong limit) {
    _maxKeep = sqrt(limit) / log(sqrt(limit)) * 1.3 ;
    _prime = new llong[_maxKeep + 200000];
    cout << "内存使用：" << _maxKeep << endl;
}