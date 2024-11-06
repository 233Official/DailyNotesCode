package com.example;

import javassist.*;

public class Example {
    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("com.example.MyClass");
        
        // 添加一个新方法
        CtMethod newMethod = CtNewMethod.make(
            "public void newMethod() { System.out.println(\"新方法\"); }",
            cc
        );
        cc.addMethod(newMethod);
        
        // 加载修改后的类
        Class<?> clazz = cc.toClass();
        Object obj = clazz.getDeclaredConstructor().newInstance();
        clazz.getMethod("newMethod").invoke(obj);
    }
}