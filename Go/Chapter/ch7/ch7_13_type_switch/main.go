package main

import (
	"fmt"
)

func main() {
	// 定义一个接口切片，包含不同的类型
	var data = []interface{}{42, "hello", true, 3.14}

	for _, v := range data {
		// 使用类型分支处理不同的类型
		switch value := v.(type) {
		case int:
			fmt.Printf("整数: %d\n", value)
		case string:
			fmt.Printf("字符串: %s\n", value)
		case bool:
			fmt.Printf("布尔值: %t\n", value)
		case float64:
			fmt.Printf("浮点数: %f\n", value)
		default:
			fmt.Println("未知类型")
		}
	}
}
