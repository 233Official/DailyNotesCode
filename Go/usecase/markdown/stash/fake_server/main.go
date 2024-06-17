package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
)

func uploadHandler(w http.ResponseWriter, r *http.Request) {
	// 获取请求头信息
	headers := r.Header

	// 读取请求体
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		http.Error(w, "Unable to read body", http.StatusInternalServerError)
		return
	}

	// 打印请求头和请求体到响应页面
	fmt.Fprintf(w, "<html><body>")

	fmt.Fprintf(w, "<h1>Request Headers</h1>")
	fmt.Printf("Request Headers: %v\n", headers)

	fmt.Fprintf(w, "<pre>%s</pre>", headers)
	fmt.Fprintf(w, "<h1>Request Body</h1>")
	fmt.Fprintf(w, "<pre>%s</pre>", body)
	fmt.Fprintf(w, "</body></html>")
	fmt.Printf("Request Body: %s\n", body)
}

func main() {
	http.HandleFunc("/upload", uploadHandler)
	fmt.Println("Starting server on :36677")
	err := http.ListenAndServe(":36677", nil)
	if err != nil {
		fmt.Println("Error starting server:", err)
	}
}
