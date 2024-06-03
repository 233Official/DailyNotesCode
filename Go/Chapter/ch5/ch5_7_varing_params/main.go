package main

import "fmt"

// sum 函数接受可变数量的 int 参数
func sum(nums ...int) int {
	total := 0
	for _, num := range nums {
		total += num
	}
	return total
}

// 可变参数基础示例
func sample1() {
	fmt.Println(sum(1, 2, 3))       // 输出 6
	fmt.Println(sum(1, 2, 3, 4, 5)) // 输出 15
}

// 可变参数切片示例
func sample2() {
	numbers := []int{1, 2, 3, 4, 5}
	result := sum(numbers...)
	fmt.Println(result) // 输出 15
}

func main() {
	// sample1()
	sample2()
}
