const maxKeep: number = 80000
const primeArray: number[] = []
let maxInd = 0
let maxPrime = 0

function primeByEuler(page: number){
    let num = new Array<boolean>(page)
    for (let n of num) n = false
    for (let i = 2; i < page; i++) {
        if (!num[i]) {
            maxPrime = i
            primeArray[maxInd++] = maxPrime
        }
        for (let j = 0; j < maxInd && i * primeArray[j] < page; j++) {
            num[i * primeArray[j]] = true
            if (i % primeArray[j] == 0) break;
        }
    }
}

function primeByEratosthenes(pos: number, page: number){
    let num = new Array<boolean>(page)
    for (let n of num) n = false
    for (let i = 0; primeArray[i] < Math.sqrt(pos + page); i++) {
        let p = primeArray[i];
        for (let j =  Math.ceil(pos / p) * p; j < pos + page; j += p)
        num[ (j - pos)] = true;
    }
    for (let i = 0; i < page; i++)
        if (!num[i])  {
            maxPrime =  pos + i;
            maxInd ++;
        }
}

function sieve(limit :number, page :number){
    primeByEuler(page);
    for (let i = 1; i < limit/page; i++) primeByEratosthenes(page * i, page);

}

// import process = require('process');

console.log("Hi Prime! I'm TypeScript :-)")
let limit: number = parseInt(process.argv[2])
let page: number = parseInt(process.argv[3])
console.log(`Calculate prime numbers up to ${limit} using partitioned Eratosthenic sieve`)
let startTime = Date.now()
sieve(limit, page);
console.log(`Kotlin finished within ${limit.toExponential()} the ${maxInd}th prime is ${maxPrime}; time cost: ${Date.now() - startTime} ms`)