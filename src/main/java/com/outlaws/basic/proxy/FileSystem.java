package com.outlaws.basic.proxy;

import java.io.File;
import java.io.Serializable;

/**
 * @auther : outlaws
 * @date : 2022/4/18 15:47
 * @use :
 */

public interface FileSystem extends Serializable {
    String[] list(File file);
}
