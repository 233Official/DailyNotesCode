package com.summery233;

import java.util.Scanner;

public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // 保持程序运行，等待用户输入以退出
        System.out.println("程序正在运行。输入 'exit' 以退出。");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("程序退出。");
                break;
            }
            System.out.println("输入了: " + input);
        }
        scanner.close();
    }
}