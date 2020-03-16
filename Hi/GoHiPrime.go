package main

import (
	"fmt"
	"math"
	"os"
	"strconv"
	"time"
)

const cache, _maxKeep uint64 = 80000, 80000

var page, repeat, _maxInd, _offSet, maxPrime uint64
var _prime [150000]uint64
var thread int

func primeByEuler(limit uint64) uint64 {
	top := 0
	num := make([]bool, limit+1)
	for i := uint64(2); i < limit; i++ {
		if !num[i] {
			_prime[_maxInd-_offSet] = i
			_maxInd++
			top++
		}
		for j := 0; j < int(_maxInd) && i*_prime[j] <= limit; j++ {
			num[i*_prime[j]] = true
			if i%_prime[j] == 0 {
				break
			}
		}
	}
	return uint64(top)
}

func primeByEratosthenesInterval(pos uint64, limit uint64) uint64 {
	top := 0
	num := make([]bool, limit)
	for i := 0; float64(_prime[i]) < math.Sqrt(float64(pos+limit)); i++ {
		p := _prime[i]
		for j := uint64(math.Ceil(float64(pos)/float64(p)) * float64(p)); j < pos+limit; j += p {
			num[j-pos] = true
		}
	}
	for i := 0; i < len(num); i++ {
		if num[i] == false {
			_prime[_maxInd-_offSet] = pos + uint64(i)
			_maxInd++
			top++
		}
	}
	return uint64(top)
}

func main() {
	fmt.Println("Hi Prime! I'm Go :-)")
	iPage, _ := strconv.Atoi(os.Args[1])
	page = uint64(iPage)
	irepeat, _ := strconv.Atoi(os.Args[2])
	repeat = uint64(irepeat)
	thread, _ = strconv.Atoi(os.Args[3])
	var top uint64 = 0
	var startTime time.Time

	fmt.Println("Calculate prime numbers up to", page*repeat, "using partitioned Eratosthenic sieve")
	startTime = time.Now()
	top += primeByEuler(page)

	if thread == 1 {
		fmt.Println("启动单线程模式")
		for i := uint64(1); i < repeat; i++ {
			pos := page * i
			top += primeByEratosthenesInterval(pos, page)
			if _maxInd > uint64(_maxKeep) {
				maxPrime = _prime[_maxInd-_offSet-1]
				_offSet = _maxInd - uint64(_maxKeep)
			}
		}
	} else {
		top = runMultiple(top)
	}
	totalTime := time.Now().Sub(startTime).Milliseconds()
	fmt.Printf("Go finished within %g; the %dth prime is %d, time cost: %d ms \n", float64(page*repeat), _maxInd, maxPrime, totalTime)
}

func runMultiple(top uint64) uint64 {
	fmt.Println("启动多线程模式，线程数量：", thread)
	var chs = make([]chan *[cache]uint64, thread)
	for i := 0; i < thread; i++ {
		chs[i] = make(chan *[cache]uint64)
		go runTask(i, chs[i])
	}
	okAll := true
	for okAll {
		okAll = false
		for i := 0; i < thread; i++ {
			var r, ok = <-chs[i]
			if !ok {
				break
			}
			okAll = true
			for k := range r {
				if r[k] == 0 {
					break
				}
				_prime[_maxInd-_offSet] = r[k]
				top++
			}
			if _maxInd > uint64(_maxKeep) {
				maxPrime = _prime[_maxInd-_offSet-1]
				_offSet = _maxInd - uint64(_maxKeep)
			}
		}
	}
	return top
}

func runTask(tid int, result chan *[cache]uint64) {
	for n := uint64(1 + tid); n < repeat; n += uint64(thread) {
		pos := page * n
		num := make([]bool, page)
		for i := 0; float64(_prime[i]) < math.Sqrt(float64(pos+page)); i++ {
			p := _prime[i]
			for j := uint64(math.Ceil(float64(pos)/float64(p)) * float64(p)); j < pos+page; j += p {
				num[j-pos] = true
			}
		}
		var rs [cache]uint64
		var top uint64 = 0
		for i := 0; i < len(num); i++ {
			if num[i] == false {
				rs[top] = pos + uint64(i)
				top++
			}
		}
		rsb := rs
		result <- &rsb
	}
	close(result)
}
