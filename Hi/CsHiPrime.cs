using System;
using static System.Math;

class CsHiPrime
{
    private static int _maxKeep = 80000;
    private static long[] _primeArray = new long[_maxKeep+70000];
    private static long _offSet;
    private static long MaxInd;
    private static long MaxPrime;

    static void PrimeByEuler(long limit)
    {
        var num = new bool[limit + 1];
        for (var i = 2; i <= limit; i++)
        {
            if (!num[i])  _primeArray[MaxInd++ - _offSet] = i;
            for (var j = 0; j < MaxInd && (long) i * _primeArray[j] <= limit; j++)
            {
                num[i * _primeArray[j]] = true;
                if (i % _primeArray[j] == 0) break;
            }
        }
    }

    static void PrimeByEratosthenes(long pos, long limit)
    {
        var num = new bool[limit];
        for (var i = 0; _primeArray[i] < Sqrt(pos + limit); i++)
        {
            var p = _primeArray[i];
            for (var j = (long) (Ceiling((double) pos / p) * p); j < pos +  limit; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < num.Length; i++)
            if (!num[i])  _primeArray[MaxInd++ - _offSet] = pos + i;
    }

    static void Main(string[] args)
    {
        Console.WriteLine("Hi Prime! I'm C# :-)");
        long page = long.Parse(args[0]);
        long repeat = long.Parse(args[1]);

        Console.WriteLine("Calculate prime numbers up to {0} using partitioned Eratosthenic sieve", page*repeat);
        var startTime = DateTime.Now;
        PrimeByEuler(page);
        for (var i = 1; i < repeat; i++)
        {
            PrimeByEratosthenes( page * i, page);
            if (MaxInd > _maxKeep)
            {
                MaxPrime = _primeArray[(int)(MaxInd - _offSet - 1)];
                _offSet = MaxInd-_maxKeep;
            }
        }
        var totalTime = (long) DateTime.Now.Subtract(startTime).TotalMilliseconds;
        Console.WriteLine("C# finished within {0:0.#e+00} the {1}th prime is {2}, cost time:{3}ms",
                                                page*repeat, MaxInd, MaxPrime, totalTime);
   }
}
