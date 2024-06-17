package main

import (
	"bufio"
	"bytes"
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
	"regexp"
	"strings"
)

const (
	// 默认的PicGo上传地址
	defaultPicGoURL = "http://127.0.0.1:36677/upload"
)

type PicGoUploadResponse struct {
	Success   bool     `json:"success"`
	FileLinks []string `json:"result"`
}

type ImageNotFoundInMDFileError struct {
	FileName string
}

func (e *ImageNotFoundInMDFileError) Error() string {
	return "在指定 Markdown 文件中未匹配到图链项目, Markdown 文件路径: " + e.FileName
}

// 通过 PicGo 上传一组文件并返回对应的一组文件网络链接以及是否有error
func uploadFiles(filePaths []string, targetURL string) ([]string, error) {
	// 创建请求体数据
	requestData := map[string][]string{
		"list": filePaths,
	}
	requestBody, err := json.Marshal(requestData)
	if err != nil {
		return nil, fmt.Errorf("解析 JSON 时出现错误 %w", err)
	}

	// 创建HTTP请求
	req, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(requestBody))
	if err != nil {
		return nil, fmt.Errorf("无法创建 POST 图片请求: %w", err)
	}

	// 设置Content-Type头
	req.Header.Set("Content-Type", "application/json")

	// 发送HTTP请求
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("请求发送失败: %w", err)
	}
	defer resp.Body.Close()

	// 检查HTTP响应
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("HTTP 响应码异常: HTTP %s", resp.Status)
	}

	// 读取响应体
	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("无法读取响应体: %w", err)
	}

	// fmt.Println("上传文件成功; 响应为:", string(respBody))

	// 从 respBody 中解析出图片链接列表并返回(响应体 JSON 中的 result属性对应的列表)
	var picGoUploadResponse PicGoUploadResponse
	if err := json.Unmarshal(respBody, &picGoUploadResponse); err != nil {
		return nil, fmt.Errorf("无法解析响应体: %w", err)
	}
	if len(picGoUploadResponse.FileLinks) > 0 {
		return picGoUploadResponse.FileLinks, nil
	} else {
		return nil, fmt.Errorf("上传出现了问题,似乎没有在响应体 %s 中找到图片链接", respBody)
	}
}

// 处理 Markdown 文件，上传图片并替换链接, 生成处理后的 Markdown 文件内容
func generateProcessedMarkdownContent(markdownFilePath string) (string, error) {
	input, err := os.Open(markdownFilePath)
	if err != nil {
		return "", fmt.Errorf("打开文件报错: %w", err)
	}
	defer input.Close()

	// 初始化缓冲区用于高效地构建字符串或二进制数据
	var output bytes.Buffer
	// 创建一个扫描器用于朱行读取文件内容
	scanner := bufio.NewScanner(input)
	// 匹配 Markdown 中的相对路径图片的正则表达式对象
	imgRegex := regexp.MustCompile(`!\[.*\]\((.*)\)`)

	for scanner.Scan() {
		line := scanner.Text()
		matches := imgRegex.FindAllStringSubmatch(line, -1)
		if matches != nil {
			for _, match := range matches {
				// 将图片相对路径转换为绝对路径
				relativePath := match[1]
				absolutePath := relativePath
				if !filepath.IsAbs(relativePath) {
					absolutePath = filepath.Join(filepath.Dir(markdownFilePath), relativePath)
				}

				// 上传图片并获取图片链接
				imgURLs, err := uploadFiles([]string{absolutePath}, defaultPicGoURL)
				if err != nil {
					return "", fmt.Errorf("上传图片时出现错误: %w,图片路径为: %s", err, relativePath)
				}

				// 替换 Markdown 中的相对路径为图片链接(最后的那个1表示只替换第一个匹配项)
				line = strings.Replace(line, match[1], imgURLs[0], 1)
			}
			output.WriteString(line + "\n")
		} else {
			output.WriteString(line + "\n")
		}
	}
	if err := scanner.Err(); err != nil {
		return "", fmt.Errorf("could not scan file: %w", err)
	}
	return output.String(), nil
}

// 处理 Markdown 文件, 将处理后的Markdown 文件内容写入到 _uploaded.md 中
func convertMarkdownFileWithRelativePicToMarkdownFileWithPicURL(markdownFilePath string) (string, error) {
	// 处理 Markdown 文件, 将处理后的 Markdown 文件内容写入到 _uploaded.md 中
	processedMarkdownContent, err := generateProcessedMarkdownContent(markdownFilePath)
	if err != nil {
		return "", fmt.Errorf("处理 Markdown 文件时出现错误: %w", err)
	}

	outputFilePath := strings.TrimSuffix(markdownFilePath, filepath.Ext(markdownFilePath)) + "_uploaded.md"
	outputFile, err := os.Create(outputFilePath)
	if err != nil {
		return "", fmt.Errorf("创建文件报错: %w", err)
	}

	defer outputFile.Close()

	if _, err := outputFile.WriteString(processedMarkdownContent); err != nil {
		return "", fmt.Errorf("写入文件报错: %w", err)
	}

	return outputFilePath, nil
}

// 测试上传图片文件
func uploadTest() {
	// 测试上传图片文件
	// 获取环境变量中的用户目录
	userDir := os.Getenv("USERPROFILE")

	// 使用 filepath.Join() 拼接路径
	filePaths := []string{
		filepath.Join(userDir, "AppData", "Local", "Temp", "Typora", "typora-icon2.png"),
		filepath.Join(userDir, "AppData", "Local", "Temp", "Typora", "typora-icon.png"),
		// 添加更多文件路径
	}
	fileLinks, err := uploadFiles(filePaths, defaultPicGoURL)
	if err != nil {
		fmt.Println("上传文件失败: %w", err)
	} else {
		fmt.Println("上传文件成功; 文件链接为:", fileLinks)
	}
}

var markdownFilePath = flag.String("p", "", "代处理Markdown文件路径")

func main() {
	// uploadTest()

	flag.Parse()
	if *markdownFilePath == "" {
		fmt.Println("请指定 Markdown 文件路径")
		return
	} else if !filepath.IsAbs(*markdownFilePath) {
		fmt.Println("请指定绝对路径")
		return
	} else if !strings.HasSuffix(*markdownFilePath, ".md") {
		fmt.Println("请指定 Markdown 文件")
		return
	} else if _, err := os.Stat(*markdownFilePath); os.IsNotExist(err) {
		fmt.Println("指定的 Markdown 文件不存在")
		return
	} else {
		convertMarkdownFileWithRelativePicToMarkdownFileWithPicURL(*markdownFilePath)
	}
}
