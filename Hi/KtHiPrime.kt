class KtHiPrime {
    val maxKeep: Int = 80000
    val primeArry: ArrayList<Long> = ArrayList(maxKeep)
    var maxInd: Long = 0
    var maxPrime: Long = 0

    fun primeByEuler(page: Int) {
        val num = BooleanArray(page)
        for (i in 2 until page) {
            if (!num[i]) {
                maxPrime = i.toLong()
                primeArry.add(maxPrime)
                maxInd++
            }
            for (j in 0 until primeArry.size) {
                if (i.toLong() * primeArry[j] >= page) break
                num[(i * primeArry[j]).toInt()] = true
                if (i % primeArry[j] == 0L) break
            }
        }
    }

fun primeByEratosthenes(pos: Long, page: Int) {
    val num = BooleanArray(page)
    for (i in 0 until primeArry.size) {
        val p = primeArry[i]
        if (p * p >= pos + page) break
        for (j in kotlin.math.ceil(pos * 1.0 / p).toLong() until ((pos + page - 1) / p) + 1)
            num[(j * p - pos).toInt()] = true
    }
    for (i in num.indices) if (!num[i]){
        maxPrime = pos + i
        maxInd ++
    }
}

fun sieve(limit: Long, page: Int){
    primeByEuler(page)
    for (i in 1 until limit/page) primeByEratosthenes(page * i, page)
}

    fun main(args: Array<String>) {
        println("Hi Prime! I'm Kotlin :-)")
        val limit = args[0].toLong()
        val page = args[1].toInt()
        println("Calculate prime numbers up to $limit using partitioned Eratosthenic sieve")
        val costTime = kotlin.system.measureTimeMillis {
            sieve(limit, page)
        }
        println("Kotlin finished within ${String.format("%.0e", limit.toDouble())} the $maxInd th prime is $maxPrime; time cost: $costTime ms ")
    }
}