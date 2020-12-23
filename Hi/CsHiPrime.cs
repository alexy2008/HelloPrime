using System;
//using static System.Math;

class CsHiPrime
{
    private static int _maxKeep = 80000;
    private static long[] _primeArray = new long[_maxKeep+70000];
    private static long _offSet;
    private static long MaxInd;
    private static long MaxPrime;

    static void PrimeByEuler(long page)
    {
        var num = new bool[page + 1];
        for (var i = 2; i <= page; i++)
        {
            if (!num[i]){
               MaxPrime = i;     
               _primeArray[MaxInd++ - _offSet] = MaxPrime; 
            }  
            for (var j = 0; j < MaxInd && (long) i * _primeArray[j] <= page; j++)
            {
                num[i * _primeArray[j]] = true;
                if (i % _primeArray[j] == 0) break;
            }
        }
    }

    static void PrimeByEratosthenes(long pos, long page)
    {
        var num = new bool[page];
        for (var i = 0; _primeArray[i] < Math.Sqrt(pos + page); i++)
        {
            var p = _primeArray[i];
            for (var j = (long) (Math.Ceiling((double) pos / p) * p); j < pos +  page; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < num.Length; i++)
            if (!num[i]){
                MaxPrime = pos + i;
                _primeArray[MaxInd++ - _offSet] = MaxPrime; 
            }  
    }

    static void Main(string[] args)
    {
        Console.WriteLine("Hi Prime! I'm C# :-)");
        long limit = long.Parse(args[0]);
        long page = long.Parse(args[1]);

        Console.WriteLine("Calculate prime numbers up to {0} using partitioned Eratosthenic sieve", limit);
        var startTime = DateTime.Now;
        PrimeByEuler(page);
        for (var i = 1; i < limit/page; i++)
        {
            PrimeByEratosthenes( page * i, page);
            if (MaxInd > _maxKeep)   _offSet = MaxInd-_maxKeep;
        }
        var totalTime = (long) DateTime.Now.Subtract(startTime).TotalMilliseconds;
        Console.WriteLine("C# finished within {0:0.#e+00} the {1}th prime is {2}, cost time:{3}ms",
                                                limit, MaxInd, MaxPrime, totalTime);
   }
}