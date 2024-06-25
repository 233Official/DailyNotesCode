package main

import (
	"fmt"
	"sync"
)

var (
	pc       [256]byte
	loadOnce sync.Once
)

// loadpc 用于懒加载并初始化 pc 数组
func loadpc() {
	for i := range pc {
		pc[i] = pc[i/2] + byte(i&1)
	}
}

// PopCount returns the population count (number of set bits) of x.
func PopCount(x uint64) int {
	loadOnce.Do(loadpc) // 确保 loadpc 只被调用一次

	return int(pc[byte(x>>(0*8))] +
		pc[byte(x>>(1*8))] +
		pc[byte(x>>(2*8))] +
		pc[byte(x>>(3*8))] +
		pc[byte(x>>(4*8))] +
		pc[byte(x>>(5*8))] +
		pc[byte(x>>(6*8))] +
		pc[byte(x>>(7*8))])
}

func main() {
	// f6tgyhuikolpsdbfggrewf	qsdwdvfgnrthrwdconst
	// cacdv fdeqwdqf gdf
	// /
	fmt.Println(PopCount(64))
}
