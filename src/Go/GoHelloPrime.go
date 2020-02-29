package main

import (
	"fmt"
	"math"
	"time"
)

func primeByEuler(limit uint64 ,prime *Prime) uint64 {
	top := 0
	num := make([]bool, limit+1)
	for  i := uint64(2); i < limit; i++ {
		if !num[i] {
			prime.add(i)
			top++
		}
		for j := 0; j < prime.size() && i * prime.get(j) <= limit; j++ {
			num[i * prime.get(j)] = true
			if i % prime.get(j) == 0 {break}
		}
	}
	return uint64(top)
}

func primeByEratosthenesInterval(pos uint64 , limit uint64 ,prime *Prime) uint64{
	top := 0
	num := make([]bool, limit)
	for i := 0; float64(prime.get(i)) < math.Sqrt(float64(pos+limit)) ; i++{
		p := prime.get(i)
		for j:= uint64(math.Ceil(float64(pos)/float64(p))*float64(p)); j<pos+limit; j+=p {
			num[j-pos] = true
		}
	}
	for i := 0; i< len(num); i++{
		if num[i] == false {
			prime.add(pos + uint64(i))
			top++
		}
	}
	return uint64(top)
}

func main() {
	fmt.Println("Hello Mr.Prime! I'm Go :-)")
	var page uint64 = 10_0000
	var repeat uint64 = 10_0000
	limit := page *repeat
	var prime Prime = *newPrime(limit)
	var top uint64 = 0
	var startTime time.Time

	fmt.Println("使用分区埃拉托色尼筛选法计算",dfString(limit),"以内素数：")
	startTime = time.Now()
	top += primeByEuler(page,&prime)
	prime.generateResults(page,top)

	for i := uint64(1); i < repeat; i++{
		pos := page * i
		top += primeByEratosthenesInterval(pos, page,&prime)
		prime.generateResults(pos + page, top)
	}
	totalTime := time.Now().Sub(startTime).Milliseconds()
	prime.printTable()
	fmt.Println(dfString(limit),"以内计算完毕。累计耗时：",totalTime,"毫秒")
}
