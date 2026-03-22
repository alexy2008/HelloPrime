import kotlin.math.ceil
import kotlin.math.sqrt
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

class KtHelloPrime {
    companion object {
        private var isDebug = false

        fun primeByEuler(page: Int): List<Long> {
            val sieve = BooleanArray(page)
            val primeArray = mutableListOf<Long>()
            for (i in 2 until page) {
                if (!sieve[i]) {
                    primeArray.add(i.toLong())
                }
                for (j in 0 until primeArray.size) {
                    if (i * primeArray[j] >= page.toLong()) break
                    sieve[(i * primeArray[j]).toInt()] = true
                    if (i % primeArray[j] == 0L) break
                }
            }
            return primeArray
        }

        data class Result(val maxInd: Long, val maxPrime: Long)

        fun primeByEratosthenes(pos: Long, page: Int, primeArray: List<Long>): Result {
            val sieve = BooleanArray(page)
            var maxInd = 0L
            var maxPrime = 0L
            val sqrLimit = ceil(sqrt((pos + page).toDouble())).toLong()

            for (i in 1 until primeArray.size) {
                if (primeArray[i] >= sqrLimit) break
                val p = primeArray[i]
                var start = ((pos + p - 1L) / p) * p
                for (j in start until pos + page step p) {
                    sieve[(j - pos).toInt()] = true
                }
            }

            for (i in 1 until page step 2) {
                if (!sieve[i]) {
                    maxPrime = pos + i.toLong()
                    maxInd++
                }
            }

            return Result(maxInd, maxPrime)
        }

        fun calculateSingle(limit: Long, page: Int): Result {
            var n = 1
            while (page * n < sqrt(limit.toDouble())) n++
            val primerList = primeByEuler(page * n)
            var maxInd = primerList.size.toLong()
            var maxPrime = primerList.last()

            for (j in n until limit / page) {
                val rs = primeByEratosthenes(page * j.toLong(), page, primerList)
                maxPrime = rs.maxPrime
                maxInd += rs.maxInd
            }

            return Result(maxInd, maxPrime)
        }

        fun calculate(limit: Long, page: Int, threadNumber: Int): Result {
            var n = 1
            while (page * n < sqrt(limit.toDouble())) n++
            val primerList = primeByEuler(page * n)
            val maxInd = AtomicLong(primerList.size.toLong())
            val maxPrime = AtomicLong(primerList.last())

            if (isDebug) println("Info=>|$page$n|$maxInd|$maxPrime|")

            val tasks = Array(threadNumber) { Thread() }
            for (i in 0 until threadNumber) {
                val tid = i
                val finalN = n
                tasks[tid] = thread {
                    var localMaxPrime = 0L
                    var localMaxInd = 0L
                    for (j in (tid + finalN).toLong() until (limit / page) step threadNumber.toLong()) {
                        val rs = primeByEratosthenes(page * j.toLong(), page, primerList)
                        localMaxPrime = rs.maxPrime
                        localMaxInd += rs.maxInd
                        if (isDebug) {
                            println("Info=>|T$tid|${page * (j + 1)}|$localMaxInd|$localMaxPrime|")
                        }
                    }
                    maxPrime.accumulateAndGet(localMaxPrime, ::maxOf)
                    maxInd.addAndGet(localMaxInd)
                }
            }

            tasks.forEach { it.join() }

            return Result(maxInd.get(), maxPrime.get())
        }

        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello Prime! I'm Kotlin :-)")
            val limit = args[0].toLong()
            val page = args[1].toInt()
            isDebug = args[2].toInt() > 0
            val threadNumber = args[3].toInt()
            println("Calculate prime numbers up to $limit using partitioned Eratosthenes sieve")
            val startTime = System.currentTimeMillis()
            val r = if (threadNumber == 1) calculateSingle(limit, page) else calculate(limit, page, threadNumber)
            val totalTime = System.currentTimeMillis() - startTime
            println("Kotlin using $threadNumber thread(s) finished within ${limit.toDouble()}e, the ${r.maxInd}th prime is ${r.maxPrime}, time cost: $totalTime ms")
        }
    }
}
