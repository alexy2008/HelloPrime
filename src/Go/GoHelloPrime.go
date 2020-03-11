package main

import (
	"fmt"
	"math"
	"os"
	"strconv"
	"time"
)

const cache uint64 = 80000
var thread int
var page,repeat uint64
var isDebug bool
var prime Prime

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

func main() {
	fmt.Println("Hello Mr.Prime! I'm Go :-)")
	iPage, _ := strconv.Atoi(os.Args[1])
	page = uint64(iPage)
	irepeat, _ := strconv.Atoi(os.Args[2])
	repeat = uint64(irepeat)
	isDebug, _ = strconv.ParseBool(os.Args[3])
	thread, _ = strconv.Atoi(os.Args[4])
	var limit  = page * repeat
	prime = *newPrime(page, repeat)
	var top uint64 = 0
	var startTime time.Time

	fmt.Println("使用分区埃拉托色尼筛选法计算", dfString(limit), "以内素数：")
	startTime = time.Now()
	top += primeByEuler(page, &prime)
	prime.generateResults(page, top)

	if thread == 1 {
		fmt.Println("启动单线程模式")
		for i := uint64(1); i < repeat; i++ {
			pos := page * i
			top += primeByEratosthenesInterval(pos, page, &prime)
			prime.generateResults(pos+page, top)
		}
	} else {
		top = runMultiple(top)
	}

	totalTime := time.Now().Sub(startTime).Milliseconds()
	prime.printTable()
	fmt.Println("Go finished within", fmt.Sprintf("%g", float64(limit)), "; time cost:", totalTime, "ms")
}

func runMultiple(top uint64) uint64 {
	fmt.Println("启动多线程模式，线程数量：", thread)
	var chs  = make([]chan *[cache]uint64, thread)
	for i := 0; i < thread; i++ {
		chs[i] = make(chan *[cache]uint64)
		go runTask(i, chs[i])
	}
	okAll := true
	for m := uint64(0); okAll; m += uint64(thread) {
		okAll = false
		for i := 0; i < thread; i++ {
			//n := uint64(1 + i )
			var r, ok = <-chs[i]
			if !ok {
				break
			}
			okAll = true
			for k := range r {
				if r[k] == 0 { break }
				prime.add(r[k])
				//println("线程",i,"第",m,"次添加top: ",top,",",k," = ",r[k])
				top++
			}
			//println(i," : ",page*(uint64(i) + m + 2))
			prime.generateResults(page*(uint64(i)+m+2), top) //todo
		}
	}
	return top
}

func runTask(tid int, result chan *[cache]uint64)  {

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

type Prime struct {
	_maxInd   uint64
	_maxKeep  int
	seqList   *[]string
	interList *[]string
	prevNo    uint64
	_prime    *[]uint64
	_offSet   uint64
}

func newPrime(page uint64, repeat uint64) *Prime {
	var p Prime
	p._maxKeep = int(math.Sqrt(float64(page*repeat)) / math.Log(math.Sqrt(float64(page*repeat))) * 1.3)
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

func (p *Prime) generateResults(inter uint64, endNo uint64) {
	(*p).outputSequence(p.prevNo, endNo)
	(*p).outputInterval(inter)
	(*p).prevNo = endNo
	(*p).freeUp()
}

func (p Prime) outputInterval(inter uint64) {
	var s string
	if inter%uint64(math.Pow10(len(fmt.Sprintf("%d", inter))-1)) == 0 {
		s = dfString(inter) + "|" + fmt.Sprintf("%d", p._maxInd) + "|" + fmt.Sprintf("%d", (*p._prime)[p._maxInd-p._offSet-1])
		*p.interList = append(*p.interList, s)
		if isDebug { fmt.Println("[In:]", s) }
	}
}

func (p Prime) outputSequence(beginNo uint64, endNo uint64) {
	var s string
	for i := len(fmt.Sprintf("%d", beginNo)) - 1; i <= len(fmt.Sprintf("%d", endNo))-1; i++ {
		for j := 1; j < 10; j++ {
			seq := uint64(j) * uint64(math.Pow10(i))
			if seq < beginNo { continue	}
			if seq >= endNo { break	}
			v := (*p._prime)[p._maxInd-p._offSet-1-(endNo-seq)]
			s = dfString(seq) + "|" + fmt.Sprintf("%d", v)
			*p.seqList = append(*p.seqList, s)
			if isDebug { fmt.Println("==>[No:]", s)	}
		}
	}
}

func (p *Prime) freeUp() {
	if len(*p._prime) > (*p)._maxKeep {
		(*p)._offSet = (*p)._maxInd - uint64((*p)._maxKeep)
	}
}

func (p Prime) printTable() {
	fmt.Println("## 素数区间表")
	fmt.Println("区间|个数|最大值")
	fmt.Println("---|---|---")
	for _, s := range *p.interList { fmt.Println(s)	}
	fmt.Println("## 素数序列表")
	fmt.Println("序号|数值")
	fmt.Println("---|---")
	for _, s := range *p.seqList { fmt.Println(s) }
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