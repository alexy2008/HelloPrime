<?php

$maxInd = 0;
$maxPrime = 0;
$offSet = 0;
$prime = array();

function primeByEuler($page){
    global $maxInd,$maxPrime,$prime,$offSet;
    $num = array();
    for ($i=0; $i<$page; $i++)  {$num[$i] = false; }
    for ($i = 2; $i < $page; $i++) {
        if (!$num[$i]) {
            $maxPrime = $i;
            $prime[$maxInd++ - $offSet] = $maxPrime;
        }
        for ($j = 0; $j < $maxInd && $i * $prime[$j] < $page; $j++) {
            $num[$i * $prime[$j]] = true;
            if ($i % $prime[$j] == 0) break;
        }
    }
}

function primeByEratosthenes($pos, $page){
    global $maxInd,$maxPrime,$prime;
    $num = array();
    for ($i=0; $i<$page; $i++)  {$num[$i] = false; }
    for ($i = 0; $prime[$i] * $prime[$i] < $pos + $page; $i++) {
        $p = $prime[$i];
        for ($j = ceil($pos * 1.0 / $p) * $p; $j < $pos + $page; $j += $p)
            $num[$j - $pos] = true;
    }
    for ($i = 0; $i < $page; $i++)
        if (!$num[$i]) {
            $maxPrime = $pos + $i;
            $maxInd++;
        }
}

function sieve($limit, $page){
    primeByEuler($page);
    for ($i = 1; $i < $limit/$page; $i++) {primeByEratosthenes($page*$i, $page);}
}

print "Hi Prime! I'm PHP :-)\n";
$limit = $argv[1];
$page = $argv[2];
print "Calculate prime numbers up to $limit using partitioned Eratosthenes sieve\n";

list($usec, $sec) = explode(" ", microtime());
$startTime = (float)$usec + (float)$sec;
sieve($limit,$page);
list($usec, $sec) = explode(" ", microtime());
$totalTime = ((float)$usec + (float)$sec - $startTime)*1000;
printf("Php finished within %.0e; the %sth prime is %s, time cost: %s ms \n",
            $limit, $maxInd, $maxPrime, round($totalTime));