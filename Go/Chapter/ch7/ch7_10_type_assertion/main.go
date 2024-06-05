package main

import "fmt"

// 单值形式-成功断言
func single_value_assert_success() {
	var i interface{} = "hello"
	s := i.(string) // s现在是string类型，值为"hello"
	fmt.Println(s)
}

// 单值形式-失败断言
func single_value_assert_fail() {
	var i interface{} = 1
	s := i.(string) // s现在是int类型，值为0
	fmt.Println(s)
}

// 双值形式-成功断言
func double_value_assert_success() {
	var i interface{} = "hello"
	s, ok := i.(string) // s现在是string类型，值为"hello"
	if ok {
		fmt.Printf("类型断言成功，值为%v, ok 值为%v\n", s, ok)
	} else {
		fmt.Printf("类型断言失败，值为%v, ok 值为%v\n", s, ok)
	}
}

// 双值形式-失败断言
func double_value_assert_fail() {
	var i interface{} = 1
	s, ok := i.(string) // s现在是int类型，值为0
	if ok {
		fmt.Printf("类型断言成功，值为%v, ok 值为%v\n", s, ok)
	} else {
		fmt.Printf("类型断言失败，值为%v, ok 值为%v\n", s, ok)
		// 判断 s 是否为 nil
		if s == "" {
			fmt.Println("s是空字符串")
		} else {
			fmt.Println("s不是空字符串")
		}
	}
}

func main() {
	// single_value_assert_success()
	// single_value_assert_fail()
	double_value_assert_success()
	double_value_assert_fail()
}
