package main

import (
	"flag"
	"fmt"
	"image"
	"image/gif"
	"image/jpeg"
	"image/png"
	"io"
	"os"
	"strings"
)

func main() {
	// 定义命令行标志参数
	var format string
	flag.StringVar(&format, "format", "jpeg", "output format: jpeg, png, gif")
	flag.Parse()

	// 检查格式是否有效
	if !validFormat(format) {
		fmt.Fprintf(os.Stderr, "Invalid format: %s\n", format)
		os.Exit(1)
	}

	// 调用转换函数
	if err := toImageFormat(os.Stdin, os.Stdout, format); err != nil {
		fmt.Fprintf(os.Stderr, "convert: %v\n", err)
		os.Exit(1)
	}
}

// 检查格式是否有效
func validFormat(format string) bool {
	switch strings.ToLower(format) {
	case "jpeg", "png", "gif":
		return true
	default:
		return false
	}
}

// 转换函数
func toImageFormat(in io.Reader, out io.Writer, format string) error {
	img, kind, err := image.Decode(in)
	if err != nil {
		return err
	}
	fmt.Fprintln(os.Stderr, "Input format =", kind)

	// 根据输出格式编码图像
	switch strings.ToLower(format) {
	case "jpeg":
		return jpeg.Encode(out, img, &jpeg.Options{Quality: 95})
	case "png":
		return png.Encode(out, img)
	case "gif":
		return gif.Encode(out, img, nil)
	default:
		return fmt.Errorf("unsupported output format: %s", format)
	}
}
