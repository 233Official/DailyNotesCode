package main

import (
	"fmt"
	"sync"
)

var (
	counter int
	rwMu    sync.RWMutex
)

func readCounter(wg *sync.WaitGroup) {
	defer wg.Done()
	rwMu.RLock()
	fmt.Println("Counter value:", counter)
	rwMu.RUnlock()
}

func writeCounter(wg *sync.WaitGroup) {
	defer wg.Done()
	rwMu.Lock()
	counter++
	rwMu.Unlock()
}

func main() {
	var wg sync.WaitGroup
	for i := 0; i < 5; i++ {
		wg.Add(1)
		go readCounter(&wg)
	}
	for i := 0; i < 5; i++ {
		wg.Add(1)
		go writeCounter(&wg)
	}
	wg.Wait()
	fmt.Println("程序末尾-Counter value:", counter)
}
