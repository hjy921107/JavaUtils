package com.jyhuang.java.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class PDFUtils {

    /**
     * 将 pdf 转换成一张指定格式的图片
     *
     * @param srcPdfFullPath pdf 文件的全路径（完整路径 + 文件名）
     * @param destPicPath    转换之后的目标路径（文件夹名称）
     * @param suffix         指定图片格式后缀
     *
     * @return destPicFullPath 最后生成一张图片的全路径
     *
     * @throws Exception
     */
    public static String createSingleImageFromPDF(String srcPdfFullPath, String destPicPath, String suffix) throws Exception {

        if (!validatePDF(srcPdfFullPath)) {
            throw new RuntimeException("需要转换的 PDF 文件错误，请确认后重试！");
        }

        // 解析 pdf，每页为一个 BufferedImage，最后返回 list
        List<BufferedImage> bufferImgList = getBufImgListFromPDF(srcPdfFullPath);

        // 将集合中的 BufferedImage 合成一个 BufferedImage
        BufferedImage imageResult = BufferedImageUtils.mergeBufImgListToBufImg(bufferImgList);

        // 指定目标文件全路径
        String destPicFullPath = destPicPath + File.separator + UUID.randomUUID().toString().replace("-", "") + "." + suffix;

        // 将 BufferedImage 写入到目标文件，并返回
        return BufferedImageUtils.writeBufImgToFile(imageResult, destPicFullPath);
    }


    /**
     * 将 pdf 每一页内容换成一张指定格式的图片
     *
     * @param srcPdfFullPath pdf 文件的全路径（完整路径 + 文件名）
     * @param destPicPath    转换之后的目标路径（文件夹名称）
     * @param suffix         指定图片格式后缀
     *
     * @return List<String>：最终生成所有图片的文件名的集合
     *
     * @throws Exception
     */
    public static List<String> createMultiImagesFromPDF(String srcPdfFullPath, String destPicPath,
            String suffix) throws Exception {

        List<String> imagePathResult = new ArrayList<>();

        if (!validatePDF(srcPdfFullPath)) {
            throw new RuntimeException("需要转化的文件格式不正确，请确认为 pdf 格式文件！");
        }

        List<BufferedImage> bufferImgList = getBufImgListFromPDF(srcPdfFullPath);

        File destFile;
        int index = 0;
        for (BufferedImage image : bufferImgList) {
            destFile = new File(destPicPath + File.separator + srcPdfFullPath
                    .substring(srcPdfFullPath.lastIndexOf(File.separator) + 1, srcPdfFullPath.lastIndexOf(".")) + "_" + (++index) + "." + suffix);
            ImageIO.write(image, suffix, destFile); // 写图片
            imagePathResult.add(destFile.getName());
        }

        return imagePathResult;
    }

    /**
     * 对 pdf 文件的简单校验
     *
     * @param srcPdfFullPath
     *
     * @return true:校验通过；false:校验失败
     */
    private static boolean validatePDF(String srcPdfFullPath) {
        if (srcPdfFullPath != null) {
            if (!srcPdfFullPath.toLowerCase().endsWith(".pdf")) {
                return false;
            } else {
                File file = new File(srcPdfFullPath);
                return file.canRead();
            }
        } else {
            return false;
        }

    }

    /**
     * 获取 pdf 的每一页内容
     *
     * @param srcPdfFullPath
     *
     * @return List<BufferedImage> 每页内容（BufferedImage）对象的集合
     *
     * @throws Exception
     */
    private static List<BufferedImage> getBufImgListFromPDF(String srcPdfFullPath) throws Exception {
        Document document = new Document();
        document.setFile(srcPdfFullPath);

        float scale = 2f;
        float rotation = 0f;
        List<BufferedImage> bufferImgList = new ArrayList<>();
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage image = (BufferedImage) document
                    .getPageImage(i, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, rotation,
                            scale);
            bufferImgList.add(image);
        }
        document.dispose();

        return bufferImgList;
    }

    /**
     * 获取PDF文件中的文本内容与图片内容
     *
     * @param ins  文件输入流
     * @param sort 是否排序
     *
     * @return 对象（content：文本内容；imgList：图片内容）
     */
    public static Map<String, Object> getContentFromPdf(InputStream ins, boolean sort) {
        Map<String, Object> map = new HashMap<>();
        PDFTextStripper stripper;
        StringBuilder content = new StringBuilder();
        List<String> list = new LinkedList<>();
        int pageSize;
        PDDocument document = null;
        PDPage page;
        PDResources resources;
        Iterable<COSName> objectNames;
        PDImageXObject pdImageXObject;
        BufferedImage image = null;
        FileOutputStream fos = null;
        String fileName;
        try {
            document = PDDocument.load(ins);    // 加载PDF文件
            pageSize = document.getNumberOfPages(); // 获取总页数
            stripper = new PDFTextStripper();
            stripper.setSortByPosition(sort);   // 是否排序
            for (int i = 0; i < pageSize; i++) {
                // 获取纯文本内容
                stripper.setStartPage(i + 1);   // 起始页
                stripper.setEndPage(i + 1);
                content.append(stripper.getText(document));

                // 获取图片内容
                page = document.getPage(i);
                resources = page.getResources();
                objectNames = resources.getXObjectNames();
                if (null != objectNames) {
                    for (COSName cosName : objectNames) {
                        if (resources.isImageXObject(cosName)) {
                            pdImageXObject = (PDImageXObject) resources.getXObject(cosName);
                            image = pdImageXObject.getImage();
                            fileName = UUID.randomUUID() + ".png";
                            fos = new FileOutputStream(PropsUtils
                                    .getProperties("uploadPath") + File.separator + fileName);
                            ImageIO.write(image, "png", fos);
                            list.add(fileName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取PDF图片出现错误，错误为：" + e.toString());
        } finally {
            try {
                if (null != fos) {
                    fos.flush();
                    fos.close();
                }
                if (null != image) {
                    image.flush();
                }
                if (null != document) {
                    document.close();
                }
            } catch (Exception e) {
                log.error("获取PDF图片出现错误，错误为：" + e.toString());
            }
        }

        map.put("content", content.toString());
        map.put("imgList", list);

        return map;
    }

    /**
     * 获取PDF中的纯文本内容
     *
     * @param ins  文件输入流
     * @param sort 是否排序
     *
     * @return 文本内容
     */
    public static String getTextFromPdf(InputStream ins, boolean sort) {
        String content = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(ins);    // 加载PDF文件
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(sort);   // 是否排序
            stripper.setStartPage(1);   // 起始页
            stripper.setEndPage(document.getNumberOfPages());    // 结束页
            // 获取文本内容
            content = stripper.getText(document);
        } catch (Exception e) {
            log.error("获取PDF内容出现错误，错误为：" + e.toString());
        } finally {
            try {
                if (null != document) {
                    document.close();
                }
            } catch (Exception e) {
                log.error("获取PDF内容出现错误，错误为：" + e.toString());
            }
        }
        return content;
    }

    /**
     * 获取PDF中的图片内容
     *
     * @param ins 文件输入流
     *
     * @return 图片名称集合
     */
    public static List<String> getImageFromPdf(InputStream ins) {
        List<String> list = new LinkedList<>();
        int pageSize;
        PDDocument document = null;
        PDPage page;
        PDResources resources;
        Iterable<COSName> objectNames;
        PDImageXObject pdImageXObject;
        BufferedImage image = null;
        FileOutputStream fos = null;
        String fileName;
        try {
            document = PDDocument.load(ins);    // 加载PDF文件
            pageSize = document.getNumberOfPages();
            for (int i = 0; i < pageSize; i++) {
                page = document.getPage(i);
                resources = page.getResources();
                objectNames = resources.getXObjectNames();
                if (null != objectNames) {
                    for (COSName cosName : objectNames) {
                        if (resources.isImageXObject(cosName)) {
                            pdImageXObject = (PDImageXObject) resources.getXObject(cosName);
                            image = pdImageXObject.getImage();
                            fileName = UUID.randomUUID() + ".png";
                            fos = new FileOutputStream(PropsUtils
                                    .getProperties("uploadPath") + File.separator + fileName);
                            ImageIO.write(image, "png", fos);
                            list.add(fileName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取PDF图片出现错误，错误为：" + e.toString());
        } finally {
            try {
                if (null != fos) {
                    fos.flush();
                    fos.close();
                }
                if (null != image) {
                    image.flush();
                }
                if (null != document) {
                    document.close();
                }
            } catch (Exception e) {
                log.error("获取PDF图片出现错误，错误为：" + e.toString());
            }
        }
        return list;
    }
}
