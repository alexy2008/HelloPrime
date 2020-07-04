using System;
using System.Collections.Generic;
using static System.Math;

class CsHelloPrime
{
    private static Prime prime;
    static void PrimeByEuler(long page)
    {
        var num = new bool[page + 1];
        for (var i = 2; i <= page; i++)
        {
            if (!num[i]) prime.Add(i);
            for (var j = 0; j < prime.MaxInd && (long) i * prime[j] <= page; j++)
            {
                num[i * prime[j]] = true;
                if (i % prime[j] == 0) break;
            }
        }
    }

    static void PrimeByEratosthenes(long pos, long page)
    {
        var num = new bool[page];
        for (var i = 0; prime[i] < Sqrt(pos + page); i++)
        {
            var p = prime[i];
            for (var j = (long) (Ceiling((double) pos / p) * p); j < pos +  page; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < num.Length; i++)
            if (!num[i]) prime.Add(pos + i);
    }

    static void Main(string[] args)
    {
        Console.WriteLine("Hello Prime! I'm C# :-)");
        long limit = long.Parse(args[0]);
        long page = long.Parse(args[1]);
        int mode = int.Parse(args[2]);
        prime = new Prime(limit, page, mode);

        Console.WriteLine("Calculate prime numbers up to {0} using partitioned Eratosthenic sieve", Prime.DfString(limit));
        var startTime = DateTime.Now;
        //����ʹ��ŷ�����õ�����������
        PrimeByEuler(page);
        prime.GenerateResults(page);
        //ѭ��ʹ�ð�����ɫ�ᷨ�������
        for (var i = 1; i < limit/page; i++)
        {
            var pos = page * i;
            PrimeByEratosthenes(pos, page);
            prime.GenerateResults(pos + page);
        }
        var totalTime = (long) DateTime.Now.Subtract(startTime).TotalMilliseconds;
        prime.PrintTable();
        Console.WriteLine("C# finished within {0:0.#e+00} the {1}th prime is {2}, cost time:{3}ms",
                                                limit, prime.MaxInd, prime.MaxPrime, totalTime);
   }
}

class Prime
    {
        private long[] _primeArray;
        private int _maxKeep; //�������ڴ��б�������������
        private long _offSet;
        private long _prevNo;
        private List<string> seqList = new List<string>();
        private List<string> interList = new List<string>();
        private int _mode;//����ģʽ��0���ܷ�ģʽ��1������ģʽ��2:����ģʽ
        public long this[int index] => _primeArray[index];
        public long MaxInd { get; private set; }
        public long MaxPrime { get; private set; }

        public Prime(long limit, long page, int mode)
        {
            _mode = mode;
            _maxKeep = (int) (Sqrt(limit) / Log(Sqrt(limit)) * 1.3);
            var reserve = (int) ((Sqrt(limit) + page) / Log(Sqrt(limit) + page) * 1.3);
            _primeArray = new long[reserve];
            if (mode > 1) Console.WriteLine("Memory allocation: " + _maxKeep + " - " + reserve);
        }

        public void Add(long p)
        {
            _primeArray[MaxInd++ - _offSet] = p;
        }

        public void GenerateResults (long inter){
            if (_mode > 0)
            {
                PutSequence(_prevNo);
                PutInterval(inter);
                _prevNo = MaxInd;
            }
            MaxPrime = this[(int)(MaxInd - _offSet - 1)];
            FreeUp();
        }

        private void PutSequence(long beginNo){
            for (var i = beginNo.ToString().Length - 1; i <= MaxInd.ToString().Length - 1 ; i++) {
                for (var j = 1; j < 10 ; j++) {
                    var seq =  (long)(j * Pow(10, i) + 0.5);
                    if (seq < beginNo) continue;
                    if (seq >= MaxInd) return;
                    var l = this[(int)(MaxInd - _offSet - 1 - (MaxInd - seq))];
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
            if (MaxInd > _maxKeep)  _offSet = MaxInd-_maxKeep;
        }

        public void PrintTable(){
            if (_mode < 1) return;
            Console.WriteLine("## �������б�");
            Console.WriteLine("���|��ֵ");
            Console.WriteLine("---|---");
            seqList.ForEach( Console.WriteLine);
            Console.WriteLine("## ���������");
            Console.WriteLine("����|����|���ֵ");
            Console.WriteLine("---|---|---");
            interList.ForEach( Console.WriteLine );
        }
        public static string CDfString(long l)
        {
            var s = l.ToString();
            if(l % 10000_0000_0000L == 0) {
                s = s.Substring(0,s.Length - 12 ) + "����";
            }else if(l % 10000_0000L == 0){
                s = s.Substring(0,s.Length - 8 ) + "��";
            }else if(l%10000 == 0){
                s = s.Substring(0,s.Length - 4 ) + "��";
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