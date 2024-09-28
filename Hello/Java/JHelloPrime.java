import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class JHelloPrime {
    private record Result(long maxInd, long maxPrime) {}

    private static Long[] generatePrimesUpTo(int upTo) {
        var sieve = new boolean[upTo];
        var primeArray = new ArrayList<Long>();
        for (var i = 2; i < upTo; i++) {
            if (!sieve[i]) primeArray.add((long) i);                
            for (var j = 0; (long) i * primeArray.get(j) < upTo; j++) {
                sieve[(int) (i * primeArray.get(j))] = true;
                if (i % primeArray.get(j) == 0) break;
            }
        }
        return primeArray.toArray(new Long[0]);
    }

    private static Result findPrimesInRange(long pos, int page, Long[] primeArray) {
        var sieve = new boolean[page];
        long maxInd = 0, maxPrime = 0, sqrLimit = (long) Math.ceil(Math.sqrt(pos + page));
        for (var i = 1; i < primeArray.length && primeArray[i] < sqrLimit; i++) {
            var p = primeArray[i];
            for (var j = ((pos + p - 1) / p) * p; j < pos + page; j += p)
                sieve[(int) (j - pos)] = true;
        }
        for (var i = 1; i < page; i += 2)
            if (!sieve[i]) {
                maxPrime = pos + i;
                maxInd++;
            }
        return new Result(maxInd, maxPrime);
    }

    public static Result calculate(long limit, int page) {
        int n = (int) Math.ceil(Math.sqrt(limit) / page);
        Long[] primerList = generatePrimesUpTo(page * n);
        long maxInd = primerList.length, maxPrime = primerList[(int)(maxInd - 1)];
        for (var i = n; i < limit / page; i++) {
            var rs = findPrimesInRange(page * (long) i, page, primerList);
            maxPrime = rs.maxPrime;
            maxInd += rs.maxInd;
        }
        return new Result(maxInd, maxPrime);
    }

    public static Result calculate(long limit, int page, int threadNumber) throws InterruptedException {
        final int n = (int) Math.ceil(Math.sqrt(limit) / page);
        Long[] primerList = generatePrimesUpTo(page * n);
        var maxInd = new AtomicLong(primerList.length);
        var maxPrime = new AtomicLong(primerList[(int) (maxInd.get() - 1)]);
        var task = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            final int tid = i;
            task[tid] = new Thread(() -> {
                long localMaxPrime = 0, localMaxInd = 0;
                for (int j = tid + n; j < limit / page; j += threadNumber) {
                    var rs = findPrimesInRange(page * (long) j, page, primerList);
                    localMaxPrime = rs.maxPrime;
                    localMaxInd += rs.maxInd;
                }
                if ((tid + 1) % threadNumber == ((limit / page) - n) % threadNumber)   maxPrime.set(localMaxPrime);
                maxInd.addAndGet(localMaxInd);
            });
            task[tid].start();
        }
        for (var t : task) t.join();
        return new Result(maxInd.get(), maxPrime.get());
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello Prime! I'm Java :-)");
        var limit = Long.parseLong(args[0]);
        var page = Integer.parseInt(args[1]);
        var threadNumber = Integer.parseInt(args[3]);
        System.out.println("Calculate prime numbers up to " + limit + " using partitioned Eratosthenes sieve");
        var startTime = System.currentTimeMillis();
        var r = threadNumber == 1 ? calculate(limit, page) : calculate(limit, page, threadNumber);
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Java using %d thread(s) finished within %.0e the %dth prime is %d, time cost: %d ms \n",
                threadNumber, (double) limit, r.maxInd, r.maxPrime, totalTime);
    }
}