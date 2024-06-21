package main

import (
	"fmt"
	"sync"
)

var once sync.Once
var config string

func loadConfig() {
	fmt.Println("Loading config...")
	config = "Config data"
}

func main() {
	var wg sync.WaitGroup

	for i := 0; i < 5; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			once.Do(loadConfig)
			fmt.Println("Config:", config)
		}()
	}

	wg.Wait()
}
