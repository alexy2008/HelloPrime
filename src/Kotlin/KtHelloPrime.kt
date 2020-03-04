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