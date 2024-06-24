package main

import (
	"fmt"
	"log"
	"net"
	"os"
	"strings"
	"time"
)

func main() {
	if len(os.Args) < 2 {
		fmt.Println("Usage: clockwall Name=host:port ...")
		os.Exit(1)
	}

	servers := os.Args[1:]
	conns := make(map[string]net.Conn)
	for _, server := range servers {
		parts := strings.Split(server, "=")
		if len(parts) != 2 {
			log.Fatalf("Invalid argument: %s", server)
		}
		name, address := parts[0], parts[1]
		conn, err := net.Dial("tcp", address)
		if err != nil {
			log.Fatalf("Failed to connect to %s: %v", name, err)
		}
		conns[name] = conn
	}

	defer func() {
		for _, conn := range conns {
			conn.Close()
		}
	}()

	for {
		printTimes(conns)
		time.Sleep(1 * time.Second)
	}
}

func printTimes(conns map[string]net.Conn) {
	times := make(map[string]string)
	for name, conn := range conns {
		buf := make([]byte, 64)
		n, err := conn.Read(buf)
		if err != nil {
			log.Fatalf("Failed to read from %s: %v", name, err)
		}
		times[name] = strings.TrimSpace(string(buf[:n]))
	}

	fmt.Print("\033[H\033[2J") // Clear screen
	for name, t := range times {
		fmt.Printf("%s: %s\n", name, t)
	}
}
