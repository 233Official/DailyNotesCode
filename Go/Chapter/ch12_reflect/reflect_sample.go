package main

import (
	"fmt"
	"reflect"
)

func main() {
	// 示例1：获取类型和值
	var x float64 = 3.4
	fmt.Println("type:", reflect.TypeOf(x))
	fmt.Println("value:", reflect.ValueOf(x))

	// 示例2：获取Kind
	fmt.Println("kind:", reflect.TypeOf(x).Kind())

	// 示例3：通过反射修改值
	y := 7
	v := reflect.ValueOf(&y) // 传递指针
	v.Elem().SetInt(42)
	fmt.Println("new value:", y)

	// 示例4：操作结构体字段
	type T struct {
		A int
		B string
	}
	t := T{203, "hello"}
	s := reflect.ValueOf(&t).Elem()
	typeOfT := s.Type()

	for i := 0; i < s.NumField(); i++ {
		f := s.Field(i)
		fmt.Printf("%d: %s %s = %v\n", i, typeOfT.Field(i).Name, f.Type(), f.Interface())
	}

	s.Field(0).SetInt(999)
	s.Field(1).SetString("world")
	fmt.Println("new struct:", t)
}
