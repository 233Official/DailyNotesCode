package com.summery233.agent;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Agent 启动");
        inst.addTransformer(new MyTransformer());
    }
}