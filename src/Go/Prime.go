package main

import (
	"fmt"
	"math"
)

var isDebug  bool = false

type Prime struct{
	_maxInd uint64
	_maxKeep int
	seqList *[]string
	interList *[]string
	prevNo uint64
	_prime *[]uint64
}

func newPrime(limit uint64) *Prime {
	var p Prime
	p._maxKeep = int(math.Sqrt(float64(limit)) / math.Log(math.Sqrt(float64(limit)))* 1.3)
	p._prime = new([]uint64)
	//*(p._prime)= make([]uint64,10)
	p.interList = &[]string{}
	p.seqList = &[]string{}
	return &p
}

func (p Prime) get(index int) uint64{
	return (*p._prime)[index]
}

func (p *Prime) add(n uint64) {
	//_p := *(p._prime)
	//_p = append(_p, n)
	//*(p._prime) = _p
	*(p._prime) = append(*(p._prime), n)
	(*p)._maxInd ++
}

func (p *Prime) size() int{
	return len(*p._prime)
}

func (p *Prime) generateResults (inter uint64, endNo uint64){
	(*p).outputSequence(p.prevNo,endNo)
	(*p).outputInterval(inter)
	(*p).prevNo = endNo
	(*p).freeUp()
}

func (p Prime)  outputInterval(inter uint64){
	var s string
	if inter % uint64(math.Pow10 (len(fmt.Sprintf("%d",inter))- 1)) == 0 {
		s = dfString(inter) + "|" + fmt.Sprintf("%d",p._maxInd)  + "|" + fmt.Sprintf("%d",(*p._prime)[len(*p._prime) -1 ])
		*p.interList = append(*p.interList, s)
		if isDebug {fmt.Println("[In:]" , s)}
	}
}

func (p Prime) outputSequence(beginNo uint64,endNo uint64) {
	var s string
	for i := len(fmt.Sprintf("%d",beginNo))-1; i <= len(fmt.Sprintf("%d",endNo))-1; i++{
		for j := 1; j < 10 ; j++ {
			seq := uint64(j)*uint64(math.Pow10(i))
			if seq < beginNo {continue}
			if seq >= endNo {break}

			l := (*p._prime)[len(*p._prime) -1 - int(endNo - seq)]
			s = dfString(seq) + "|" + fmt.Sprintf("%d",l)
			*p.seqList = append(*p.seqList, s)
			if isDebug {fmt.Println("==>[No:]" , s)}
		}
	}
}

func (p *Prime) freeUp () {
	if len(*p._prime) > (*p)._maxKeep {
		*p._prime = (*p._prime)[:p._maxKeep]
	}
}

func (p Prime) printTable (){
	fmt.Println("## 素数区间表")
	fmt.Println("区间|个数|最大值")
	fmt.Println("---|---|---")
	for _,s := range *p.interList { fmt.Println(s) }
	fmt.Println("## 素数序列表")
	fmt.Println("序号|数值")
	fmt.Println("---|---")
	for _,s := range *p.seqList { fmt.Println(s) }
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
