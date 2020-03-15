public class JHiPrime {
    private static int maxKeep = 80000;
    private static long[] primeArray = new long[maxKeep +70000];
    private static long offSet;
    private static long maxInd;
    private static long _maxPrime;

    private static void primeByEuler(int limit) {
        var num = new boolean[limit];
        for (var i = 2; i < limit; i++) {
            if (!num[i]) primeArray[(int) (maxInd++ - offSet)] = i;
            for (var j = 0; j < maxInd && (long) i * primeArray[j] < limit; j++) {
                num[(int) (i * primeArray[j])] = true;
                if (i % primeArray[j] == 0) break;
            }
        }
    }

    public static void primeByEratosthenes(Long pos, int limit) {
        var num = new boolean[limit];
        for (var i = 0; primeArray[i] < Math.sqrt(pos + limit); i++) {
            var p = primeArray[i];
            for (var j = ((long) (Math.ceil(pos * 1.0 / p))) * p; j < pos + (long) limit; j += p)
                num[(int) (j - pos)] = true;
        }
        for (var i = 0; i < num.length; i++)
            if (!num[i])  primeArray[(int) (maxInd++ - offSet)] = pos + i;
    }

    public static void main(String[] args) {
        System.out.println("Hi Prime! I'm Java :-)");
        int page = Integer.parseInt(args[0]);
        long repeat = Long.parseLong(args[1]);
        System.out.println("Calculate prime numbers up to "+ page * repeat +" using partitioned Eratosthenic sieve");
        var startTime = System.currentTimeMillis();
        primeByEuler(page);
        for (int i = 1; i < repeat; i++) {
            primeByEratosthenes(page * (long) i, page);
            if (maxInd > maxKeep){
                _maxPrime = primeArray[(int)(maxInd - offSet - 1)];
                offSet = maxInd - maxKeep;
            }
        }
        var totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Java finished within %.0e the %dth prime is %d, time cost: %d ms \n" ,
                            (double) (page * repeat), maxInd, _maxPrime, totalTime);
    }
}