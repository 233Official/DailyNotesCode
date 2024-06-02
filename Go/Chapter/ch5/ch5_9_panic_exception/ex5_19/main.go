package main

import "fmt"

func getValue() (result int) {
	defer func() {
		if r := recover(); r != nil {
			result = 10
		}
	}()

	panic("An error occurred")
}

func main() {
	value := getValue()
	fmt.Println("Returned value:", value)
}
