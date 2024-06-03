package main

import (
	"fmt"
)

// Person 人的信息结构体
type Person struct {
	name string // 可导出的字段
	age  int    // 不可导出的字段
}

// Employee 员工信息结构体，嵌入Person结构体
type Employee struct {
	Person
	position string // 可导出的字段
}

// NewEmployee 创建员工实例的工厂函数
func NewEmployee(name string, age int, position string) *Employee {
	return &Employee{
		Person:   Person{name: name, age: age},
		position: position,
	}
}

// GetAge 获取员工的年龄
func (e *Employee) GetAge() int {
	return e.age
}

// SetAge 设置员工的年龄
func (e *Employee) SetAge(age int) {
	e.age = age
}

func main() {
	// 创建一个员工实例
	employee := NewEmployee("summer", 23, "Software Engineer")

	// 获取员工的年龄并打印
	fmt.Println("Employee's age:", employee.GetAge())

	// 设置员工的年龄并再次获取并打印
	employee.SetAge(24)
	fmt.Println("Employee's age after setting:", employee.GetAge())
}
