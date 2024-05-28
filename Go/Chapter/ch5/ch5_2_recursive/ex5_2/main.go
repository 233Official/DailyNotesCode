package main

import (
	"fmt"
	"os"

	"golang.org/x/net/html"
)

func main() {
	doc, err := html.Parse(os.Stdin)
	if err != nil {
		fmt.Fprintf(os.Stderr, "findelements: %v\n", err)
		os.Exit(1)
	}
	elementsCount := make(map[string]int)
	countElements(elementsCount, doc)
	for element, count := range elementsCount {
		fmt.Printf("%s: %d\n", element, count)
	}
}

// countElements 递归遍历 HTML 树，并记录每种元素的出现次数。
func countElements(elementsCount map[string]int, n *html.Node) {
	if n == nil {
		return
	}
	if n.Type == html.ElementNode {
		elementsCount[n.Data]++
	}
	countElements(elementsCount, n.FirstChild)  // 递归遍历第一个子节点
	countElements(elementsCount, n.NextSibling) // 递归遍历下一个兄弟节点
}

// go build
// ..\findlinks1\CH1-5-GetURL.exe https://golang.org | .\ex5_2.exe
