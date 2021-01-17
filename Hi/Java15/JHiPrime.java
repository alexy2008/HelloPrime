import java.util.concurrent.CountDownLatch;

public class JHiPrime {
    private static final int maxKeep = 350000;
    private static CountDownLatch cd;

    private static Result primeByEuler(Integer page) {
        var num = new boolean[page];
        long[] primeArray = new long[maxKeep];
        long maxInd = 0, maxPrime = 0;
        for (var i = 2; i < page; i++) {
            if (!num[i]) {
                maxPrime = i;
                primeArray[(int) (maxInd++)] = maxPrime;
            }
            for (var j = 0; (long) i * primeArray[j] < page; j++) {
                num[(int) (i * primeArray[j])] = true;
                if (i % primeArray[j] == 0) break;
            }
        }
        return new Result(maxInd, maxPrime, primeArray);
    }

    private static Result primeByEratosthenes(Long pos, Integer page, long[] primeArray) {
        var num = new boolean[page];
        long maxInd = 0, maxPrime = 0;
        for (var i = 0; primeArray[i] * primeArray[i] < pos + page; i++) {
            var p = primeArray[i];
            for (var j = ((long) (Math.ceil((double) pos / p))) * p; j < pos + page; j += p) {
                System.out.println("i is:"+i+";p is:"+p +" ;j is :" + j + ";j - pos is:" + (j-pos));
                num[(int) (j - pos)] = true;
            }
        }
        for (var i = 0; i < page; i++)
            if (!num[i]) {
                maxPrime = pos + i;
                maxInd++;
            }
        return new Result(maxInd, maxPrime, null);
    }

    public static Result sieve(long limit, int page) {
        Result r = primeByEuler(page);
        long maxInd = r.maxInd, maxPrime = r.maxPrime;
        long[] primeArray = r.primeArray;
        for (var i = 1; i < limit / page; i++) {
            r = primeByEratosthenes(page * (long) i, page, primeArray);
            maxInd+= r.maxInd;
            maxPrime = r.maxPrime;
        }
        return new Result(maxInd, maxPrime, primeArray);
    }

    public static Result sieve(long limit, int page, int threadCount) throws InterruptedException {
        if (threadCount <= 1) return sieve(limit, page);
        cd = new CountDownLatch(threadCount);
        Result r = primeByEuler(page);
        long maxInd = r.maxInd, maxPrime = r.maxPrime;
        long[] primeArray = r.primeArray;

        class Task extends Thread{
            public long maxPrime;
            public long maxInd;
            final int tid;
            public Task(int tid){
                this.tid = tid;
            }
            @Override
            public void run() {
                for (int i = tid ; i < limit/page ; i+= threadCount) {
                    if (i==0) continue;
                    var rs = primeByEratosthenes(page * (long) i, page, primeArray );
                    maxPrime = rs.maxPrime;
                    maxInd += rs.maxInd;
                }
                cd.countDown();
            }
        }

        var ths = new Task[threadCount];
        for (int i = 0; i < threadCount; i++) {
            ths[i] = new Task(i);
            ths[i].start();
        }
        cd.await();
        for (int i = 0; i < threadCount; i++) {
            if (ths[i].maxPrime > maxPrime) maxPrime = ths[i].maxPrime;
            maxInd += ths[i].maxInd;
        }
        return new Result(maxInd, maxPrime, primeArray);
    }

    public record Result(long maxInd, long maxPrime, long[] primeArray){}

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hi Prime! I'm Java :-)");
        long limit = Long.parseLong(args[0]);
        int page = Integer.parseInt(args[1]);
        int threadCount = Integer.parseInt(args[3]);
        System.out.println("Calculate prime numbers up to " + limit + " using partitioned Eratosthenes sieve");
        var startTime = System.currentTimeMillis();
        Result r = sieve(limit, page, threadCount);
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Java finished within %.0e the %dth prime is %d, time cost: %d ms \n",
                (double) limit, r.maxInd, r.maxPrime, totalTime);
    }
}