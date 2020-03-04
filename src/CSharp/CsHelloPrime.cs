using System;
using System.Collections.Generic;

class CsHelloPrime
{
    static long PrimeByEuler(long limit)
    {
        long top = 0;
        bool[] num = new bool[limit + 1];
        for (int i = 2; i <= limit; i++)
        {
            if (!num[i]) { prime.Add(i); top++; }
            for (int j = 0; j < prime.Size() && (long) i * prime.Get(j) <= limit; j++)
            {
                num[i * prime.Get(j)] = true;
                if (i % prime.Get(j) == 0) break;
            }
        }
        return top;
    }

    static long PrimeByEratosthenesInterval(long pos, long limit)
    {
        long top = 0;
        bool[] num = new bool[limit];
        for (int i = 0; prime.Get(i) < Math.Sqrt(pos + limit); i++)
        {
            long p = prime.Get(i);
            for (long j = (long) (Math.Ceiling((double) pos / p) * p); j < pos + (long) limit; j += p)
                num[(int) (j - pos)] = true;
        }
        for (int i = 0; i < num.Length; i++)
            if (!num[i]) { prime.Add(pos + i); top++; }
        return top;
    }

    static void Main(string[] args)
    {
        Console.WriteLine("Hello Prime! I'm C# :-)");
        long page = Int32.Parse(args[0]);
        long repeat = Int32.Parse(args[1]);
        bool isDebug = Boolean.Parse(args[2]);
        prime = new Prime(page, repeat, isDebug);
        long top = 0;

        Console.WriteLine("ʹ�÷���������ɫ��ɸѡ������{0}��������", Prime.DfString(page*repeat));
        var startTime = DateTime.Now;
        //����ʹ��ŷ�����õ�����������
        top += PrimeByEuler(page);
        prime.GenerateResults(page, top);
        //ѭ��ʹ�ð�����ɫ�ᷨ�������
        for (var i = 1; i < repeat; i++)
        {
            long pos = page * (long) i;
            top += PrimeByEratosthenesInterval(pos, page);
            prime.GenerateResults(pos + page, top);
        }
        var totalTime = (long) (DateTime.Now.Subtract(startTime).TotalMilliseconds);
        prime.PrintTable();
        Console.WriteLine("{0}���ڼ�����ϡ��ۼƺ�ʱ :{1}����", Prime.DfString(page*repeat), totalTime);
   }
    private static Prime prime;
}
class Prime
    {
        private long[] _prime;
        private long _maxInd; //�����洢��ǰ�����������������
        private int _maxKeep; //�������ڴ��б�������������
        private long _offSet;
        private long _prevNo;
        private List<String> seqList = new List<string>();
        private List<String> interList = new List<string>();
        private bool isDebug;

        public Prime(long page, long repeat, bool isDbg)
        {
            isDebug = isDbg;
            _maxKeep = (int) (Math.Sqrt(page * repeat) / Math.Log(Math.Sqrt(page * repeat)) * 1.3);
            var reserve = (int) ((Math.Sqrt(page * repeat) + page) / Math.Log(Math.Sqrt(page * repeat) + page) * 1.3);
            _prime = new long[reserve];
            Console.WriteLine("�ڴ���䣺" + _maxKeep + " - " + reserve);
        }

        public long Get(int index)
        {
            return _prime[index];
        }

        public long Size()
        {
            return  _maxInd;
        }

        public void Add(long p)
        {
            _prime[_maxInd - _offSet] = p;
            _maxInd++;
        }

        public void GenerateResults (long inter, long endNo){
            PutSequence(_prevNo,endNo);
            PutInterval(inter);
            _prevNo = endNo;
            FreeUp();
        }

        private void PutSequence(long beginNo, long endNo){
            for (int i = beginNo.ToString().Length - 1; i <= endNo.ToString().Length - 1 ; i++) {
                for (int j = 1; j < 10 ; j++) {
                    long seq =  (long)(j * Math.Pow(10, i) + 0.5);
                    if (seq < beginNo) continue;
                    if (seq >= endNo) return;
                    long l = Get((int)(Size() - _offSet - 1 - (endNo - seq)));
                    var s = DfString(seq) + "|" + l;
                    seqList.Add(s);
                    if (isDebug) Console.WriteLine("==>[No:] "+s);
                }
            }
        }

        public void PutInterval(long inter) {
            if (inter % (long)(Math.Pow(10, inter.ToString().Length - 1) + 0.5) == 0) {
                var ss  = DfString(inter) + "|" + _maxInd + "|" + _prime[_maxInd - _offSet - 1] ;
                interList.Add(ss);
                if (isDebug) Console.WriteLine("[In:]" + ss);
            }
        }

        private void FreeUp() {
            if (_maxInd > _maxKeep) _offSet = _maxInd-_maxKeep;
        }

        public void PrintTable(){
            Console.WriteLine("## ���������");
            Console.WriteLine("����|����|���ֵ");
            Console.WriteLine("---|---|---");
            interList.ForEach( s => Console.WriteLine(s) );
            Console.WriteLine("## �������б�");
            Console.WriteLine("���|��ֵ");
            Console.WriteLine("---|---");
            seqList.ForEach( s => Console.WriteLine(s));
        }
        public static string DfString(long l)
        {
            string s = l.ToString();
            if(l % 10000_0000_0000L == 0) {
                s = s.Substring(0,s.Length - 12 ) + "����";
            }else if(l % 10000_0000L == 0){
                s = s.Substring(0,s.Length - 8 ) + "��";
            }else if(l%10000 == 0){
                s = s.Substring(0,s.Length - 4 ) + "��";
            }
            return s;
        }
    }