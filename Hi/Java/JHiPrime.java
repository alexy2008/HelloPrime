import java.util.ArrayList;

public class JHiPrime {
    private static long maxInd, maxPrime;
    private static ArrayList<Long> primeArray;

    private static void primeByEuler(Integer page) {
        var sieve = new boolean[page];
        primeArray = new ArrayList<>();
        for (var i = 2; i < page; i++) {
            if (!sieve[i]){
                maxPrime = i;
                primeArray.add(maxPrime);
                maxInd++;
            }
            for (var j = 0; (long) i * primeArray.get(j) < page; j++) {
                sieve[(int) (i * primeArray.get(j))] = true;
                if (i % primeArray.get(j) == 0) break;
            }
        }
    }

    private static void primeByEratosthenes(Long pos, Integer page) {
        var sieve = new boolean[page];
        long sqrLimit = (long) Math.ceil(Math.sqrt((pos + page)));
        for (var i = 1; i < primeArray.size() && primeArray.get(i) < sqrLimit; i++) {
            var p = primeArray.get(i);
            for (var j = ((pos + p - 1) / p) * p; j < pos + page; j += p)
                sieve[(int) (j - pos)] = true;
        }
        for (var i = 1; i < page; i += 2)
            if (!sieve[i]) {
                maxPrime = pos + i;
                maxInd++;
            }
    }

    public static void calculate(long limit, int page) {
        primeByEuler(page);
        for (var i = 1; i < limit / page; i++) primeByEratosthenes(page * (long) i, page);
    }

    public static void main(String[] args) {
        System.out.println("Hi Prime! I'm Java :-)");
        long limit = Long.parseLong(args[0]);
        int page = Integer.parseInt(args[1]);
        System.out.println("Calculate prime numbers up to " + limit + " using partitioned Eratosthenes sieve");
        var startTime = System.currentTimeMillis();
        calculate(limit, page);
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Java finished within %.0e the %dth prime is %d, time cost: %d ms \n",
                (double) limit, maxInd, maxPrime, totalTime);
    }
}