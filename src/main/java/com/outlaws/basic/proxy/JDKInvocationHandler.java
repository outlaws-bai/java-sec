package com.outlaws.basic.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @auther : outlaws
 * @date : 2022/4/18 15:48
 * @use :
 */

public class JDKInvocationHandler implements InvocationHandler, Serializable {

    private final Object target;

    public JDKInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 为了不影响测试Demo的输出结果，这里忽略掉toString方法
        if ("toString".equals(method.getName())) {
            return method.invoke(target, args);
        }

        System.out.println("即将调用[" + target.getClass().getName() + "]类的[" + method.getName() + "]方法...");
        Object obj = method.invoke(target, args);
        System.out.println("已完成[" + target.getClass().getName() + "]类的[" + method.getName() + "]方法调用...");

        return obj;
    }

}
