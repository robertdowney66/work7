package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * classpath下的资源
 */
public class ClassPathResource implements Resource{
    private final String path;

    public ClassPathResource(String path){
        this.path = path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        // this.getClass().getClassLoader(): ClassLoader是类加载器
        // 正常用于读取硬盘中，class文件的字节码，存入内存让其成为对象
        // 这里获取一个InputStream流，通过path读取对应classpath下的文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(this.path);
        if (is == null){
            throw new FileNotFoundException(this.path + "cannot be opened because it does not exist!");
        }
        return is;
    }
}
