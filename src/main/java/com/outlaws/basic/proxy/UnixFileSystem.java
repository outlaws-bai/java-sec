package com.outlaws.basic.proxy;

import java.io.File;

/**
 * @auther : outlaws
 * @date : 2022/4/18 15:48
 * @use :
 */

public class UnixFileSystem implements FileSystem {

    /* -- Disk usage -- */
    public int spaceTotal = 996;

    @Override
    public String[] list(File file) {
        System.out.println("正在执行[" + this.getClass().getName() + "]类的list方法，参数:[" + file + "]");

        return file.list();
    }

}