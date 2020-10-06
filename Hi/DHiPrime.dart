import 'dart:math';

const maxKeep = 80000;
var primeArray = List<int>();
var maxInd = 0, maxPrime = 0;

void primeByEuler(int page) {
  var num = List<bool>(page);
  num.fillRange(0, page, false);
  for (var i = 2; i < page; i++) {
    if (!num[i]) {
      maxPrime = i;
      primeArray.add(maxPrime);
      maxInd++;
    }
    for (var j = 0; j < maxInd && i * primeArray[j] < page; j++) {
      num[i * primeArray[j]] = true;
      if (i % primeArray[j] == 0) break;
    }
  }
}

void primeByEratosthenes(int pos, int page) {
  var num = List<bool>(page);
  num.fillRange(0, page, false);
  for (var i = 0; primeArray[i] < sqrt(pos + page); i++) {
    var p = primeArray[i];
    for (var j = (pos * 1.0 / p).ceil() * p; j < pos + page; j += p)
      num[j - pos] = true;
  }
  for (var i = 0; i < num.length; i++)
    if (!num[i]) {
      maxPrime = pos + i;
      primeArray.add(maxPrime);
      maxInd++;
    }
}

void sieve(int limit, int page) {
  primeByEuler(page);
  for (int i = 1; i < limit / page; i++) {
    primeByEratosthenes(page * i, page);
    if (maxInd > maxKeep)
      primeArray.removeRange(maxKeep, primeArray.length - 1);
  }
}

void main(List<String> args) {
  print("Hi Prime! I'm Dart :-)");
  var limit = int.parse(args[0]);
  var page = int.parse(args[1]);
  print(
      "Calculate prime numbers up to $limit using partitioned Eratosthenic sieve");
  var startTime = new DateTime.now().millisecondsSinceEpoch;
  sieve(limit, page);
  var totalTime = new DateTime.now().millisecondsSinceEpoch - startTime;
  print(
      "Dart finished within ${limit.toStringAsExponential()} the ${maxInd}th prime is $maxPrime, time cost: $totalTime ms");
}