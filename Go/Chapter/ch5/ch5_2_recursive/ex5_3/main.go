package main

import (
	"fmt"
	"os"

	"golang.org/x/net/html"
)

func main() {
	doc, err := html.Parse(os.Stdin)
	if err != nil {
		fmt.Fprintf(os.Stderr, "findtext: %v\n", err)
		os.Exit(1)
	}
	printTextNodes(doc)
}

// printTextNodes 递归遍历 HTML 树，并输出所有文本节点的内容
func printTextNodes(n *html.Node) {
	if n == nil {
		return
	}
	if n.Type == html.TextNode {
		fmt.Println(n.Data)
	}
	if n.Type == html.ElementNode && (n.Data == "script" || n.Data == "style") {
		// 跳过 <script> 和 <style> 元素
		return
	}
	// 递归处理第一个子节点
	printTextNodes(n.FirstChild)
	// 递归处理下一个兄弟节点
	printTextNodes(n.NextSibling)
}

// go build
// ..\findlinks1\CH1-5-GetURL.exe https://golang.org | .\ex5_3.exe
