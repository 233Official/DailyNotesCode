package main

import (
	"fmt"
	"strings"
)

// expand 函数将字符串 s 中的 "foo" 替换为 f("foo") 的返回值
func expand(s string, f func(string) string) string {
	return strings.ReplaceAll(s, "foo", f("foo"))
}

func main() {
	// 示例 f 函数，将 "foo" 替换为 "bar"
	f := func(s string) string {
		return "bar"
	}

	// 测试 expand 函数
	s := "foo foo foo foo foo foo foo foo foo foo foo foo"
	fmt.Println("原始字符串:", s)
	result := expand(s, f)
	fmt.Println("变更字符串:", result)
}
