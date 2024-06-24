package main

import (
	"bufio"
	"io"
	"log"
	"net"
	"os"
	"path/filepath"
	"strings"
)

func main() {
	listener, err := net.Listen("tcp", "localhost:2121")
	if err != nil {
		log.Fatal(err)
	}
	log.Println("FTP server listening on port 2121")

	for {
		conn, err := listener.Accept()
		if err != nil {
			log.Print(err) // e.g., connection aborted
			continue
		}
		go handleConn(conn)
	}
}

func handleConn(conn net.Conn) {
	defer conn.Close()
	cwd, err := os.Getwd()
	if err != nil {
		log.Fatal(err)
	}

	scanner := bufio.NewScanner(conn)
	for scanner.Scan() {
		line := scanner.Text()
		log.Println("Received command:", line)
		args := strings.Fields(line)
		if len(args) < 1 {
			continue
		}

		cmd := args[0]
		switch cmd {
		case "cd":
			if len(args) < 2 {
				io.WriteString(conn, "Usage: cd <dir>\n")
				continue
			}
			newDir := args[1]
			if err := os.Chdir(newDir); err != nil {
				io.WriteString(conn, "Failed to change directory\n")
				continue
			}
			cwd, _ = os.Getwd()
			io.WriteString(conn, "Changed directory to "+cwd+"\n")
		case "ls":
			files, err := os.ReadDir(cwd)
			if err != nil {
				io.WriteString(conn, "Failed to list directory\n")
				continue
			}
			for _, file := range files {
				io.WriteString(conn, file.Name()+"\n")
			}
		case "get":
			if len(args) < 2 {
				io.WriteString(conn, "Usage: get <file>\n")
				continue
			}
			filePath := filepath.Join(cwd, args[1])
			file, err := os.Open(filePath)
			if err != nil {
				io.WriteString(conn, "Failed to open file\n")
				continue
			}
			defer file.Close()
			io.Copy(conn, file)
		case "send":
			if len(args) < 2 {
				io.WriteString(conn, "Usage: send <file>\n")
				continue
			}
			filePath := filepath.Join(cwd, args[1])
			file, err := os.Create(filePath)
			if err != nil {
				io.WriteString(conn, "Failed to create file\n")
				continue
			}
			defer file.Close()
			io.Copy(file, conn)
		case "close":
			io.WriteString(conn, "Closing connection\n")
			return
		default:
			io.WriteString(conn, "Unknown command\n")
		}
	}

	if err := scanner.Err(); err != nil {
		log.Println("Error reading from connection:", err)
	}
}
