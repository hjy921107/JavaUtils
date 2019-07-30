package com.jyhuang.java.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BufferedImageUtils {

    /**
     * 将集合中的 BufferedImage 合成一个 BufferedImage
     *
     * @param bufImgList
     *
     * @return
     */
    public static BufferedImage mergeBufImgListToBufImg(List<BufferedImage> bufImgList) {

        int height = 0, width = 0; // 总高度，总宽度
        int _height; // 临时的高度 , 保存偏移高度
        int picNum = bufImgList.size();
        int[] heightArray = new int[picNum]; // 保存每个文件的高度
        BufferedImage buffer; // 保存图片流
        List<int[]> imgRGB = new ArrayList<>(); // 保存所有的图片的RGB
        int[] _imgRGB; // 保存一张图片中的RGB数据

        for (int i = 0; i < picNum; i++) {
            buffer = bufImgList.get(i);
            heightArray[i] = buffer.getHeight();// 图片高度
            _height = buffer.getHeight();

            if (i == 0) {
                width = buffer.getWidth();// 图片宽度
            }

            height += _height; // 获取总高度
            _imgRGB = new int[width * _height];// 从图片中读取RGB
            _imgRGB = buffer.getRGB(0, 0, width, _height, _imgRGB, 0, width);
            imgRGB.add(_imgRGB);
        }

        _height = 0; // 设置偏移高度为0

        // 生成新图片
        BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int __height; // 临时的高度，主要保存每个高度
        for (int i = 0; i < picNum; i++) {
            __height = heightArray[i];
            if (i != 0) {
                _height += __height; // 计算偏移高度
            }
            imageResult.setRGB(0, _height, width, __height, imgRGB.get(i), 0, width); // 写入流中
        }

        return imageResult;
    }

    /**
     * 将 BufferedImage 写入到目标文件
     *
     * @param bufImg
     * @param destFullPath 目标文件的完整路径（文件名无后缀）
     *
     * @return
     *
     * @throws IOException
     */
    public static String writeBufImgToFile(BufferedImage bufImg, String destFullPath)
            throws IOException {

        File destFile = new File(destFullPath);
        ImageIO.write(bufImg, destFullPath.substring(destFullPath.lastIndexOf(".") + 1),
                destFile); // 写图片

        return destFile.getName();
    }
}
