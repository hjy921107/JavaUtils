package com.jyhuang.java.utils;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public static String createSingleImageFromPDF(String srcPdfFullPath, String destPicPath,
            String suffix) throws Exception {

        if (!validatePDF(srcPdfFullPath)) {
            throw new RuntimeException("需要转换的 PDF 文件错误，请确认后重试！");
        }

        // 解析 pdf，每页为一个 BufferedImage，最后返回 list
        List<BufferedImage> bufferImgList = getBufImgListFromPDF(srcPdfFullPath);

        // 将集合中的 BufferedImage 合成一个 BufferedImage
        BufferedImage imageResult = BufferedImageUtils.mergeBufImgListToBufImg(bufferImgList);

        // 指定目标文件全路径
        String destPicFullPath = destPicPath + File.separator + UUID.randomUUID().toString()
                .replace("-", "") + "." + suffix;

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
                    .substring(srcPdfFullPath.lastIndexOf(File.separator) + 1,
                            srcPdfFullPath.lastIndexOf(".")) + "_" + (++index) + "." + suffix);
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
    private static List<BufferedImage> getBufImgListFromPDF(String srcPdfFullPath)
            throws Exception {
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
}
