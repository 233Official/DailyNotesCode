package main

import (
	"bytes"
	"fmt"
)

// An IntSet is a set of small non-negative integers.
// Its zero value represents the empty set.
type IntSet struct {
	words []uint64
}

// Has reports whether the set contains the non-negative value x.
func (s *IntSet) Has(x int) bool {
	word, bit := x/64, uint(x%64)
	return word < len(s.words) && s.words[word]&(1<<bit) != 0
}

// Add adds the non-negative value x to the set.
func (s *IntSet) Add(x int) {
	word, bit := x/64, uint(x%64)
	for word >= len(s.words) {
		s.words = append(s.words, 0)
	}
	s.words[word] |= 1 << bit
}

// UnionWith sets s to the union of s and t.
func (s *IntSet) UnionWith(t *IntSet) {
	for i, tword := range t.words {
		if i < len(s.words) {
			s.words[i] |= tword
		} else {
			s.words = append(s.words, tword)
		}
	}
}

// String returns the set as a string of the form "{1 2 3}".
func (s *IntSet) String() string {
	var buf bytes.Buffer
	buf.WriteByte('{')
	for i, word := range s.words {
		if word == 0 {
			continue
		}
		for j := 0; j < 64; j++ {
			if word&(1<<uint(j)) != 0 {
				if buf.Len() > len("{") {
					buf.WriteByte(' ')
				}
				fmt.Fprintf(&buf, "%d", 64*i+j)
			}
		}
	}
	buf.WriteByte('}')
	return buf.String()
}

// ex6_1
// Len returns the number of elements in the set.
func (s *IntSet) Len() int {
	count := 0
	for _, word := range s.words {
		count += popCount(word)
	}
	return count
}

// Remove removes x from the set.
func (s *IntSet) Remove(x int) {
	word, bit := x/64, uint(x%64)
	if word < len(s.words) {
		s.words[word] &^= 1 << bit
	}
}

// Clear removes all elements from the set.
func (s *IntSet) Clear() {
	s.words = nil
}

// Copy returns a copy of the set.
func (s *IntSet) Copy() *IntSet {
	t := &IntSet{}
	t.words = append(t.words, s.words...)
	return t
}

// Helper function to count the number of set bits (1s) in a uint64.
func popCount(x uint64) int {
	count := 0
	for x != 0 {
		x &= x - 1
		count++
	}
	return count
}

func ex6_1() {
	var s IntSet

	// 向集合 s 中添加一些元素
	s.Add(1)
	s.Add(144)
	s.Add(9)

	// 输出集合的长度
	fmt.Println("Set s length:", s.Len()) // 3

	// 从集合中移除元素
	s.Remove(9)
	fmt.Println("Set s contains 9 after removal:", s.Has(9)) // false
	fmt.Println("Set s length after removal:", s.Len())      // 2

	// 复制集合
	t := s.Copy()
	fmt.Println("Copy of set s contains 1:", t.Has(1))     // true
	fmt.Println("Copy of set s contains 144:", t.Has(144)) // true

	// 清空集合
	s.Clear()
	fmt.Println("Set s length after clearing:", s.Len())      // 0
	fmt.Println("Set s contains 1 after clearing:", s.Has(1)) // false
}

// ex6_2 AddAll
// AddAll adds a list of non-negative values to the set.
func (s *IntSet) AddAll(values ...int) {
	for _, v := range values {
		s.Add(v)
	}
}

func ex6_2() {
	var s IntSet

	// 使用 AddAll 方法添加一组元素
	s.AddAll(1, 2, 3, 144, 9)

	// 输出集合的长度
	fmt.Println("Set s length:", s.Len()) // 5

	// 检查集合是否包含某些元素
	fmt.Println("Set s contains 1:", s.Has(1))     // true
	fmt.Println("Set s contains 144:", s.Has(144)) // true
	fmt.Println("Set s contains 5:", s.Has(5))     // false
}

// ex6_3 交集 差集 并差集
// IntersectWith sets s to the intersection of s and t.
func (s *IntSet) IntersectWith(t *IntSet) {
	for i := range s.words {
		if i < len(t.words) {
			s.words[i] &= t.words[i]
		} else {
			s.words[i] = 0
		}
	}
}

// DifferenceWith sets s to the difference of s and t.
func (s *IntSet) DifferenceWith(t *IntSet) {
	for i := range s.words {
		if i < len(t.words) {
			s.words[i] &^= t.words[i]
		}
	}
}

// SymmetricDifference sets s to the symmetric difference of s and t.
func (s *IntSet) SymmetricDifference(t *IntSet) {
	for i, tword := range t.words {
		if i < len(s.words) {
			s.words[i] ^= tword
		} else {
			s.words = append(s.words, tword)
		}
	}
}

func ex6_3() {
	// ex6_3 交集 差集 并差集
	var s, t IntSet

	// 向集合 s 和 t 中添加一些元素
	s.AddAll(1, 144, 9)
	t.AddAll(9, 42)

	// 输出集合 s 和 t 的并集
	s.UnionWith(&t)
	fmt.Println("Union of s and t:", s) // [1 9 42 144]

	// 重置 s 和 t
	s.Clear()
	s.AddAll(1, 144, 9)
	t.Clear()
	t.AddAll(9, 42)

	// 输出集合 s 和 t 的交集
	s.IntersectWith(&t)
	fmt.Println("Intersection of s and t:", s) // [9]

	// 重置 s 和 t
	s.Clear()
	s.AddAll(1, 144, 9)
	t.Clear()
	t.AddAll(9, 42)

	// 输出集合 s 和 t 的差集
	s.DifferenceWith(&t)
	fmt.Println("Difference of s and t:", s) // [1 144]

	// 重置 s 和 t
	s.Clear()
	s.AddAll(1, 144, 9)
	t.Clear()
	t.AddAll(9, 42)

	// 输出集合 s 和 t 的并差集
	s.SymmetricDifference(&t)
	fmt.Println("Symmetric difference of s and t:", s) // [1 42 144]
}

// ex6_4 Elems()
// Elems returns all elements in the set as a slice of ints.
func (s *IntSet) Elems() []int {
	var elems []int
	for i, word := range s.words {
		for bit := 0; bit < 64; bit++ {
			if word&(1<<uint(bit)) != 0 {
				elems = append(elems, 64*i+bit)
			}
		}
	}
	return elems
}

func ex6_4() {
	var s IntSet

	// 向集合 s 中添加一些元素
	s.AddAll(1, 144, 9, 42)

	// 使用 Elems 方法获取集合中的所有元素
	elems := s.Elems()
	fmt.Println("Elements in set s:", elems) // [1 9 42 144]

	// 使用 range 遍历集合中的元素
	for _, elem := range elems {
		fmt.Println("Element:", elem)
	}
}

func main() {
	fmt.Println("==========ex6_1==========")
	ex6_1()
	fmt.Println("==========ex6_2==========")
	ex6_2()
	fmt.Println("==========ex6_3==========")
	ex6_3()
	fmt.Println("==========ex6_4==========")
	ex6_4()
}
