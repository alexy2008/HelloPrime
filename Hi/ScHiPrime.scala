import scala.util.control.*

object ScHiPrime {
  def primeByEuler(page: Int) = {
    var sieve =  new Array[Boolean](page)
    var primeArray: List[Long] = List()
    val loop = new Breaks
    var i = 0
    for (i <- 2 until page) {
      if (!sieve(i)) primeArray = primeArray :+ i
      var j = 0
      loop.breakable {
        while (i * primeArray(j) < page) {
          sieve( i * primeArray(j).toInt) = true
          if (i % primeArray(j) == 0) loop.break
          j += 1
        }
      }
    }
    primeArray
  }

  def primeByEratosthenes(pos: Long, page: Int, primeArray: List[Long]) = {
    var sieve = new Array[Boolean](page)
    var maxInd, maxPrime, p = 0L
    for (p <- primeArray if p * p < pos + page) {
      var j = (pos.toDouble / p.toDouble).ceil.toLong * p
      while (j < pos + page) {
        sieve((j - pos).toInt) = true
        j += p
      }
    }
    var i = 0
    for (i <- 1 until page by 2) {
      if (!sieve(i)) {
        maxInd += 1
        maxPrime = pos + i
      }
    }
    (maxInd, maxPrime)
  }

  def calculate(limit: Long, page: Int) = {
    val primerList = primeByEuler(page)
    var maxInd: Long = primerList.length
    var maxPrime: Long = primerList.last
    for (i <- 1 until limit.toInt / page) {
      var (localMaxInd, localMaxPrime) = primeByEratosthenes(page * i, page, primerList)
      maxInd += localMaxInd
      maxPrime = localMaxPrime
    }
    (maxInd, maxPrime)
  }

  def main(args: Array[String]): Unit = {
    println("Hi Prime! I'm Scala :-)")
    val limit = args(0).toLong
    val page = args(1).toInt
    println("Calculate prime numbers up to " + limit + " using partitioned Eratosthenes sieve")
    val startTime = System.currentTimeMillis
    val (maxInd, maxPrime) = calculate(limit, page)
    val totalTime = System.currentTimeMillis - startTime
    printf("Scala finished within %.0e the %dth prime is %d, time cost: %d ms \n", limit.toDouble, maxInd, maxPrime, totalTime)
  }
}
