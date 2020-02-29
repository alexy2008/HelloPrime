using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace HelloPrime
{
    class Prime
    {
        private long[] _prime;
        private long _maxInd; //用来存储当前计算的最大素数的序号
        private int _maxKeep; //允许在内存中保留的素数数量
        private static bool isSilent;
        private long _offSet;
        private long _prevNo = 0;
        private List<String> seqList = new List<string>();
        private List<String> interList = new List<string>();
        private bool isDebug = false;

        public Prime(long limit)
        {
            _maxKeep = (int) (Math.Sqrt(limit) / Math.Log(Math.Sqrt(limit)) * 1.5);
            Console.WriteLine(_maxKeep);
            _maxInd = 0;
            _prime = new long[_maxKeep + 200000];
        }

        public long get(int index)
        {
            return index > _maxInd - 1 ? _prime[index - _offSet] : _prime[index];
        }

        public long getlast()
        {
            return _maxInd > _prime.Length ? _prime[_prime.Length - 1] : _prime[_maxInd - 1];
        }

        public long size()
        {
            return  _maxInd;
        }

        public void add(long p)
        {
            _prime[_maxInd - _offSet] = p;
            _maxInd++;
        }

        public void generateResults (long inter, long endNo){
            outputSequence(_prevNo,endNo);
            outputInterval(inter);
            _prevNo = endNo;
            freeUp();
        }

        public void outputSequence(long beginNo, long endNo){
            string s;
            for (int i = beginNo.ToString().Length - 1; i <= endNo.ToString().Length - 1 ; i++) {
                for (int j = 1; j < 10 ; j++) {
                    long seq =  (long)(j * Math.Pow(10, i) + 0.5);
                    if (seq < beginNo) continue;
                    if (seq >= endNo) return;
                    long l = _prime[_maxInd - _offSet - 1 - (endNo - seq)];
                    s = getDfString(seq) + "|" + l;
                    seqList.Add(s);
                    if (isDebug) Console.WriteLine("==>[No:] "+s);
                }
            }
        }

        public void outputInterval(long inter) {
            string ss;
            if (inter % (long)(Math.Pow(10, inter.ToString().Length - 1) + 0.5) == 0) {
                ss  = getDfString(inter) + "|" + _maxInd + "|" + _prime[_maxInd - _offSet - 1] ;
                interList.Add(ss);
                if (isDebug) Console.WriteLine("[In:]" + ss);
            }
        }

        private void freeUp() {
            if (_maxInd > _maxKeep) _offSet = _maxInd-_maxKeep;
        }

        public void printTable(){
            Console.WriteLine("## 素数区间表");
            Console.WriteLine("区间|个数|最大值");
            Console.WriteLine("---|---|---");
            interList.ForEach( s => Console.WriteLine(s) );
            Console.WriteLine("## 素数序列表");
            Console.WriteLine("序号|数值");
            Console.WriteLine("---|---");
            seqList.ForEach( s => Console.WriteLine(s));
        }


        public static string getDfString(long l)
        {
            string s = l.ToString();
            if(l % 10000_0000_0000L == 0) {
                s = s.Substring(0,s.Length - 12 ) + "万亿";
            }else if(l % 10000_0000L == 0){
                s = s.Substring(0,s.Length - 8 ) + "亿";
            }else if(l%10000 == 0){
                s = s.Substring(0,s.Length - 4 ) + "万";
            }
            return s;
        }
    }
}