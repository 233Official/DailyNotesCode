package main

import (
	"context"
	"fmt"
	"time"

	nuclei "github.com/projectdiscovery/nuclei/v3/lib"
	"github.com/projectdiscovery/nuclei/v3/pkg/output"
)

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
	engine.LoadTargets([]string{"http://192.168.1.215:9221"}, false)
	err = engine.ExecuteWithCallback(func(event *output.ResultEvent) {
		vul_name := event.Info.Name
		vul_description := event.Info.Description
		detected_url := event.URL + event.Path
		fmt.Printf("在 %s 发现 %s 漏洞，漏洞描述：%s\n", detected_url, vul_name, vul_description)
	})
	if err != nil {
		panic(err)
	}
	defer engine.Close()
}
