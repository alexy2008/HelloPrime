package main

import (
	"fmt"
	"math"
	"os"
	"strconv"
	"time"
)

const _maxKeep uint64 = 80000

var _maxInd, _offSet, maxPrime uint64
var _prime [150000]uint64

func primeByEuler(page uint64) {
	num := make([]bool, page+1)
	for i := uint64(2); i < page; i++ {
		if !num[i] {
			maxPrime = i
			_prime[_maxInd-_offSet] = maxPrime
			_maxInd++
		}
		for j := 0; j < int(_maxInd) && i*_prime[j] <= page; j++ {
			num[i*_prime[j]] = true
			if i%_prime[j] == 0 {
				break
			}
		}
	}
}

func primeByEratosthenes(pos uint64, page uint64) {
	num := make([]bool, page)
	for i := 0; float64(_prime[i]) < math.Sqrt(float64(pos+page)); i++ {
		p := _prime[i]
		for j := uint64(math.Ceil(float64(pos)/float64(p)) * float64(p)); j < pos+page; j += p {
			num[j-pos] = true
		}
	}
	for i := 0; i < len(num); i++ {
		if num[i] == false {
			maxPrime = pos + uint64(i)
			_prime[_maxInd-_offSet] = maxPrime
			_maxInd++
		}
	}
}

func main() {
	fmt.Println("Hi Prime! I'm Go :-)")
	limit, _ := strconv.ParseUint(os.Args[1], 10, 64)
	page, _ := strconv.ParseUint(os.Args[2], 10, 64)
	var startTime time.Time

	fmt.Println("Calculate prime numbers up to", limit, "using partitioned Eratosthenic sieve")
	startTime = time.Now()
	primeByEuler(page)
	for i := uint64(1); i < limit/page; i++ {
		pos := page * i
		primeByEratosthenes(pos, page)
		if _maxInd > _maxKeep {
			_offSet = _maxInd - _maxKeep
		}
	}
	totalTime := time.Now().Sub(startTime).Milliseconds()
	fmt.Printf("Go finished within %g; the %dth prime is %d, time cost: %d ms \n",
		float64(limit), _maxInd, maxPrime, totalTime)
}
