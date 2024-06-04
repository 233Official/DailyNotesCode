package main

import (
	"fmt"
	"math"
)

type Point struct {
	X, Y float64
}

func (p Point) Distance(q Point) float64 {
	dx := p.X - q.X
	dy := p.Y - q.Y
	return math.Sqrt(dx*dx + dy*dy)
}

func (p *Point) ScaleBy(factor float64) {
	// p.X *= factor
	(*p).X *= factor
	p.Y *= factor
}

func main() {
	p := Point{1, 2}
	q := Point{4, 6}
	// 传值
	distance := p.Distance(q) // 调用时使用 Point 类型
	fmt.Println(distance)
	// 传引用
	pptr := &Point{1, 2}
	pptr.ScaleBy(2) // 调用时使用 *Point 类型

	// T调用形参*T会隐式转成&T调用
	p = Point{1, 2}
	p.ScaleBy(2) // 调用时使用 Point 类型，编译器隐式取地址 &p

	// &T 调用形参T会隐式转成 *&T 调用
	pptr = &Point{1, 2}
	q = Point{4, 6}
	distance = pptr.Distance(q) // 调用时使用 *Point 类型，编译器隐式解引用 *pptr
	fmt.Println(distance)
}
