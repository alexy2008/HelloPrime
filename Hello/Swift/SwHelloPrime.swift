import Foundation

let lock = NSLock() // 使用锁来保证线程安全

func primeByEuler(_ page: Int) -> [Int] {
    var sieve = [Bool](repeating: false, count: page)
    var primeArray = [Int]()

    for i in 2..<page {
        if !sieve[i] {
            primeArray.append(i)
        }
        for j in 0..<primeArray.count {
            if i * primeArray[j] >= page {
                break
            }
            sieve[i * primeArray[j]] = true
            if i % primeArray[j] == 0 {
                break
            }
        }
    }
    return primeArray
}

func primeByEratosthenes(_ pos: Int, _ page: Int, _ primeArray: [Int]) -> (maxInd: Int, maxPrime: Int) {
    var sieve = [Bool](repeating: false, count: page)
    var maxInd = 0
    var maxPrime = 0
    let sqrLimit = Int(ceil(sqrt(Double(pos + page))))
    
    for i in 1..<primeArray.count {
        let p = primeArray[i]
        if p >= sqrLimit {
            break
        }
        for j in stride(from: ((pos + p - 1) / p) * p, to: pos + page, by: p) {
            sieve[j - pos] = true
        }
    }
    
    for i in stride(from: 1, to: page, by: 2) {
        if !sieve[i] {
            maxPrime = pos + i
            maxInd += 1
        }
    }
    
    return (maxInd, maxPrime)
}

func calculate(limit: Int, page: Int, threadNumber: Int)-> (maxInd: Int, maxPrime: Int) {
    var n = 1
    while page * n < Int(sqrt(Double(limit))) { n += 1 }
    let primeList = primeByEuler(page * n)
    var maxInd = primeList.count
    var maxPrime = primeList.last ?? 0
    // print("Info=> \(page * n) | \(maxInd) | \(maxPrime)")

    let group = DispatchGroup()
    let queue = DispatchQueue.global()

    for i in 0..<threadNumber {
        queue.async(group: group) {
            var localMaxInd = 0
            var localMaxPrime = 0
            for j in stride(from: i + n, to: limit / page, by: threadNumber) {
                let result = primeByEratosthenes(page * j, page, primeList)
                localMaxPrime = result.maxPrime
                localMaxInd += result.maxInd
                // print("Info=>|T\(i)| \((page * (j + 1))) | \(localMaxInd) | \(localMaxPrime)")
            }
            lock.lock()
            if localMaxPrime > maxPrime {
                maxPrime = localMaxPrime
            }
            maxInd += localMaxInd
            lock.unlock()
        }
    }
    group.wait()

    return (maxInd, maxPrime)
}

print("Hello Prime! I'm Swift :-)")
et limit = Int(CommandLine.arguments[1]) ?? 1000
let page = Int(CommandLine.arguments[2]) ?? 100
let threadNumber = Int(CommandLine.arguments[4]) ?? 1

print("Calculate prime numbers up to \(limit) using partitioned Eratosthenes sieve")

let startTime = CFAbsoluteTimeGetCurrent()
let result = calculate(limit: limit, page: page, threadNumber: threadNumber)
let totalTime = (CFAbsoluteTimeGetCurrent() - startTime) * 1000
print(String(format: "Swift using %d thread(s) finished in %.0e the %dth prime is %d, time cost: %.f ms",
                threadNumber, Double(limit), result.maxInd, result.maxPrime, totalTime))