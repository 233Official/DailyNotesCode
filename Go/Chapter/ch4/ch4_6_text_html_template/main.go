package main

import (
	"fmt"
	"html/template"
	"os"
	"time"
)

type Issue struct {
	Number int
	User   struct {
		Login string
	}
	Title     string
	CreatedAt time.Time
}

type IssueList struct {
	TotalCount int
	Items      []Issue
}

// daysAgo calculates the number of days since t.
func daysAgo(t time.Time) int {
	return int(time.Since(t).Hours() / 24)
}

func main() {
	const templ = `{{.TotalCount}} issues:
{{range .Items}}----------------------------------------
Number: {{.Number}}
User:   {{.User.Login}}
Title:  {{.Title | printf "%.64s"}}
Age:    {{.CreatedAt | daysAgo}} days
{{end}}`

	// Sample data
	data := IssueList{
		TotalCount: 2,
		Items: []Issue{
			{
				Number:    1,
				User:      struct{ Login string }{Login: "user1"},
				Title:     "Issue number one with a very long title that should be truncated",
				CreatedAt: time.Now().AddDate(0, 0, -10), // 10 days ago
			},
			{
				Number:    2,
				User:      struct{ Login string }{Login: "user2"},
				Title:     "Issue number two",
				CreatedAt: time.Now().AddDate(0, 0, -5), // 5 days ago
			},
		},
	}

	// Create a new template and register the custom function
	tmpl := template.Must(template.New("issueList").Funcs(template.FuncMap{
		"daysAgo": daysAgo,
	}).Parse(templ))

	// Execute the template
	if err := tmpl.Execute(os.Stdout, data); err != nil {
		fmt.Println("Error executing template:", err)
	}
}
