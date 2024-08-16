package org.springframework.core.io;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * 资源查找策略的默认实现类
 */
public class DefaultResourceLoader implements ResourceLoader{
    // 默认类路径先查找
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    @Override
    public Resource getResource(String location) {
        if (location.startsWith(CLASSPATH_URL_PREFIX)){
            // classpath下的资源，去掉前缀后返回
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
        }else {
            try {
                // 尝试当成url来处理
                URL url = new URL(location);
                return new UrlResource(url);
            }catch (MalformedURLException exception){
                // 当成文件系统下的资源处理
                return new FileSystemResource(location);
            }
        }
    }
}
