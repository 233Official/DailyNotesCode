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
	links, images, scripts, styles := visit(nil, nil, nil, nil, doc)
	fmt.Println("Links:")
	for _, link := range links {
		fmt.Println(link)
	}
	fmt.Println("Images:")
	for _, img := range images {
		fmt.Println(img)
	}
	fmt.Println("Scripts:")
	for _, script := range scripts {
		fmt.Println(script)
	}
	fmt.Println("Styles:")
	for _, style := range styles {
		fmt.Println(style)
	}
}

// visit 递归遍历 HTML 树，收集不同类型节点的信息。
func visit(links, images, scripts, styles []string, n *html.Node) ([]string, []string, []string, []string) {
	if n == nil {
		return links, images, scripts, styles
	}
	if n.Type == html.ElementNode {
		switch n.Data {
		case "a":
			for _, a := range n.Attr {
				if a.Key == "href" {
					links = append(links, a.Val)
				}
			}
		case "img":
			for _, a := range n.Attr {
				if a.Key == "src" {
					images = append(images, a.Val)
				}
			}
		case "script":
			for _, a := range n.Attr {
				if a.Key == "src" {
					scripts = append(scripts, a.Val)
				}
			}
		case "link":
			for _, a := range n.Attr {
				if a.Key == "rel" && a.Val == "stylesheet" {
					for _, a := range n.Attr {
						if a.Key == "href" {
							styles = append(styles, a.Val)
						}
					}
				}
			}
		}
	}
	links, images, scripts, styles = visit(links, images, scripts, styles, n.FirstChild)
	links, images, scripts, styles = visit(links, images, scripts, styles, n.NextSibling)
	return links, images, scripts, styles
}

// go build
// ..\findlinks1\CH1-5-GetURL.exe https://golang.org | .\ex5_4.exe
