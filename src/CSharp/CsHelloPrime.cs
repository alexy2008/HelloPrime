using System;

namespace HelloPrime
{
    class CsHelloPrime
    {
        static void PrimeByEuler(int limit)
        {
            bool[] num = new bool[limit + 1];
            for (int i = 2; i <= limit; i++)
            {
                if (!num[i]) prime.add(i);
                for (int j = 0; j < prime.size() && (long) i * prime.get(j) <= limit; j++)
                {
                    num[i * prime.get(j)] = true;
                    if (i % prime.get(j) == 0) break;
                }
            }
        }

        static void PrimeByEratosthenesInterval(long pos, int limit)
        {
            bool[] num = new bool[limit];
            for (int i = 0; prime.get(i) < Math.Sqrt(pos + limit); i++)
            {
                long p = prime.get(i);
                for (long j = (long) (Math.Ceiling((double) pos / p) * p); j < pos + (long) limit; j += p)
                    num[(int) (j - pos)] = true;
            }
            for (int i = 0; i < num.Length; i++) if (!num[i]) prime.add(pos + i);
        }

        static void Main(string[] args)
        {
            Console.WriteLine("Hello Mr.Prime! I'm C# :-)");
            int step = 10_0000;
            int repeat = 1_0000;
            long limit = (long) step * repeat;
            DateTime startTime;
            long totalTime = 0;

            int keep = (int) (Math.Sqrt(limit) / Math.Log(Math.Sqrt(limit)) * 1.5);
            Console.WriteLine(keep);
            prime = new Prime(keep);

            Console.WriteLine("使用分区埃拉托色尼筛选法计算{0}以内素数", Prime.getDfString(limit));
            startTime = DateTime.Now;
            //首先使用欧拉法得到种子素数组
            PrimeByEuler(step);
            //循环使用埃拉托色尼法计算分区
            for (int i = 1; i < repeat; i++)
            {
                long pos = step * (long) i;
                if (!isSilent && pos % Math.Pow(10, Math.Min(pos.ToString().Length - 1, 9)) == 0)
                    Console.WriteLine("{0}以内，共{1}个，最大素数为：{2}",
                        Prime.getDfString(pos), prime.size(), prime.getlast());
                PrimeByEratosthenesInterval(pos, step);
            }

            totalTime += (long) (DateTime.Now.Subtract(startTime).TotalMilliseconds);
            Console.WriteLine("{0}以内，共{1}个，最大素数为：{2} ",
                Prime.getDfString(limit), prime.size(), prime.getlast());
            Console.WriteLine("累计耗时 :{0}毫秒", totalTime);
            // foreach (var l in prime) Console.Write(l + ",");
        }

        private static Prime prime;
        private static bool isSilent; //缄默模式，不输出中间信息
    }

    class Prime
    {
        private long[] _prime;
        private long _maxInd; //用来存储当前计算的最大素数的序号
        private int _maxKeep; //允许在内存中保留的素数数量
        private static bool isSilent;

        public Prime(int keep)
        {
            _maxKeep = keep;
            _maxInd = 0;
            _prime = new long[_maxKeep];
        }

        public long get(int index)
        {
            return index > _maxInd - 1 ? getlast() : _prime[index];
        }

        public long getlast()
        {
            return _maxInd > _prime.Length ? _prime[_prime.Length - 1] : _prime[_maxInd - 1];
        }

        public int size()
        {
            return (int) _maxInd;
        }

        public void add(long p)
        {
            if (_maxInd < _maxKeep) _prime[_maxInd] = p;
            else _prime[_maxKeep - 1] = p;
            if (!isSilent && (_maxInd + 1) % Math.Pow(10, Math.Min((_maxInd + 1).ToString().Length - 1, 8)) == 0)
                Console.WriteLine("==>第{0}个素数为：{1} ", getDfString(_maxInd + 1), p);
            _maxInd++;
        }

        public static string getDfString(long l)
        {
            string s = l.ToString();
            if (l >= 1_0000) s = s.Substring(0, s.Length - 4) + "万";
            if (l % 1_0000_0000 == 0) s = s.Substring(0, s.Length - 5) + "亿";
            else if (l >= 1_0000_0000) s = s.Substring(0, s.Length - 5) + "亿" + s.Substring(s.Length - 5);
            return s;
        }
    }
}