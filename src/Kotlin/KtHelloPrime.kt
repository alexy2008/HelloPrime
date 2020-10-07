import kotlin.math.ceil
import kotlin.math.pow

fun primeByEuler(page: Int, prime: KtPrime) {
    val num = BooleanArray(page)
    for (i in 2 until page) {
        if (!num[i]) prime.add(i.toLong())
        for (j in 0 until prime.size() ){
            if (i.toLong() * prime.getitem(j) >= page) break
            num[(i * prime.getitem(j)).toInt()] = true
            if (i % prime.getitem(j) == 0L) break
        }
    }
}

fun primeByEratosthenesInterval(pos: Long, page: Int, prime: KtPrime) {
    val num = BooleanArray(page)
    for (i in 0 until prime.size()) {
        val p = prime.getitem(i)
        if (p * p >= pos + page) break
        for (j in ceil(pos * 1.0 / p).toLong() until ((pos + page - 1) / p).toLong() + 1)
            num[(j * p - pos).toInt()] = true
    }
    for (i in num.indices) if (!num[i]) prime.add(pos + i)
}

fun main(args: Array<String>) {
    println("Hello sample.Prime! I'm Kotlin :-)")
    val limit = args[0].toLong()
    val page = args[1].toInt()
    val mode = args[2].toInt()
    val prime = KtPrime(limit, page, mode)

    println("使用分区埃拉托色尼筛选法计算${prime.getDfString(limit)}以内素数：")
    val costTime = kotlin.system.measureTimeMillis {
        //首先使用欧拉法得到种子素数组
        primeByEuler(page, prime)
        prime.generateResults(page.toLong())
        //循环使用埃拉托色尼法计算分区
        for (i in 1 until limit/page) {
            val pos = page * i
            primeByEratosthenesInterval(pos, page, prime)
            prime.generateResults(pos + page)
        }
    }
    prime.printTable()
    System.out.printf("Kotline finished within %.0e the %dth prime is %d; time cost: %d ms \n",
            limit.toDouble(), prime.maxInd, prime.maxPrime, costTime)
}

class KtPrime(limit: Long, page: Int, private val mode: Int) {
    var maxInd: Long = 0
    var maxPrime: Long = 0
    private val maxKeep: Int = (Math.sqrt(limit.toDouble()) / Math.log(Math.sqrt(limit.toDouble())) * 1.3).toInt()
    var reserve = ((Math.sqrt(limit.toDouble()) + page) / Math.log(Math.sqrt(limit.toDouble()) + page) * 1.3).toInt()
    private val primeList: ArrayList<Long> = ArrayList(reserve)
    private val seqList = ArrayList<String>()
    private val interList = ArrayList<String>()
    private var prevNo: Long = 0

    fun getitem(index: Int): Long {
        return primeList[index]
    }

    fun size(): Int {
        return primeList.size
    }

    fun add(p: Long) {
        primeList.add(p)
        maxInd++
    }

    fun generateResults(inter: Long) {
        if (mode > 0) {
            putSequence(prevNo)
            putInterval(inter)
            prevNo = maxInd
        }
        maxPrime = primeList[primeList.size - 1]
        freeUp()
    }

    private fun putInterval(inter: Long) {
        val s: String
        if (inter % 10.0.pow(inter.toString().length - 1.toDouble()) == 0.0) {
            s = "${getDfString(inter)}|$maxInd|${getitem(size() - 1)}"
            interList.add(s)
            if (mode > 1) println("[In:]$s")
        }
    }

    private fun putSequence(beginNo: Long) {
        for (i in beginNo.toString().length - 1 until maxInd.toString().length) {
            for (j in 1..9) {
                val seq = (j * 10.0.pow(i.toDouble())).toLong()
                if (seq < beginNo) continue
                if (seq >= maxInd) return
                val l = getitem(size() - 1 - (maxInd - seq).toInt())
                var s = getDfString(seq) + "|" + l
                seqList.add(s)
                if (mode > 1) println("==>[No:]$s")
            }
        }
    }

    private fun freeUp() {
        if (maxInd > maxKeep) primeList.subList(maxKeep, primeList.size - 1).clear()
    }

    fun printTable() {
        if (mode < 1) return
        println("## 素数序列表")
        println("序号|数值")
        println("---|---")
        seqList.forEach { x: String? -> println(x) }
        println("## 素数区间表")
        println("区间|个数|最大值")
        println("---|---|---")
        interList.forEach { x: String? -> println(x) }
    }

    fun getDfString(l: Long): String {
        var s = l.toString()
        when {
            l % 1000000000000L == 0L -> {
                s = s.substring(0, s.length - 12) + "万亿"
            }
            l % 100000000L == 0L -> {
                s = s.substring(0, s.length - 8) + "亿"
            }
            l % 10000 == 0L -> {
                s = s.substring(0, s.length - 4) + "万"
            }
        }
        return s
    }

    init {
        println("内存分配：$maxKeep")
    }
}