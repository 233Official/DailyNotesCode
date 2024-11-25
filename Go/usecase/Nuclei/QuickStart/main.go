package main

import (
	"context"
	"time"

	nuclei "github.com/projectdiscovery/nuclei/v3/lib"
)

func main() {
	// create nuclei engine with options
	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()
	engine, err := nuclei.NewNucleiEngineCtx(
		ctx,
		nuclei.WithTemplateFilters(nuclei.TemplateFilters{Severity: "critical"}), // run critical severity templates only
	)
	if err != nil {
		panic(err)
	}
	// load targets and optionally probe non http/https targets
	engine.LoadTargets([]string{"scanme.sh"}, false)
	err = engine.ExecuteWithCallback(nil)
	if err != nil {
		panic(err)
	}
	defer engine.Close()
}
