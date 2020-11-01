import java.util.ArrayList;

public class JHiPrime {
    protected static final int maxKeep = 670000;
    protected static ArrayList<Long> primeList = new ArrayList<>(maxKeep);
    protected long maxInd, maxPrime;

    protected static void primeByEuler(Integer page) {
        var num = new boolean[page];
        for (var i = 2; i < page; i++) {
            if (!num[i]) {
                primeList.add((long) i);
            }
            for (var j = 0; (long) i * primeList.get(j) < page; j++) {
                num[(int) (i * primeList.get(j))] = true;
                if (i % primeList.get(j) == 0) break;
            }
        }
    }

    protected void primeByEratosthenes(Long pos, Integer page) {
        var num = new boolean[page];
        for (var i = 0; primeList.get(i) * primeList.get(i) < pos + (long)page; i++) {
            var p = primeList.get(i);
            for (var j = ((long) (Math.ceil((double) pos / p))) * p; j < pos + page; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < page; i++)
            if (!num[i]) {
                maxPrime = pos + i;
                maxInd++;
            }
    }

    public void sieve(long limit, int page) {
        primeByEuler(page);
        maxInd = primeList.size();
        maxPrime = primeList.get((int) maxInd - 1);
        for (var i = 1; i < limit / page; i++) primeByEratosthenes(page * (long) i, page);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hi Prime! I'm Java :-)");
        long limit = Long.parseLong(args[0]);
        int page = Integer.parseInt(args[1]);
        var hi = new JHiPrime();
        System.out.println("Calculate prime numbers up to " + limit + " using partitioned Eratosthenes sieve");
        var startTime = System.currentTimeMillis();
        hi.sieve(limit, page);
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Java finished within %.0e the %dth prime is %d, time cost: %d ms \n",
                (double) limit, hi.maxInd, hi.maxPrime, totalTime);
    }
}