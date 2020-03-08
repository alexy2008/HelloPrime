import kotlin.math.*
import kotlin.system.measureTimeMillis
import kotlin.time.microseconds
import kotlin.time.milliseconds

fun primeByEuler(limit: Int, prime: Prime): Int {
    var top = 0
    val num = BooleanArray(limit)
    for (i in 2 until limit) {
        if (!num[i]) {
            prime.add(i.toLong())
            top++
        }
        for (j in 0 until prime.size() ){
            if (i.toLong() * prime.getitem(j) >= limit) break
            num[(i * prime.getitem(j)).toInt()] = true
            if (i % prime.getitem(j) == 0L) break
        }
    }
    return top
}

fun primeByEratosthenesInterval(pos: Long, limit: Int, prime: Prime): Int {
    var top = 0
    val num = BooleanArray(limit)
    for (i in 0 until prime.size()) {
        val p = prime.getitem(i)
        if (p * p >= pos + limit) break
        for (j in ceil(pos * 1.0 / p).toLong() until ((pos + limit - 1) / p).toLong() + 1)
            num[(j * p - pos).toInt()] = true
    }
    for (i in num.indices) if (!num[i]) {
        prime.add(pos + i)
        top++
    }
    return top
}

fun main(args: Array<String>) {
    println("Hello sample.Prime! I'm Kotlin :-)")
    val page = args[0].toInt()
    val repeat = args[1].toLong()
    val isDebug = args[2].toBoolean()
    val prime = Prime(page.toLong(), repeat, isDebug)
    var top: Long = 0
    println("使用分区埃拉托色尼筛选法计算${prime.getDfString(page * repeat)}以内素数：")
    val costTime = kotlin.system.measureTimeMillis {
        //首先使用欧拉法得到种子素数组
        top += primeByEuler(page, prime).toLong()
        prime.generateResults(page.toLong(), top)
        //循环使用埃拉托色尼法计算分区
        for (i in 1 until repeat) {
            val pos = page * i
            top += primeByEratosthenesInterval(pos, page, prime).toLong()
            prime.generateResults(pos + page, top)
        }
    }
    prime.printTable()
    println("${prime.getDfString(page * repeat)} 以内计算完毕。累计耗时：${costTime} 毫秒")
}

class Prime(page: Long, repeat: Long, private val isDebug: Boolean) {
    private var maxInd: Long = 0
    private val maxKeep: Int = (sqrt(page * (repeat + 1).toDouble()) / ln(sqrt(page * (repeat + 1).toDouble())) * 1.3).toInt()
    private val primeList: ArrayList<Long> = ArrayList(maxKeep)
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

    fun generateResults(inter: Long, endNo: Long) {
        putSequence(prevNo, endNo)
        putInterval(inter)
        prevNo = endNo
        freeUp()
    }

    private fun putInterval(inter: Long) {
        val s: String
        if (inter % 10.0.pow(inter.toString().length - 1.toDouble()) == 0.0) {
            s = "${getDfString(inter)}|$maxInd|${getitem(size() - 1)}"
            interList.add(s)
            if (isDebug) println("[In:]$s")
        }
    }

    private fun putSequence(beginNo: Long, endNo: Long) {
        var s: String
        for (i in beginNo.toString().length - 1 until endNo.toString().length) {
            for (j in 1..9) {
                val seq = (j * 10.0.pow(i.toDouble())).toLong()
                if (seq < beginNo) continue
                if (seq >= endNo) return
                val l = getitem(size() - 1 - (endNo - seq).toInt())
                s = getDfString(seq) + "|" + l
                seqList.add(s)
                if (isDebug) println("==>[No:]$s")
            }
        }
    }

    private fun freeUp() {
        if (maxInd > maxKeep) primeList.subList(maxKeep, primeList.size - 1).clear()
    }

    fun printTable() {
        println("## 素数区间表")
        println("区间|个数|最大值")
        println("---|---|---")
        interList.forEach { x: String? -> println(x) }
        println("## 素数序列表")
        println("序号|数值")
        println("---|---")
        seqList.forEach { x: String? -> println(x) }
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