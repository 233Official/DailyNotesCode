// Package bank provides a concurrency-safe bank with one account.
package main

import "fmt"

var deposits = make(chan int) // send amount to deposit
var balances = make(chan int) // receive balance

type Withdrawal struct {
	Amount int
	Result chan bool
}

var withdrawals = make(chan Withdrawal) // send amount to withdraw

func Deposit(amount int) {
	deposits <- amount
}

func Balance() int {
	return <-balances
}

func Withdraw(amount int) bool {
	result := make(chan bool)
	withdrawals <- Withdrawal{Amount: amount, Result: result}
	return <-result
}

func teller() {
	var balance int // balance is confined to teller goroutine
	for {
		select {
		case amount := <-deposits:
			balance += amount
		case balances <- balance:
		case withdrawal := <-withdrawals:
			if balance >= withdrawal.Amount {
				balance -= withdrawal.Amount
				withdrawal.Result <- true
			} else {
				withdrawal.Result <- false
			}
		}
	}
}

func init() {
	go teller() // start the monitor goroutine
}

func main() {

	Deposit(100)
	fmt.Println("Balance:", Balance()) // Output: Balance: 100

	if Withdraw(50) {
		fmt.Println("Withdraw 50: Success")
	} else {
		fmt.Println("Withdraw 50: Failed")
	}
	fmt.Println("Balance:", Balance()) // Output: Balance: 50

	if Withdraw(100) {
		fmt.Println("Withdraw 100: Success")
	} else {
		fmt.Println("Withdraw 100: Failed")
	}
	fmt.Println("Balance:", Balance()) // Output: Balance: 50
}
