package com.summery233.agent;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("Agent 已加载");
        // 添加你的代理逻辑
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Agent 已启动");
        // 添加你的代理逻辑
    }
}