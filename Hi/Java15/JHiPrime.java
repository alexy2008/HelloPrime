import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class JHiPrime {
    private static final int maxKeep = 350000;
    private static CountDownLatch cd;

    private static ArrayList<Long> primeByEuler(Integer page) {
        var num = new boolean[page];
        var primeArray = new ArrayList<Long>(maxKeep);
        for (var i = 2; i < page; i++) {
            if (!num[i]) {
                primeArray.add((long) i);
            }
            for (var j = 0; (long) i * primeArray.get(j) < page; j++) {
                num[(int) (i * primeArray.get(j))] = true;
                if (i % primeArray.get(j) == 0) break;
            }
        }
        return  primeArray;
    }

    private static long[] primeByEratosthenes(Long pos, Integer page, ArrayList<Long> primeArray) {
        var num = new boolean[page];
        long maxInd = 0, maxPrime = 0;
        for (var i = 0; primeArray.get(i) * primeArray.get(i) < pos + page; i++) {
            var p = primeArray.get(i);
            for (var j = ((long) (Math.ceil((double) pos / p))) * p; j < pos + page; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < page; i++)
            if (!num[i]) {
                maxPrime = pos + i;
                maxInd++;
            }
        return new long[]{maxInd, maxPrime};
    }

    public static long[] sieve(long limit, int page) {
        ArrayList<Long> r = primeByEuler(page);
        long maxInd = r.size(), maxPrime = r.get(r.size()-1);
        long[] l;
        for (var i = 1; i < limit / page; i++) {
            l = primeByEratosthenes(page * (long) i, page, r);
            maxInd+= l[0];
            maxPrime = l[1];
        }
        return new long[]{maxInd, maxPrime};
    }

    public static long[] sieve(long limit, int page, int threadCount) throws InterruptedException {
        if (threadCount <= 1) return sieve(limit, page);
        cd = new CountDownLatch(threadCount);
        ArrayList<Long> r = primeByEuler(page);
        long maxInd = r.size(), maxPrime = r.get(r.size()-1);

        class Task extends Thread{
            public long maxPrime;
            public long maxInd;
            int tid;
            public Task(int tid){
                this.tid = tid;
            }
            @Override
            public void run() {
                for (int i = tid ; i < limit/page ; i+= threadCount) {
                    if (i==0) continue;
                    var l = primeByEratosthenes(page * (long) i, page, r);
                    maxInd+= l[0];
                    maxPrime = l[1];
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
        return new long[]{maxInd, maxPrime};
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hi Prime! I'm Java :-)");
        long limit = Long.parseLong(args[0]);
        int page = Integer.parseInt(args[1]);
        int threadCount = Integer.parseInt(args[3]);
        System.out.println("Calculate prime numbers up to " + limit + " using partitioned Eratosthenes sieve");
        var startTime = System.currentTimeMillis();
        long[] r = sieve(limit, page, threadCount);
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Java finished within %.0e the %dth prime is %d, time cost: %d ms \n",
                (double) limit, r[0], r[1], totalTime);
    }
}