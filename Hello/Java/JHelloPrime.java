import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class JHelloPrime {
    private static boolean isDebug = false;

    private static ArrayList<Long> primeByEuler(Integer page) {
        var sieve = new boolean[page];
        ArrayList<Long> primeArray = new ArrayList<>();
        for (var i = 2; i < page; i++) {
            if (!sieve[i])
                primeArray.add((long) i);
            for (var j = 0; (long) i * primeArray.get(j) < page; j++) {
                sieve[(int) (i * primeArray.get(j))] = true;
                if (i % primeArray.get(j) == 0)
                    break;
            }
        }
        return primeArray;
    }

    private static Result primeByEratosthenes(Long pos, Integer page, ArrayList<Long> primeArray) {
        var sieve = new boolean[page];
        long maxInd = 0, maxPrime = 0, sqrLimit = (long) Math.ceil(Math.sqrt((pos + page)));
        for (var i = 1; i < primeArray.size() && primeArray.get(i) < sqrLimit; i++) {
            var p = primeArray.get(i);
            // for (var j = ((long) (Math.ceil((double) pos / p))) * p; j < pos + page; j += p)
            // for (var j = (pos % p == 0 ? pos / p : (pos / p) + 1) * p; j < pos + page; j+= p)
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

    public static Result calculate(Long limit, Integer page, Integer threadNumber) throws InterruptedException {
        int n = 1;
        while (page * n < Math.sqrt(limit)) n++;
        ArrayList<Long> primerList = primeByEuler(page * n);
        AtomicLong maxInd = new AtomicLong(primerList.size());
        AtomicLong maxPrime = new AtomicLong(primerList.get((int) maxInd.get() - 1));
        if (isDebug) System.out.println("Info=>" + "|" + (page * n) + "|" + maxInd + "|" + maxPrime + "|");
        Thread[] task = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            int tid = i, finalN = n;
            task[tid] = new Thread(() -> {
                long localMaxPrime = 0, localMaxInd = 0;
                for (int j = tid + finalN; j < limit / page; j += threadNumber) {
                    var rs = primeByEratosthenes(page * (long) j, page, primerList);
                    localMaxPrime = rs.maxPrime;
                    localMaxInd += rs.maxInd;
                    if (isDebug)
                        System.out.println("Info=>|T" + tid + "|" + ((long) page * (j + 1)) + "|" + localMaxInd + "|"
                                + localMaxPrime + "|");
                }
                if (((tid + 1) % threadNumber == ((limit / page) - finalN) % threadNumber) && (localMaxPrime > maxPrime.get()))
                    maxPrime.set(localMaxPrime);
                maxInd.addAndGet(localMaxInd);
            });
            task[tid].start();
        }
        for (Thread t : task)
            t.join();
        return new Result(maxInd.get(), maxPrime.get());
    }

    public static Result SieveOfAtkin(int limit) {
        long maxPrime = 5, maxInd = 2;
        System.out.println("Atk");
        boolean sieve[] = new boolean[limit + 1];
        int up = (int)Math.sqrt(limit);

        for (int x = 1; x  <= up; x++) {
            for (int y = 1; y  <= up; y++) {
                int n = (4 * x * x) + (y * y);
                if (n <= limit && (n % 12 == 1 || n % 12 == 5))
                    sieve[n] ^= true;
                n = (3 * x * x) + (y * y);
                if (n <= limit && n % 12 == 7)
                    sieve[n] ^= true;
                n = (3 * x * x) - (y * y);
                if (x > y && n <= limit && n % 12 == 11)
                    sieve[n] ^= true;
            }
        }
        for (int r = 5; r * r <= limit; r++) {
            if (sieve[r]) {
                for (int i = r * r; i <= limit; i += r * r)
                    sieve[i] = false;
            }
        }
        for (int a = 0; a <= limit; a++)
            if (sieve[a]){
                maxInd ++;
                maxPrime = a;
            }
        return new Result(maxInd, maxPrime);           
    }

    public record Result(long maxInd, long maxPrime) {}

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello Prime! I'm Java :-)");
        long limit = Long.parseLong(args[0]);
        int page = Integer.parseInt(args[1]);
        isDebug = Integer.parseInt(args[2]) > 0;
        int threadNumber = Integer.parseInt(args[3]);
        System.out.println("Calculate prime numbers up to " + limit + " using partitioned Eratosthenes sieve");
        var startTime = System.currentTimeMillis();
        Result r ;
        if (page == 0) {
            r = SieveOfAtkin((int)limit);  
            
        }
        else{
            r = calculate(limit, page, threadNumber);  
        }
        
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Java using %d thread(s) finished within %.0e the %dth prime is %d, time cost: %d ms \n",
                threadNumber, (double) limit, r.maxInd, r.maxPrime, totalTime);
    }
}