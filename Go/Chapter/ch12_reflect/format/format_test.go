package format

import (
	"fmt"
	"testing"
	"time"
)

func TestFormat(t *testing.T) {
	var x int64 = 1
	var d time.Duration = 1 * time.Nanosecond

	tests := []struct {
		value interface{}
	}{
		{x},
		{d},
		{42},
		{"test"},
		{true},
		{[]int{1, 2, 3}},
		{[]time.Duration{d}},
		{nil},
	}
	for _, test := range tests {
		fmt.Println(Any(test.value))
	}
}
