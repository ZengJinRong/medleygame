package pers.zengjinrong.medleygame;

import java.awt.*;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;

/**
 * 用于图像的裁剪切割
 *
 * @author ZengJinRong
 * @version 1.0
 */
public class ImageTailor {

    /**
     * 裁剪图像为正方形，边长为原图像长宽的最小值
     *
     * @param image     待裁剪的原始图像
     * @param component 容器对象，目的是用来创建裁剪后的每个图片对象
     * @return  原始图像经裁剪生成的图像
     */
    public static Image cutImageIntoSquare(Image image, Component component) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int length = Math.min(width, height);

        ImageFilter filter = new CropImageFilter(0, 0, length, length);
        Image imageSquare = component.createImage(new FilteredImageSource(image.getSource(), filter));
        return imageSquare;
    }

    /**
     * 等分切割图像
     *
     * @param image     用于等分切割的原始图像
     * @param rows      垂直方向上需要裁剪出的图片数量 - 行
     * @param cols      水平方向上需要裁剪出的图片数量 - 列
     * @param component 容器对象，目的是用来创建裁剪后的每个图片对象
     * @return  原始图像经等分切割生成的图像组，以数组形式保存
     */
    public static Image[][] divideImage(Image image, int rows, int cols, Component component) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int newHeight = height / cols;
        int newWidth = width / rows;
        Image[][] images = new Image[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ImageFilter filter = new CropImageFilter(col * newWidth, row * newHeight, newWidth, newHeight);
                images[row][col] = component.createImage(new FilteredImageSource(image.getSource(), filter));
            }
        }
        return images;
    }

}
