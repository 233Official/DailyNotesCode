package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
)

func uploadFiles(filePaths []string, targetURL string) error {
	// 创建请求体数据
	requestData := map[string][]string{
		"list": filePaths,
	}
	requestBody, err := json.Marshal(requestData)
	if err != nil {
		return fmt.Errorf("could not marshal JSON: %w", err)
	}

	// 创建HTTP请求
	req, err := http.NewRequest("POST", targetURL, bytes.NewBuffer(requestBody))
	if err != nil {
		return fmt.Errorf("could not create request: %w", err)
	}

	// 设置Content-Type头
	req.Header.Set("Content-Type", "application/json")

	// 发送HTTP请求
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("could not send request: %w", err)
	}
	defer resp.Body.Close()

	// 检查HTTP响应
	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("bad status: %s", resp.Status)
	}

	// 读取响应体
	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		return fmt.Errorf("could not read response body: %w", err)
	}

	fmt.Println("Upload successful. Response:", string(respBody))
	return nil
}

func main() {
	filePaths := []string{
		"AppData\\Local\\Temp\\Typora\\typora-icon2.png",
		"AppData\\Local\\Temp\\Typora\\typora-icon2.png",
	}
	targetURL := "http://127.0.0.1:36677/upload"

	err := uploadFiles(filePaths, targetURL)
	if err != nil {
		fmt.Println("Error uploading files:", err)
	}
}
