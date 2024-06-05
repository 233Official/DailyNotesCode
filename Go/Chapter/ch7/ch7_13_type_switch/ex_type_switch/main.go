package main

import (
	"fmt"
	"math"
)

// 定义 Circle 类型
type Circle struct {
	Radius float64
}

// 定义 Rectangle 类型
type Rectangle struct {
	Width, Height float64
}

// 定义 Triangle 类型
type Triangle struct {
	Base, Height float64
}

// 为 Circle 实现 Area 方法
func (c Circle) Area() float64 {
	return math.Pi * c.Radius * c.Radius
}

// 为 Rectangle 实现 Area 方法
func (r Rectangle) Area() float64 {
	return r.Width * r.Height
}

// 为 Triangle 实现 Area 方法
func (t Triangle) Area() float64 {
	return 0.5 * t.Base * t.Height
}

// PrintArea 函数，使用类型分支判断图形类型并打印面积
func PrintArea(shape interface{}) {
	switch s := shape.(type) {
	case Circle:
		fmt.Printf("Circle Area: %.2f\n", s.Area())
	case Rectangle:
		fmt.Printf("Rectangle Area: %.2f\n", s.Area())
	case Triangle:
		fmt.Printf("Triangle Area: %.2f\n", s.Area())
	default:
		fmt.Println("Unknown shape")
	}
}

func main() {
	// 创建不同类型的图形实例
	c := Circle{Radius: 5}
	r := Rectangle{Width: 4, Height: 6}
	t := Triangle{Base: 3, Height: 4}

	// 调用 PrintArea 函数打印图形的面积
	PrintArea(c)
	PrintArea(r)
	PrintArea(t)
}
