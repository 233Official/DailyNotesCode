package split

import (
	"strings"
	"testing"
)

func TestSplit(t *testing.T) {
	var tests = []struct {
		sep  string
		args string
		want int
	}{
		{"", "", 0},
		{"\t", "one\ttwo\tthree\n", 3},
		{",", "a,b,c\n", 3},
		{":", "1:2:3", 3},
		{",", "a b c\n", 1},
		{":", "a:b:c", 3},
	}

	for _, test := range tests {
		words := strings.Split(test.args, test.sep)
		if got := len(words); got != test.want {
			t.Errorf("Split(%q,%q) returned %d words, want %d", test.args, test.sep, got, test.want)
		}
	}
}
