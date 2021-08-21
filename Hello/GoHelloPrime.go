package main

import (
	"fmt"
	"math"
	"os"
	"strconv"
	"time"
)

func primeByEuler(page int) []int {
	num := make([]bool, page)
	var primeArray []int
	for i := 2; i < page; i++ {
		if !num[i] {
			primeArray = append(primeArray, i)
		}
		for j := 0; i*primeArray[j] < page; j++ {
			num[i*primeArray[j]] = true
			if i%primeArray[j] == 0 {
				break
			}
		}
	}
	return primeArray
}

func primeByEratosthenes(pos int, page int, primeArray []int) (int, int) {
	num := make([]bool, page)
	var maxInd, maxPrime int
	for i := 1; i < len(primeArray) && float64(primeArray[i]) < math.Sqrt(float64(pos+page)); i++ {
		p := primeArray[i]
		for j := int(math.Ceil(float64(pos)/float64(p)) * float64(p)); j < pos+page; j += p {
			num[j-pos] = true
		}
	}
	for i := 1; i < len(num); i += 2 {
		if num[i] == false {
			maxPrime = pos + i
			maxInd++
		}
	}
	return maxInd, maxPrime
}

func calculate(limit int, page int, threadNumber int) (int, int) {
	primeArray := primeByEuler(page)
	maxInd, maxPrime := len(primeArray), primeArray[(len(primeArray)-1)]
	chanMaxInd := make(chan int, threadNumber)
	chanMaxPrime := make(chan int, threadNumber)

	for i := 0; i < threadNumber; i++ {
		tid := i
		go func() {
			var localMaxPrime, localMaxInd int
			for j := tid + 1; j < limit/page; j += threadNumber {
				ind, mp := primeByEratosthenes(page*j, page, primeArray)
				localMaxPrime = mp
				localMaxInd += ind
			}
			chanMaxInd <- localMaxInd
			chanMaxPrime <- localMaxPrime
		}()
	}
	for i := 0; i < threadNumber; i++ {
		maxInd += <-chanMaxInd
		mp := <-chanMaxPrime
		if mp > maxPrime {
			maxPrime = mp
		}
	}
	return maxInd, maxPrime
}

func main() {
	fmt.Println("Hello Prime! I'm Go :-)")
	limit, _ := strconv.ParseInt(os.Args[1], 10, 64)
	page, _ := strconv.ParseInt(os.Args[2], 10, 64)
	threadNumber, _ := strconv.ParseInt(os.Args[4], 10, 64)
	fmt.Println("Calculate prime numbers up to", limit, "using partitioned Eratosthenes sieve")
	startTime := time.Now()
	maxInd, maxPrime := calculate(int(limit), int(page), int(threadNumber))
	totalTime := time.Now().Sub(startTime).Milliseconds()
	fmt.Printf("Go using %d thread(s) finished within %g; the %dth prime is %d, time cost: %d ms \n",
		threadNumber, float64(limit), maxInd, maxPrime, totalTime)
}
