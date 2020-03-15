using System;
using System.Collections.Generic;
using static System.Math;

class CsHelloPrime
{
    private static Prime prime;
    static long PrimeByEuler(long limit)
    {
        long top = 0;
        var num = new bool[limit + 1];
        for (var i = 2; i <= limit; i++)
        {
            if (!num[i]) { prime.Add(i); top++; }
            for (var j = 0; j < prime.MaxInd && (long) i * prime[j] <= limit; j++)
            {
                num[i * prime[j]] = true;
                if (i % prime[j] == 0) break;
            }
        }
        return top;
    }

    static long PrimeByEratosthenes(long pos, long limit)
    {
        long top = 0;
        var num = new bool[limit];
        for (var i = 0; prime[i] < Sqrt(pos + limit); i++)
        {
            var p = prime[i];
            for (var j = (long) (Ceiling((double) pos / p) * p); j < pos +  limit; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < num.Length; i++)
            if (!num[i]) { prime.Add(pos + i); top++; }
        return top;
    }

    static void Main(string[] args)
    {
        Console.WriteLine("Hello Prime! I'm C# :-)");
        long page = long.Parse(args[0]);
        long repeat = long.Parse(args[1]);
        int mode = int.Parse(args[2]);
        prime = new Prime(page, repeat, mode);
        long top = 0;

        Console.WriteLine("Calculate prime numbers up to {0} using partitioned Eratosthenic sieve", Prime.DfString(page*repeat));
        var startTime = DateTime.Now;
        //首先使用欧拉法得到种子素数组
        top += PrimeByEuler(page);
        prime.GenerateResults(page, top);
        //循环使用埃拉托色尼法计算分区
        for (var i = 1; i < repeat; i++)
        {
            var pos = page * i;
            top += PrimeByEratosthenes(pos, page);
            prime.GenerateResults(pos + page, top);
        }
        var totalTime = (long) DateTime.Now.Subtract(startTime).TotalMilliseconds;
        prime.PrintTable();
        Console.WriteLine("C# finished within {0:0.#e+00} the {1}th prime is {2}, cost time:{3}ms",
                                                page*repeat, prime.MaxInd, prime.MaxPrime, totalTime);
   }

}
class Prime
    {
        private long[] _primeArray;
        private int _maxKeep; //允许在内存中保留的素数数量
        private long _offSet;
        private long _prevNo;
        private List<string> seqList = new List<string>();
        private List<string> interList = new List<string>();
        private int _mode;//运行模式：0：跑分模式，1：正常模式，2:调试模式
        public long this[int index] => _primeArray[index];
        public long MaxInd { get; private set; }
        public long MaxPrime { get; private set; }

        public Prime(long page, long repeat, int mode)
        {
            _mode = mode;
            _maxKeep = (int) (Sqrt(page * repeat) / Log(Sqrt(page * repeat)) * 1.3);
            var reserve = (int) ((Sqrt(page * repeat) + page) / Log(Sqrt(page * repeat) + page) * 1.3);
            _primeArray = new long[reserve];
            if (mode > 1) Console.WriteLine("Memory allocation: " + _maxKeep + " - " + reserve);
        }

        public void Add(long p)
        {
            _primeArray[MaxInd++ - _offSet] = p;
        }

        public void GenerateResults (long inter, long endNo){
            if (_mode > 0)
            {
                PutSequence(_prevNo,endNo);
                PutInterval(inter);
                _prevNo = endNo;
            }
            FreeUp();
        }

        private void PutSequence(long beginNo, long endNo){
            for (var i = beginNo.ToString().Length - 1; i <= endNo.ToString().Length - 1 ; i++) {
                for (var j = 1; j < 10 ; j++) {
                    var seq =  (long)(j * Pow(10, i) + 0.5);
                    if (seq < beginNo) continue;
                    if (seq >= endNo) return;
                    var l = this[(int)(MaxInd - _offSet - 1 - (endNo - seq))];
                    var s = CDfString(seq) + "|" + l;
                    seqList.Add(s);
                    if (_mode > 1) Console.WriteLine("==>[No:] "+s);
                }
            }
        }

        private void PutInterval(long inter) {
            if (inter % (long)(Pow(10, inter.ToString().Length - 1) + 0.5) == 0) {
                var ss  = CDfString(inter) + "|" + MaxInd + "|" + this[(int)(MaxInd - _offSet - 1)] ;
                interList.Add(ss);
                if (_mode > 1) Console.WriteLine("[In:]" + ss);
            }
        }

        private void FreeUp() {
            if (MaxInd > _maxKeep)
            {
                MaxPrime = this[(int)(MaxInd - _offSet - 1)];
                _offSet = MaxInd-_maxKeep;
            }
        }

        public void PrintTable(){
            if (_mode < 1) return;
            Console.WriteLine("## 素数序列表");
            Console.WriteLine("序号|数值");
            Console.WriteLine("---|---");
            seqList.ForEach( Console.WriteLine);
            Console.WriteLine("## 素数区间表");
            Console.WriteLine("区间|个数|最大值");
            Console.WriteLine("---|---|---");
            interList.ForEach( Console.WriteLine );
        }
        public static string CDfString(long l)
        {
            var s = l.ToString();
            if(l % 10000_0000_0000L == 0) {
                s = s.Substring(0,s.Length - 12 ) + "万亿";
            }else if(l % 10000_0000L == 0){
                s = s.Substring(0,s.Length - 8 ) + "亿";
            }else if(l%10000 == 0){
                s = s.Substring(0,s.Length - 4 ) + "万";
            }
            return s;
        }

        public static string DfString(long l)
        {
            var s = l.ToString();
            if(l % 1000_000_000_000L == 0) {
                s = s.Substring(0,s.Length - 12 ) + "T";
            }else if(l % 1000_000_000L == 0){
                s = s.Substring(0,s.Length - 9 ) + "G";
            }else if(l%1000_000 == 0){
                s = s.Substring(0,s.Length - 6 ) + "M";
            }else if(l%1000 == 0){
                s = s.Substring(0,s.Length - 3 ) + "K";
            }
            return s;
        }
    }