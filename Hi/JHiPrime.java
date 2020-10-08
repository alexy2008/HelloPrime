public class JHiPrime {
    private static final int maxKeep = 80000;
    private static final long[] primeArray = new long[maxKeep +70000];
    private static long offSet, maxInd, maxPrime ;

    private static void primeByEuler(int page) {
        var num = new boolean[page];
        for (var i = 2; i < page; i++) {
            if (!num[i]) {
                maxPrime = i;
                primeArray[(int) (maxInd++ - offSet)] = maxPrime;
            }
            for (var j = 0; j < maxInd && (long) i * primeArray[j] < page; j++) {
                num[(int) (i * primeArray[j])] = true;
                if (i % primeArray[j] == 0) break;
            }
        }
    }

    public static void primeByEratosthenes(Long pos, int page) {
        var num = new boolean[page];
        for (var i = 0; primeArray[i] < Math.sqrt(pos + page); i++) {
            var p = primeArray[i];
            for (var j = ((long) (Math.ceil(pos * 1.0 / p))) * p; j < pos + (long) page; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < page; i++)
            if (!num[i])  {
                maxPrime =  pos + i;
                primeArray[(int) (maxInd++ - offSet)] = maxPrime;
            }
    }

    public static void sieve(Long limit, int page){
        primeByEuler(page);
        for (int i = 1; i < limit/page; i++) {
            primeByEratosthenes(page * (long) i, page);
            if (maxInd > maxKeep) offSet = maxInd - maxKeep;
        }
    }

    public static void main(String[] args) {
        System.out.println("Hi Prime! I'm Java :-)");
        long limit = Long.parseLong(args[0]);
        int page = Integer.parseInt(args[1]);
        System.out.println("Calculate prime numbers up to "+ limit +" using partitioned Eratosthenic sieve");
        var startTime = System.currentTimeMillis();
        sieve(limit, page);
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Java finished within %.0e the %dth prime is %d, time cost: %d ms \n" ,
                            (double) limit, maxInd, maxPrime, totalTime);
    }
}