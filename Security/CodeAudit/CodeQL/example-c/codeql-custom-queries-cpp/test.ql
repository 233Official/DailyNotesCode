/**
 * 查找 malloc 函数调用的参数是指针类型的 sizeof 表达式
 *
 * @name Find malloc calls with sizeof expression as argument
 * @kind problem
 * @problem.severity error
 * @id cpp/example/malloc-sizeof-pointer-argument
 */

import cpp

// 获取所有的函数调用
// from FunctionCall call
// select call, "获取所有的函数调用"

// 获取所有的函数
// from Function func
// select func, "获取所有的函数"

// 获取所有的函数调用和相应的函数
// from FunctionCall call, Function func
// where call.getTarget() = func
// select call, "函数调用：" + call, func, "调用目标函数：" + func

// 查找 malloc 函数调用
// from FunctionCall call, Function func
// where call.getTarget() = func and func.getName() = "malloc"
// select call, "函数调用：" + call, func, "调用目标函数：" + func

// 查找 malloc 函数调用的参数是 sizeof 表达式
// from FunctionCall call, Function func, SizeofOperator sizeof
// where 
// 	call.getTarget() = func and 
// 	func.getName() = "malloc" and 
// 	call.getArgument(0) = sizeof
// select call, "函数调用：" + call, func, "调用目标函数：" + func, sizeof, "sizeof 表达式：" + sizeof

// 匹配 malloc 函数调用的参数是指针类型的 sizeof 表达式
// from FunctionCall call, Function func, SizeofExprOperator sizeof, Expr expr
// where 
// 	call.getTarget() = func and 
// 	func.getName() = "malloc" and 
// 	call.getArgument(0) = sizeof and
// 	sizeof.getExprOperand() = expr
// select call, func, sizeof, expr

// 匹配 malloc 函数调用的参数是指针类型的 sizeof 表达式
// from FunctionCall call, Function func, SizeofExprOperator sizeof, Expr expr
// where 
// 	call.getTarget() = func and 
// 	func.getName() = "malloc" and 
// 	call.getArgument(0) = sizeof and
// 	sizeof.getExprOperand() = expr
// select call, func, sizeof, expr, expr.getPrimaryQlClasses()

// 匹配 malloc 函数调用的参数是指针类型的 sizeof 表达式
// from FunctionCall call, Function func, SizeofExprOperator sizeof, VariableAccess va
// where 
// 	call.getTarget() = func and 
// 	func.getName() = "malloc" and 
// 	call.getArgument(0) = sizeof and
// 	sizeof.getExprOperand() = va
// select call, func, sizeof, va, va.getType(), va.getTarget(), va.getTarget().getType(), va.getTarget().getType().getPrimaryQlClasses()



// from
//     FunctionCall call,
//     Function func,
//     SizeofExprOperator sizeof,
//     VariableAccess va
// where
//     call.getTarget() = func and
//     func.getName() = "malloc" and
//     call.getArgument(0) = sizeof and
//     sizeof.getExprOperand() = va and
//     va.getTarget().getType() instanceof PointerType
// select call, func, sizeof, va

from
    FunctionCall call,
    Function func,
    SizeofExprOperator sizeof,
    VariableAccess va
where
    call.getTarget() = func and
    func.getName() = "malloc" and
    call.getArgument(0) = sizeof and
    sizeof.getExprOperand() = va and
    va.getType() instanceof PointerType
select call, func, sizeof, va