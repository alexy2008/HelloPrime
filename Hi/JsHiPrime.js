var maxKeep = 80000;
var primeArray = [];
var maxInd = 0;
var maxPrime = 0;
function primeByEuler(page) {
    var num = new Array(page);
    for (var i = 2; i < page; i++) {
        if (!num[i]) {
            maxPrime = i;
            primeArray[maxInd++] = maxPrime;
        }
        for (var j = 0; j < maxInd && i * primeArray[j] < page; j++) {
            num[i * primeArray[j]] = true;
            if (i % primeArray[j] === 0)
                break;
        }
    }
}
function primeByEratosthenes(pos, page) {
    var num = new Array(page);
    for (var i = 0; primeArray[i] < Math.sqrt(pos + page); i++) {
        var p = primeArray[i];
        for (var j = Math.ceil(pos / p) * p; j < pos + page; j += p)
            num[(j - pos)] = true;
    }
    for (var i = 0; i < page; i++)
        if (!num[i]) {
            maxPrime = pos + i;
            maxInd++;
        }
}
function sieve(limit, page) {
    primeByEuler(page);
    for (var i = 1; i < limit / page; i++)
        primeByEratosthenes(page * i, page);
}
// import process = require('process');
console.log("Hi Prime! I'm JavaScript :-)");
var limit = parseInt(process.argv[2]);
var page = parseInt(process.argv[3]);
console.log("Calculate prime numbers up to " + limit + " using partitioned Eratosthenes sieve");
var startTime = Date.now();
sieve(limit, page);
console.log("JavaScript finished within " + limit.toExponential() + " the " + maxInd + "th prime is " + maxPrime + "; time cost: " + (Date.now() - startTime) + " ms");
