package display

import (
	"fmt"
)

func Display(name string, x interface{}) {
	fmt.Printf("Display %s (%T):\n", name, x)
	// display(name, reflect.ValueOf(x))
}
