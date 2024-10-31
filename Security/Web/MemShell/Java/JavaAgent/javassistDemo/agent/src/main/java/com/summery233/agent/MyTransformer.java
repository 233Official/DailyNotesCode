package com.summery233.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.*;
import java.io.IOException;

public class MyTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        // 转换类名格式：com/example/target/TargetClass -> com.example.target.TargetClass
        String transformedClassName = className.replace('/', '.');
        String targetClassName = "com.example.target.TargetClass";

        if (transformedClassName.equals(targetClassName)) {
            try {
                // 获取默认的 ClassPool
                ClassPool classPool = ClassPool.getDefault();
                // 绑定当前线程的类加载器
                classPool.appendClassPath(new LoaderClassPath(loader));

                // 获取目标类
                CtClass ctClass = classPool.get(targetClassName);

                // 获取目标方法
                CtMethod ctMethod = ctClass.getDeclaredMethod("targetMethod");

                // 在方法开头插入代码
                ctMethod.insertBefore("{ System.out.println(\"方法开始执行\"); }");

                // 返回字节码
                byte[] byteCode = ctClass.toBytecode();
                ctClass.detach();
                return byteCode;
            } catch (NotFoundException | CannotCompileException | IOException e) {
                e.printStackTrace();
            }
        }
        // 返回 null 不修改字节码
        return null;
    }
}