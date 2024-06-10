package main

import (
	"bufio"
	"fmt"
	"strings"
)

type WordCounter int
type LineCounter int

// 写入时统计单词数
func (c *WordCounter) Write(p []byte) (int, error) {
	scanner := bufio.NewScanner(strings.NewReader(string(p)))
	scanner.Split(bufio.ScanWords)
	count := 0
	for scanner.Scan() {
		count++
	}
	*c += WordCounter(count)
	return len(p), nil
}

// 写入时统计行数
func (c *LineCounter) Write(p []byte) (int, error) {
	scanner := bufio.NewScanner(strings.NewReader(string(p)))
	count := 0
	for scanner.Scan() {
		count++
	}
	*c += LineCounter(count)
	return len(p), nil
}

func main() {
	// 测试 WordCounter 统计单词数
	var wc WordCounter
	wc.Write([]byte("hello world"))
	fmt.Println(wc) // 2个单词

	wc = 0 // 重置计数器
	var sentence = "hello, Dolly. How are you?"
	fmt.Fprintf(&wc, "hello, %s", sentence)
	fmt.Println(wc) // 6个单词

	// 测试 LineCounter
	var lc LineCounter
	lc.Write([]byte("hello world\nhello Go\n"))
	fmt.Println(lc) // 2行

	lc = 0 // 重置计数器
	var text = "hello, Dolly.\nHow are you?\nI am fine."
	fmt.Fprintf(&lc, "hello, %s", text)
	fmt.Println(lc) // 3行

}
