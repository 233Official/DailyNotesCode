package main

import (
	"fmt"
	"io"
	"os"
	"reflect"
)

func main() {
	var w io.Writer = os.Stdout
	fmt.Println(reflect.TypeOf(w)) // "*os.File"

	fmt.Printf("%T\n", 3) // "int"

	v := reflect.ValueOf(3) // a reflect.Value
	//  fmt.Println 函数会自动调用 reflect.Value 的 String 方法，该方法对基本类型（如 int）进行特殊处理，返回其字符串表示形式。 "3"
	fmt.Println(v)
	fmt.Printf("%v\n", v) // %v 是 Go 语言中的通用占位符，它会调用 v 的 String 方法，输出结果同样为 "3"
	// v.String() 明确调用了 reflect.Value 类型的 String 方法。对于 reflect.Value 类型，这个方法返回的是值的描述信息，而不是被封装的值本身
	fmt.Println(v.String()) // NOTE: "<int Value>"

	t := v.Type()           // a reflect.Type
	fmt.Println(t.String()) // "int"

}
