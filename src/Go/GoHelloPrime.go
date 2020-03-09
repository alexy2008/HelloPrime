package main

import (
	"fmt"
	"math"
	"os"
	"strconv"
	"time"
)

const cache uint64 = 80000
const thread  = 10

func primeByEuler(limit uint64, prime *Prime) uint64 {
	top := 0
	num := make([]bool, limit+1)
	for i := uint64(2); i < limit; i++ {
		if !num[i] {
			prime.add(i)
			top++
		}
		for j := 0; j < prime.size() && i*prime.get(j) <= limit; j++ {
			num[i*prime.get(j)] = true
			if i%prime.get(j) == 0 { break }
		}
	}
	return uint64(top)
}

func primeByEratosthenesInterval(pos uint64, limit uint64, prime *Prime) uint64 {
	top := 0
	num := make([]bool, limit)
	for i := 0; float64(prime.get(i)) < math.Sqrt(float64(pos+limit)); i++ {
		p := prime.get(i)
		for j := uint64(math.Ceil(float64(pos)/float64(p)) * float64(p)); j < pos+limit; j += p {
			num[j-pos] = true
		}
	}
	for i := 0; i < len(num); i++ {
		if num[i] == false {
			prime.add(pos + uint64(i))
			top++
		}
	}
	return uint64(top)
}

func main_s() {
	fmt.Println("Hello Mr.Prime! I'm Go :-)")
	ipage, _ := strconv.Atoi(os.Args[1])
	page := uint64(ipage)
	irepeat, _ := strconv.Atoi(os.Args[2])
	repeat := uint64(irepeat)
	isDebug, _ = strconv.ParseBool(os.Args[3])
	var limit uint64 = uint64(page * repeat)
	var prime Prime = *newPrime(page, repeat)
	var top uint64 = 0
	var startTime time.Time

	fmt.Println("使用分区埃拉托色尼筛选法计算", dfString(limit), "以内素数：")
	startTime = time.Now()
	top += primeByEuler(page, &prime)
	prime.generateResults(page, top)

	for i := uint64(1); i < repeat; i++ {
		pos := page * i
		top += primeByEratosthenesInterval(pos, page, &prime)
		prime.generateResults(pos+page, top)
	}
	totalTime := time.Now().Sub(startTime).Milliseconds()
	prime.printTable()
	fmt.Println("Go finished within", fmt.Sprintf("%g", float64(limit)), "; time cost:", totalTime, "ms")
}

func runTask(tid int, page uint64, repeat uint64, prime *Prime, result chan *[cache]uint64)  {

	for n := uint64(1 + tid ); n < repeat; n+= uint64(thread) {
		pos := page * n
		num := make([]bool, page)
		for i := 0; float64(prime.get(i)) < math.Sqrt(float64(pos+page)); i++ {
			p := prime.get(i)
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

func main() {
	fmt.Println("Hello Mr.Prime! I'm Go :-)")
	ipage, _ := strconv.Atoi(os.Args[1])
	page := uint64(ipage)
	irepeat, _ := strconv.Atoi(os.Args[2])
	repeat := uint64(irepeat)
	isDebug, _ = strconv.ParseBool(os.Args[3])
	var limit uint64 = uint64(page * repeat)
	var prime Prime = *newPrime(page, repeat)
	var top uint64 = 0
	var startTime time.Time

	fmt.Println("使用分区埃拉托色尼筛选法计算", dfString(limit), "以内素数：")
	startTime = time.Now()
	top += primeByEuler(page, &prime)
	prime.generateResults(page, top)

	var chs [thread]chan *[cache]uint64
	for i:= 0; i < thread; i++ {
		chs[i] = make(chan *[cache]uint64)
		go runTask(i,page,repeat,&prime, chs[i])
	}

	okAll := true
	for m := uint64(0); okAll ;m += thread {
		okAll = false
		for i:= 0; i < thread; i++ {
			//n := uint64(1 + i )
			var r,ok = <- chs[i]
			if !ok {break}
			okAll = true
			for k := range r {
				if r[k] == 0 {break}
				prime.add(r[k])
				//println("线程",i,"第",m,"次添加top: ",top,",",k," = ",r[k])
				top++
			}
			//println(i," : ",page*(uint64(i) + m + 2))
			prime.generateResults(page*(uint64(i) + m + 2) , top) //todo
		}
	}

	totalTime := time.Now().Sub(startTime).Milliseconds()
	prime.printTable()
	fmt.Println("Go finished within", fmt.Sprintf("%g", float64(limit)), "; time cost:", totalTime, "ms")
}
