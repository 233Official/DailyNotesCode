// charcount_test.go
package charcount

import (
	"strings"
	"testing"
	"unicode/utf8"
)

func TestCharCount(t *testing.T) {
	input := "Hello, 世界"
	expectedCounts := map[rune]int{
		'H': 1,
		'e': 1,
		'l': 2,
		'o': 1,
		',': 1,
		' ': 1,
		'世': 1,
		'界': 1,
	}
	expectedUtfLen := [utf8.UTFMax + 1]int{0, 7, 0, 2, 0}

	r := strings.NewReader(input)
	counts, utflen, invalid, err := CharCount(r)
	if err != nil {
		t.Fatalf("CharCount failed: %v", err)
	}

	for r, count := range expectedCounts {
		if counts[r] != count {
			t.Errorf("For rune %q, expected %d but got %d", r, count, counts[r])
		}
	}

	// 判断 x 字节字符数量
	for i, count := range expectedUtfLen {
		if utflen[i] != count {
			t.Errorf("For UTF length %d, expected %d but got %d", i, count, utflen[i])
		}
	}

	if invalid != 0 {
		t.Errorf("Expected 0 invalid characters but got %d", invalid)
	}
}
