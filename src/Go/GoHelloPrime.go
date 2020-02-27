package main

import (
	"fmt"
	"math"
	"time"
)

func primeByEuler(limit int , prime *[]uint64) int {
	num := make([]bool, limit+1)
	for i := 2; i < limit; i++ {
		if num[i] == false { *prime = append(*prime, uint64(i)) }
		for j := 0; j < len(*prime) && uint64(i) * (*prime)[j] <= uint64(limit); j++ {
			num[i * int((*prime)[j])] = true
			if i % int((*prime)[j]) == 0 {break}
		}
	}
	return len(*prime)
}

func primeByEratosthenesInterval(pos uint64 , limit int , prime *[]uint64) int{
	top := 0
	num := make([]bool, limit)
	for i := 0; float64((*prime)[i]) < math.Sqrt(float64(pos+uint64(limit))) ; i++{
		p := (*prime)[i]
		for j:= uint64(math.Ceil(float64(pos)/float64(p))*float64(p)); j<pos+uint64(limit); j+=p {
			num[int(j-pos)] = true
		}
	}
	for i := 0; i< len(num); i++{
		if num[i] == false {
			*prime = append(*prime, pos+uint64(i))
			top++
		}
	}
	return top
}

func dfString(l uint64)  string {
	s := fmt.Sprintf("%d",l)
	if l % (10000*10000) == 0  {
		s = s[:len(s)-8] + "亿"
	}	else if l % 10000 == 0 {
		s = s[:len(s) -4] + "万"
	}
	return s
}

func main() {
	fmt.Println("Hello Mr.Prime! I'm Go :-)")
	step := 10_0000
	repeat := 10_0000
	limit := step*repeat
	p := 0
	maxKeep := int(math.Sqrt(float64(limit)) / math.Log(math.Sqrt(float64(limit)))* 1.3)
	maxPrime := uint64(0)
	var prime []uint64
	var startTime time.Time

	fmt.Println("使用分区筛选法计算",dfString(uint64(limit)),"以内素数：")

	startTime = time.Now()
	p += primeByEuler(step,&prime)
	maxPrime =  prime[len(prime)-1]

	for i := 1; i < repeat; i++{
		pos := uint64(step * i)
		p += primeByEratosthenesInterval(pos,step,&prime)
		maxPrime =  prime[len(prime)-1]
		if len(prime) > maxKeep {
			prime = prime[:maxKeep]
		}
	}
	fmt.Println("共", p ,"个,最大质数：", maxPrime,"耗时：",time.Now().Sub(startTime).Milliseconds(),"毫秒")
}
