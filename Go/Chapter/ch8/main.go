package main

import (
	"fmt"
	"time"
)

func sayHello() {
	fmt.Println("Hello, World!")
}

// Goroutine 示例
func goroutineExample() {
	go sayHello()
	fmt.Println("main function")
}

// 无缓冲区的 Channel 示例
func channelWithoutBufferExample() {
	// 创建一个 string 类型的 Channel
	messages := make(chan string)
	// 启动一个 Goroutine，向 Channel 发送数据
	go func() {
		messages <- "ping"
		fmt.Println("test")
	}()
	time.Sleep(10 * time.Second)
	msg := <-messages
	fmt.Println(msg)
}

// 有缓冲区的 Channel 示例
func channelWithBufferExample() {
	// 创建一个有缓冲的字符串类型的 Channel，缓冲区大小为2
	messages := make(chan string, 2)

	// 启动一个 Goroutine，向 Channel 发送数据
	go func() {
		messages <- "Hello, Goroutines!"
		fmt.Println("Message sent from Goroutine")
	}()

	// 从 Channel 接收数据并打印
	msg := <-messages
	fmt.Println("Received message:", msg)
}

func main() {
	// goroutineExample()
	channelWithoutBufferExample()
	// channelWithBufferExample()
}
