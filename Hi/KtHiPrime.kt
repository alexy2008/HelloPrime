class KtHiPrime {
    var maxInd: Long = 0
    var maxPrime: Long = 0
    val maxKeep: Int = 80000
    val primeArry: ArrayList<Long> = ArrayList(maxKeep)

    fun primeByEuler(page: Int) {
        val num = BooleanArray(page)
        for (i in 2 until page) {
            if (!num[i]) {
                maxPrime = i.toLong()
                primeArry.add(maxPrime)
                maxInd++
            }
            for (p in primeArry) {
                if (i * p >= page) break
                num[i * p.toInt()] = true
                if (i % p == 0L) break
            }
        }
    }

    fun primeByEratosthenes(pos: Long, page: Int) {
        val num = BooleanArray(page)
        for (p in primeArry){
            if (p * p >= pos + page) break
            for (j in kotlin.math.ceil(pos * 1.0 / p).toLong() * p until pos + page step p)
                num[(j - pos).toInt()] = true
        }

        for ((i, n) in num.withIndex()) if (!n) {
            maxPrime = pos + i
            maxInd++
        }
    }

    fun sieve(limit: Long, page: Int) {
        primeByEuler(page)
        for (i in 1 until limit / page) primeByEratosthenes(page * i, page)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Hi Prime! I'm Kotlin :-)")
            val limit = args[0].toLong()
            val page = args[1].toInt()
            val hi = KtHiPrime()
            println("Calculate prime numbers up to $limit using partitioned Eratosthenic sieve")
            val costTime = kotlin.system.measureTimeMillis {
                hi.sieve(limit, page)
            }
            println("Kotlin finished within ${String.format("%.0e", limit.toDouble())} the ${hi.maxInd}th prime is ${hi.maxPrime}; time cost: $costTime ms ")
        }
    }
}