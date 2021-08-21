public class JHiPrime {
    private static final int maxKeep = 350000;
    private static final long[] primeArray = new long[maxKeep];
    private static long maxInd, maxPrime;

    private static void primeByEuler(Integer page) {
        var num = new boolean[page];
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
    }

    private static void primeByEratosthenes(Long pos, Integer page) {
        var num = new boolean[page];
        for (var i = 0;primeArray[i] != 0 && primeArray[i] * primeArray[i] < pos + page; i++) {
            var p = primeArray[i];
            for (var j = ((long) (Math.ceil((double) pos / p))) * p; j < pos + page; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < page; i++)
            if (!num[i]) {
                maxPrime = pos + i;
                maxInd++;
            }
    }

    public static void sieve(long limit, int page) {
        primeByEuler(page);
        for (var i = 1; i < limit / page; i++) primeByEratosthenes(page * (long) i, page);
    }

    public static void main(String[] args) {
        System.out.println("Hi Prime! I'm Java :-)");
        long limit = Long.parseLong(args[0]);
        int page = Integer.parseInt(args[1]);
        System.out.println("Calculate prime numbers up to " + limit + " using partitioned Eratosthenes sieve");
        var startTime = System.currentTimeMillis();
        sieve(limit, page);
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Java finished within %.0e the %dth prime is %d, time cost: %d ms \n",
                (double) limit, maxInd, maxPrime, totalTime);
    }
}