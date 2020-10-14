class GvHiPrime {
    static int maxKeep = 80000L
    def static primeArray = new long[maxKeep]
    static long maxInd,maxPrime

    static void primeByEuler(int page) {
        boolean[] num = new boolean[page]
        for (def i = 2; i < page; i++) {
            if (!num[i]) {
                maxPrime = i
                primeArray[maxInd++ as int] = maxPrime
            }
            for (def j = 0; j < maxInd &&  i * primeArray[j] < page; j++) {
                num[i * primeArray[j] as int] = true
                if (i % primeArray[j] == 0) break
            }
        }
    }

    static primeByEratosthenes( long pos,  int page) {
        def num = new boolean[page]
        for (int i = 0; primeArray[i] < Math.sqrt(pos + page); i++) {
            long p = primeArray[i]
            for (def j = ((Math.ceil(pos * 1.0 / p))) * p; j < pos + page; j += p)
                num[j - pos as int] = true
        }
        for (int i = 0; i < num.length; i++)
            if (!num[i]) {
                maxPrime = pos + i
                maxInd++
            }
    }

    static void sieve(long limit, int page){
        primeByEuler(page)
        for (def i = 1; i < limit/page; i++) primeByEratosthenes(page * i, page)
    }

    static void main(String[] args) {
        println("Hi Prime! I'm Groovy! :-)")
        long limit = args[0] as long
        int page = args[1] as int

        println "Calculate prime numbers up to $limit using partitioned Eratosthenic sieve"
        def startTime = System.currentTimeMillis()
        sieve(limit, page)
        def totalTime = System.currentTimeMillis() - startTime
        println "Java finished within ${String.format("%.0e", limit.toDouble())} the ${maxInd}th prime is $maxPrime, time cost: $totalTime ms"
    }
}