// 7.11. 基于类型断言区别错误类型
package main

import (
	"fmt"
)

// 自定义错误类型
type MyError struct {
	Msg string
}

func (e *MyError) Error() string {
	return e.Msg
}

// 另一种自定义错误类型
type AnotherError struct {
	Code int
	Msg  string
}

func (e *AnotherError) Error() string {
	return fmt.Sprintf("Code: %d, Msg: %s", e.Code, e.Msg)
}

func main() {
	var err error

	// 赋值一个MyError类型的错误(在变量 err 中存储一个 MyError 类型的指针，该指针指向一个包含消息 "this is a MyError" 的 MyError 实例)
	err = &MyError{Msg: "this is a MyError"}

	// 基于类型断言处理错误
	if myErr, ok := err.(*MyError); ok {
		fmt.Println("这是一个MyError:", myErr.Msg)
	} else if anotherErr, ok := err.(*AnotherError); ok {
		fmt.Println("这是另一个错误类型:", anotherErr.Msg)
	} else {
		fmt.Println("这是一个普通错误:", err)
	}

	// 赋值一个AnotherError类型的错误
	err = &AnotherError{Code: 404, Msg: "not found"}

	// 基于类型断言处理错误
	if myErr, ok := err.(*MyError); ok {
		fmt.Println("这是一个MyError:", myErr.Msg)
	} else if anotherErr, ok := err.(*AnotherError); ok {
		fmt.Println("这是另一个错误类型:", anotherErr.Code, anotherErr.Msg)
	} else {
		fmt.Println("这是一个普通错误:", err)
	}
}
