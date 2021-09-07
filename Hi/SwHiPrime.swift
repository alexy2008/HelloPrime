import Foundation

let maxKeep = 80000
var maxInd = 0, maxPrime = 0
var primeArray = [Int](repeating: 0, count: maxKeep)

func primeByEuler(page: Int){
    var num = [Bool](repeating: false, count: page)
    for i in 2..<page {
        if (!num[i]){
            maxPrime = i
            primeArray[maxInd] = maxPrime
            maxInd += 1
        }
        for j in 0..<maxInd {
            if i * primeArray[j] >= page { break }
            num[i * primeArray[j]] = true
            if i % primeArray[j] == 0 { break }
        }
    }
}

func primeByEratosthenes(pos: Int, page: Int){
    var num = [Bool](repeating: false, count: page)
    for i in 0..<maxKeep {
        let p = primeArray[i]
        if p * p >= pos + page {break}
        for j in Int(ceil(Double(pos)/Double(p)))..<(((pos + page - 1) / p) + 1){
            num[j * p - pos] = true
        }
    }
    for i in 0..<page{
        if !num[i] {
            maxPrime = pos + i
            maxInd += 1
        }
    }
}

func sieve(limit: Int, page: Int) {
    primeByEuler(page: page)
    for i in 1..<limit/page {
        primeByEratosthenes(pos: page * i, page: page)
    }
}

print("Hi Prime! I'm Swift :-)")
let limit: Int = Int(CommandLine.arguments[1]) ?? 1000
let page: Int = Int(CommandLine.arguments[2]) ?? 100
print("Calculate prime numbers up to \(limit) using partitioned Eratosthenic sieve")
let startTime = Date.timeIntervalSinceReferenceDate * 1000
sieve(limit: limit, page: page)
let costTime = Int(Date.timeIntervalSinceReferenceDate * 1000 - startTime)
print("Swift finished within \(String(format: "%.0e",Double(limit))) the \(maxInd)th prime is \(maxPrime); time cost: \(costTime) ms ")