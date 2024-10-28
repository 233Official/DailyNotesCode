package com.summery233;
 
import java.lang.instrument.Instrumentation;
 
public class JavaAgentPremain {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("调用了premain-Agent!");
        System.err.println("传入参数：" + args);
    }
}