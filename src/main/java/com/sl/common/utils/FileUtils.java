/**
 * 
 */
package com.sl.common.utils;

/**
 * @author 李旭日
 *
 */

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author gaoxuyang
 *
 */
public class FileUtils {

    /**
     * 
     * @param file 文件
     * @param path   文件存放路径
     * @param fileName 原文件名
     * @return
     */
     public static boolean upload(MultipartFile file, String path) {

            File dest = new File(path);
            if(!dest.getParentFile().exists()){
            	dest.getParentFile().mkdirs();
            }
            if(dest.exists() && dest.isFile()){
            	dest.delete();
            }
            try {
                //保存文件 出现FileNotFoundException
                file.transferTo(dest.getAbsoluteFile());
                return true;
            } catch (IllegalStateException e) {             
            	throw new RuntimeException("文件写入异常 FileUtils$upload file path = "+ dest.getAbsolutePath() ,e);
            } catch (IOException e) {
            	throw new RuntimeException("文件写入异常 FileUtils$upload file path = "+ dest.getAbsolutePath() ,e);
            }

        }
}
