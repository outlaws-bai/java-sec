package com.outlaws.basic.proxy;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @auther : outlaws
 * @date : 2022/4/18 15:49
 * @use :
 */

public class FileSystemProxyTest {

    public static void main(String[] args) {
        // 创建UnixFileSystem类实例
        FileSystem fileSystem = new UnixFileSystem();

        // 使用JDK动态代理生成FileSystem动态代理类实例
        FileSystem proxyInstance = (FileSystem) Proxy.newProxyInstance(
                FileSystem.class.getClassLoader(),// 指定动态代理类的类加载器
                new Class[]{FileSystem.class}, // 定义动态代理生成的类实现的接口
                new JDKInvocationHandler(fileSystem)// 动态代理处理类
        );

        System.out.println("动态代理生成的类名:" + proxyInstance.getClass());
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("动态代理生成的类名toString:" + proxyInstance.toString());
        System.out.println("----------------------------------------------------------------------------------------");

        // 使用动态代理的方式UnixFileSystem方法
        String[] files = proxyInstance.list(new File("."));

        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("UnixFileSystem.list方法执行结果:" + Arrays.toString(files));
        System.out.println("----------------------------------------------------------------------------------------");

    }

}
