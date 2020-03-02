using System;

namespace HelloPrime
{
    class CsHelloPrime
    {
        static long PrimeByEuler(int limit)
        {
            long top = 0;
            bool[] num = new bool[limit + 1];
            for (int i = 2; i <= limit; i++)
            {
                if (!num[i])
                {
                    prime.add(i);
                    top++;
                }
                for (int j = 0; j < prime.size() && (long) i * prime.get(j) <= limit; j++)
                {
                    num[i * prime.get(j)] = true;
                    if (i % prime.get(j) == 0) break;
                }
            }

            return top;
        }

        static long PrimeByEratosthenesInterval(long pos, int limit)
        {
            long top = 0;
            bool[] num = new bool[limit];
            for (int i = 0; prime.get(i) < Math.Sqrt(pos + limit); i++)
            {
                long p = prime.get(i);
                for (long j = (long) (Math.Ceiling((double) pos / p) * p); j < pos + (long) limit; j += p)
                    num[(int) (j - pos)] = true;
            }
            for (int i = 0; i < num.Length; i++)
                if (!num[i])
                {
                    prime.add(pos + i);
                    top++;
                }
            return top;
        }

        static void Main(string[] args)
        {
            Console.WriteLine("Hello Mr.Prime! I'm C# :-)");
            int PAGE = 1_0000;
            int repeat = 1_0000;
            long limit = (long) PAGE * repeat;
            DateTime startTime;
            long top = 0;

            prime = new Prime(limit);

            Console.WriteLine("ʹ�÷���������ɫ��ɸѡ������{0}��������", Prime.getDfString(limit));
            startTime = DateTime.Now;
            //����ʹ��ŷ�����õ�����������
            top += PrimeByEuler(PAGE);
            prime.generateResults(PAGE, top);
            //ѭ��ʹ�ð�����ɫ�ᷨ�������
            for (int i = 1; i < repeat; i++)
            {
                long pos = PAGE * (long) i;
                top += PrimeByEratosthenesInterval(pos, PAGE);
                prime.generateResults(pos + PAGE, top);
            }
            long totalTime = (long) (DateTime.Now.Subtract(startTime).TotalMilliseconds);
            prime.printTable();
            Console.WriteLine("{0}���ڼ�����ϡ��ۼƺ�ʱ :{1}����", Prime.getDfString(limit), totalTime);
       }
        private static Prime prime;
    }
}