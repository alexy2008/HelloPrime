using System;
using System.Collections.Generic;
using System.Threading;

class CsHelloPrime {
    internal record Result ( long MaxInd ,long MaxPrime );

    private static List<long> GeneratePrimesUpTo(long upTo) {
        var sieve = new bool[upTo];
        var primeArray = new List<long>(350000);
        for (var i = 2; i < upTo; i++) {
            if (!sieve[i]) primeArray.Add(i);
            for (var j = 0; i * primeArray[j] < upTo; j++) {
                sieve[i * primeArray[j]] = true;
                if (i % primeArray[j] == 0) break;
            }
        }
        return primeArray;
    }

    private static Result FindPrimesInRange(long pos, int page, List<long> primeArray) {
        var sieve = new bool[page];
        long maxInd = 0, maxPrime = 0, sqrLimit = (long)Math.Ceiling(Math.Sqrt(pos + page));
        for (var i = 1; i < primeArray.Count && primeArray[i] < sqrLimit; i++) {
            var p = primeArray[i];
            for (var j = ((pos + p - 1) / p ) * p; j < pos + page; j += p)
                sieve[(int)(j - pos)] = true;
        }
        for (var i = 1; i < sieve.Length; i += 2)
            if (!sieve[i]) {
                maxPrime = pos + i;
                maxInd++;
            }
        return new Result(maxInd, maxPrime);
    }

    public static Result Calculate(long limit, int page) {
        int n = (int) Math.Ceiling(Math.Sqrt(limit) / page);  
        var primerList = GeneratePrimesUpTo(page * n);
        long maxInd = primerList.Count, maxPrime = primerList[^1];
        for (var i = n; i < limit / page; i++) {
            var rs = FindPrimesInRange(page * (long)i, page, primerList);
            maxPrime = rs.MaxPrime;
            maxInd += rs.MaxInd;
        }
        return new Result(maxInd, maxPrime);
    }

    public static Result Calculate(long limit, int page,  int threadNumber) {
        int n = (int) Math.Ceiling(Math.Sqrt(limit) / page);  
        var primerList = GeneratePrimesUpTo(page * n);
        long maxInd = primerList.Count;
        var maxPrime = primerList[^1];
        var task = new Thread[threadNumber];
        for (var i = 0; i < threadNumber; i++) {
            var tid = i;
            task[tid] = new Thread(() => {
                long localMaxPrime = 0, localMaxInd = 0;
                for (var j = tid + n; j < limit / page; j += threadNumber) {
                    var rs = FindPrimesInRange(page * (long)j, page, primerList);
                    localMaxPrime = rs.MaxPrime;
                    localMaxInd += rs.MaxInd;
                }
                if ((tid + 1) % threadNumber == ((limit / page) - n) % threadNumber) maxPrime = localMaxPrime;
                Interlocked.Add(ref maxInd, localMaxInd);
            });
            task[tid].Start();
        }
        foreach (var t in task) t.Join();
        return new Result(maxInd, maxPrime);
    }

    static void Main(string[] args) {
        Console.WriteLine("Hello Prime! I'm C# :-)");
        var limit = long.Parse(args[0]);
        var page = int.Parse(args[1]);
        var threadNumber = int.Parse(args[3]);
        Console.WriteLine("Calculate prime numbers up to {0} using partitioned Eratosthenes sieve", limit);
        var startTime = DateTime.Now;
        var r = threadNumber == 1 ? Calculate(limit, page) : Calculate(limit, page, threadNumber);
        var totalTime = (long)DateTime.Now.Subtract(startTime).TotalMilliseconds;
        Console.WriteLine("C# using {4} thread(s) finished within {0:0.#e+00} the {1}th prime is {2}, time cost: {3} ms",
            limit, r.MaxInd, r.MaxPrime, totalTime, threadNumber);
    }
}