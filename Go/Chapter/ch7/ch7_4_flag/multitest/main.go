package main

import (
	"fmt"
)

// 定义 Celsius 类型
type Celsius float64

// Celsius 实现 String 方法
func (c Celsius) String() string {
	return fmt.Sprintf("%g°C", c)
}

// 定义 Fahrenheit 类型
type Fahrenheit float64

// Fahrenheit 实现 String 方法
// func (f Fahrenheit) String() string {
// 	return fmt.Sprintf("%g°F", f)
// }

// 定义一个包含 Celsius 和 Fahrenheit 的结构体
type Weather struct {
	Celsius
	Fahrenheit
}

// 结构体 Weather 本身实现 String 方法
// func (w Weather) String() string {
// 	return fmt.Sprintf("Celsius: %s, Fahrenheit: %s", w.Celsius.String(), w.Fahrenheit.String())
// }

func main() {
	w := Weather{
		Celsius:    30.0,
		Fahrenheit: 86.0,
	}

	// 调用 Weather 的 String 方法
	fmt.Println(w)

	// 调用嵌入类型的 String 方法
	fmt.Println(w.Celsius.String())
	// fmt.Println(w.Fahrenheit.String())
	// fmt.Println(w.String())
}
