package com.jyhuang.java.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtils {

    public void createZip(String sourcePath, String zipPath) {
        log.info("begin CreateZipUtils:" + "sourcePath:" + sourcePath + "zipPath:" + zipPath);

        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
            // zos.setEncoding("gbk"); // 此处修改字节码方式。
            writeZip(new File(sourcePath), "", zos);
        } catch (Exception e) {
            log.error("创建ZIP文件失败", e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (Exception e) {
                log.error("关闭压缩输出流失败", e);
            }
        }
    }

    private void writeZip(File file, String parentPath, ZipOutputStream zos) throws Exception {
        if (file.exists()) {
            if (file.isDirectory()) { // 处理文件夹
                parentPath += file.getName() + File.separator;
                log.info("parentPath:" + parentPath);

                File[] files = file.listFiles();
                log.info("files.length:" + files.length);

                if (files.length != 0) {
                    for (File f : files) {
                        writeZip(f, parentPath, zos);
                    }
                } else { // 空目录则创建当前目录
                    zos.putNextEntry(new ZipEntry(parentPath));
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);

                    byte[] content = new byte[1024];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                        zos.write(content, 0, len);
                        zos.flush();
                    }
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (Exception e) {
                        log.error("关闭文件输入流失败", e);
                    }
                }
            }
        }
    }
}
