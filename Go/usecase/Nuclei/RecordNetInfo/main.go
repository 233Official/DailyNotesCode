package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"net/http"
	"reflect"
	"time"

	nuclei "github.com/projectdiscovery/nuclei/v3/lib"
	"github.com/projectdiscovery/nuclei/v3/pkg/output"
	retryablehttp "github.com/projectdiscovery/retryablehttp-go"
)

func customDialContext(ctx context.Context, network, addr string) (net.Conn, error) {
	conn, err := net.Dial(network, addr)
	if err != nil {
		return nil, err
	}
	// 获取并记录源和目标的 IP 端口
	localAddr := conn.LocalAddr().String()
	remoteAddr := conn.RemoteAddr().String()
	log.Printf("Source: %s, Destination: %s\n", localAddr, remoteAddr)
	return conn, nil
}

func main() {
	// create nuclei engine with options
	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	// 创建 Nuclei 引擎，指定模板目录和模板文件
	var templates []string = []string{"./pikachu_rce_eval.yaml"}
	engine, err := nuclei.NewNucleiEngineCtx(
		ctx,
		nuclei.WithTemplatesOrWorkflows(nuclei.TemplateSources{Templates: templates}),
	)
	if err != nil {
		panic(err)
	}
	// load targets and optionally probe non http/https targets
	engine.LoadTargets([]string{"scanme.sh"}, false)

	// 创建自定义的 Transport
	transport := &http.Transport{
		DialContext: customDialContext,
	}

	// 创建并配置 retryablehttp.Client
	retryClient := retryablehttp.NewClient(retryablehttp.Options{})
	retryClient.HTTPClient = &http.Client{
		Transport: transport,
	}
	// 可根据需要配置 retryClient，例如重试次数
	// retryClient.RetryMax = 5

	customHTTPClient := retryClient

	// 使用反射设置未导出的 httpClient 属性
	engineValue := reflect.ValueOf(engine).Elem()
	httpClientField := engineValue.FieldByName("httpClient")
	if httpClientField.IsValid() && httpClientField.CanSet() {
		httpClientField.Set(reflect.ValueOf(customHTTPClient))
	} else {
		fmt.Println("httpClient: ", httpClientField)
		fmt.Println("无法设置 httpClient 属性")
	}

	err = engine.ExecuteWithCallback(func(event *output.ResultEvent) {
		vulName := event.Info.Name
		vulDescription := event.Info.Description
		detectedURL := event.URL + event.Path
		fmt.Printf("在 %s 发现 %s 漏洞，漏洞描述：%s\n", detectedURL, vulName, vulDescription)
	})
	if err != nil {
		panic(err)
	}
	defer engine.Close()

	// err = engine.ExecuteCallbackWithCtx(ctx, func(event *output.ResultEvent) {
	// 	vulName := event.Info.Name
	// 	vulDescription := event.Info.Description
	// 	detectedURL := event.URL + event.Path
	// 	fmt.Printf("在 %s 发现 %s 漏洞，漏洞描述：%s\n", detectedURL, vulName, vulDescription)
	// })
	// if err != nil {
	// 	panic(err)
	// }
	// defer engine.Close()

}
