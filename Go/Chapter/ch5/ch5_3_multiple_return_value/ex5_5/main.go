package main

import (
	"fmt"
	"net/http"
	"os"
	"strings"

	"golang.org/x/net/html"
)

func main() {
	// 示例：统计指定 URL 的单词和图片数量
	url := "https://golang.org"
	words, images, err := CountWordsAndImages(url)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error: %v\n", err)
		return
	}
	fmt.Printf("Words: %d, Images: %d\n", words, images)
}

// CountWordsAndImages 统计给定 URL 网页中的单词和图片数量
func CountWordsAndImages(url string) (words, images int, err error) {
	resp, err := http.Get(url)
	if err != nil {
		return
	}
	doc, err := html.Parse(resp.Body)
	resp.Body.Close()
	if err != nil {
		err = fmt.Errorf("parsing HTML: %s", err)
		return
	}
	words, images = countWordsAndImages(doc)
	return
}

// countWordsAndImages 遍历 HTML 文档树，统计单词和图片数量
func countWordsAndImages(n *html.Node) (words, images int) {
	if n == nil {
		return
	}
	if n.Type == html.TextNode {
		words += len(splitWords(n.Data))
	}
	if n.Type == html.ElementNode && n.Data == "img" {
		images++
	}
	wordsChild, imagesChild := countWordsAndImages(n.FirstChild)
	wordsSibling, imagesSibling := countWordsAndImages(n.NextSibling)
	words += wordsChild + wordsSibling
	images += imagesChild + imagesSibling
	return
}

// splitWords 将文本节点内容按空格分割成单词
func splitWords(text string) []string {
	// 可以使用 strings.Fields 函数按空格分割文本
	return strings.Fields(text)
}
