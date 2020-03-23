package main

import (
	"fmt"
	"math"
	"os"
	"strconv"
	"time"
)

const cache uint64 = 80000

var thread, mode int
var page, limit uint64
var prime Prime

func primeByEuler(page uint64){
	num := make([]bool, page+1)
	for i := uint64(2); i < page; i++ {
		if !num[i] { prime.add(i) }
		for j := 0; j < prime.size() && i*prime.get(j) <= page; j++ {
			num[i*prime.get(j)] = true
			if i%prime.get(j) == 0 {break}
		}
	}
}

func primeByEratosthenesInterval(pos uint64, page uint64) {
	num := make([]bool, page)
	for i := 0; float64(prime.get(i)) < math.Sqrt(float64(pos+page)); i++ {
		p := prime.get(i)
		for j := uint64(math.Ceil(float64(pos)/float64(p)) * float64(p)); j < pos+page; j += p {
			num[j-pos] = true
		}
	}
	for i := 0; i < len(num); i++ {
		if num[i] == false { prime.add(pos + uint64(i))	}
	}
}

func main() {
	fmt.Println("Hello Mr.Prime! I'm Go :-)")
	limit, _ = strconv.ParseUint(os.Args[1], 10, 64)
	page, _ = strconv.ParseUint(os.Args[2], 10, 64)
	mode, _ = strconv.Atoi(os.Args[3])
	thread, _ = strconv.Atoi(os.Args[4])
	prime = *newPrime()
	var startTime time.Time

	fmt.Println("使用分区埃拉托色尼筛选法计算", dfString(limit), "以内素数：")
	startTime = time.Now()
	primeByEuler(page)
	prime.generateResults(page)

	if thread == 1 {
		fmt.Println("启动单线程模式")
		for i := uint64(1); i < limit/page; i++ {
			pos := page * i
			primeByEratosthenesInterval(pos, page)
			prime.generateResults(pos+page)
		}
	} else {
		runMultiple()
	}

	totalTime := time.Now().Sub(startTime).String()
	prime.printTable()
	fmt.Printf("Go finished within %g; the %dth prime is %d, time cost: %s ms \n",
		float64(limit), prime._maxInd, prime._maxPrime, totalTime)
}

func runMultiple() {
	fmt.Println("启动多线程模式，线程数量：", thread)
	var chs = make([]chan *[cache]uint64, thread)
	for i := 0; i < thread; i++ {
		chs[i] = make(chan *[cache]uint64)
		go runTask(i, chs[i])
	}
	okAll := true
	for m := uint64(0); okAll; m += uint64(thread) {
		okAll = false
		for i := 0; i < thread; i++ {
			var r, ok = <-chs[i]
			if !ok {break}
			okAll = true
			for k := range r {
				if r[k] == 0 {break}
				prime.add(r[k])
			}
			prime.generateResults(page*(uint64(i)+m+2)) //todo
		}
	}
}

func runTask(tid int, result chan *[cache]uint64) {

	for n := uint64(1 + tid); n < limit/page; n += uint64(thread) {
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

type Prime struct {
	_maxInd   uint64
	_maxPrime uint64
	_maxKeep  int
	seqList   *[]string
	interList *[]string
	prevNo    uint64
	_prime    *[]uint64
	_offSet   uint64
}

func newPrime() *Prime {
	var p Prime
	p._maxKeep = int(math.Sqrt(float64(limit)) / math.Log(math.Sqrt(float64(limit))) * 1.3)
	sp := make([]uint64, p._maxKeep+800000)
	p._prime = &sp
	p.interList = &[]string{}
	p.seqList = &[]string{}
	return &p
}

func (p Prime) get(index int) uint64 {
	return (*p._prime)[index]

}

func (p *Prime) add(n uint64) {
	(*p._prime)[(*p)._maxInd-(*p)._offSet] = n
	(*p)._maxInd++
}

func (p *Prime) size() int {
	return int((*p)._maxInd)
}

func (p *Prime) generateResults(inter uint64) {
	if mode > 0 {
		(*p).outputSequence(p.prevNo)
		(*p).outputInterval(inter)
		(*p).prevNo = (*p)._maxInd
	}
	(*p)._maxPrime = (*p._prime)[(*p)._maxInd - (*p)._offSet - 1]
	(*p).freeUp()
}

func (p Prime) outputInterval(inter uint64) {
	var s string
	if inter%uint64(math.Pow10(len(fmt.Sprintf("%d", inter))-1)) == 0 {
		s = fmt.Sprintf("%s|%d|%d", dfString(inter), p._maxInd, (*p._prime)[p._maxInd-p._offSet-1])
		*p.interList = append(*p.interList, s)
		if mode > 1 { fmt.Println("[In:]", s) }
	}
}

func (p Prime) outputSequence(beginNo uint64) {
	var s string
	for i := len(fmt.Sprintf("%d", beginNo)) - 1; i <= len(fmt.Sprintf("%d", p._maxInd))-1; i++ {
		for j := 1; j < 10; j++ {
			seq := uint64(j) * uint64(math.Pow10(i))
			if seq < beginNo { continue	}
			if seq >= p._maxInd { break	}
			s = fmt.Sprintf("%s|%d", dfString(seq), (*p._prime)[p._maxInd - p._offSet-1-(p._maxInd-seq)])
			*p.seqList = append(*p.seqList, s)
			if mode > 1 { fmt.Println("==>[No:]", s) }
		}
	}
}

func (p *Prime) freeUp() {
	if len(*p._prime) > (*p)._maxKeep {
		(*p)._offSet = (*p)._maxInd - uint64((*p)._maxKeep)
	}
}

func (p Prime) printTable() {
	if mode < 1 { return }
	fmt.Println("## 素数序列表")
	fmt.Println("序号|数值")
	fmt.Println("---|---")
	for _, s := range *p.seqList { fmt.Println(s) }
	fmt.Println("## 素数区间表")
	fmt.Println("区间|个数|最大值")
	fmt.Println("---|---|---")
	for _, s := range *p.interList { fmt.Println(s)	}
}

func dfString(l uint64) string {
	s := fmt.Sprintf("%d", l)
	if l%(10000*10000*10000) == 0 {
		s = s[:len(s)-12] + "万亿"
	} else if l%(10000*10000) == 0 {
		s = s[:len(s)-8] + "亿"
	} else if l%10000 == 0 {
		s = s[:len(s)-4] + "万"
	}
	return s
}