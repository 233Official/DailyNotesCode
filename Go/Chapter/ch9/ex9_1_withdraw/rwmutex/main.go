package main

import (
	"fmt"
	"sync"
)

var (
	balance int
	rwMu    sync.RWMutex
)

// Deposit function for adding to balance
func Deposit(amount int, wg *sync.WaitGroup) {
	defer wg.Done()
	rwMu.Lock() // Acquire write lock
	balance += amount
	rwMu.Unlock() // Release write lock
	fmt.Println("Deposit", amount, "Balance:", balance)
}

// Withdraw function for withdrawing from balance
func Withdraw(amount int, wg *sync.WaitGroup) {
	defer wg.Done()
	rwMu.Lock()

	if balance >= amount {
		balance -= amount
		fmt.Println("Withdraw", amount, "Balance:", balance)
	} else {
		fmt.Println("Withdraw", amount, "failed", "Balance:", balance)
	}

	rwMu.Unlock()
}

func main() {
	var wg sync.WaitGroup
	wg.Add(3)
	go Deposit(100, &wg)
	go Withdraw(50, &wg)
	go Withdraw(100, &wg)

	wg.Wait()
}
