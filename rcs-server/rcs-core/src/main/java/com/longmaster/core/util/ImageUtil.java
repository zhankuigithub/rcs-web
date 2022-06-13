package com.longmaster.core.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageUtil {

    /**
     * dpi越大转换后越清晰，相对转换速度越慢
     */
    private static final Integer DPI = 100;

    /**
     * 转换后的图片类型
     */
    private static final String IMG_TYPE = "png";

    public static boolean createImage(String directory, byte[] pdfBytes) {
        try {
            List<byte[]> images = pdfToImage(pdfBytes);
            List<String> names = new ArrayList<>();

            for (int i = 0; i < images.size(); i++) {
                String name = directory + System.currentTimeMillis() + ".png";
                FileOutputStream fos = new FileOutputStream(name);
                fos.write(images.get(i)); // 生成临时文件
                fos.close();
                names.add(name);
            }

            if (names.size() > 1) { // 开始合成图片
                return verticalJoinTarget(names, directory + "temp.png");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * pdf转图片
     *
     * @param fileContent
     * @return
     * @throws IOException
     */
    public static List<byte[]> pdfToImage(byte[] fileContent) throws IOException {
        List<byte[]> result = new ArrayList<>();
        try (PDDocument document = PDDocument.load(fileContent)) {
            PDFRenderer renderer = new PDFRenderer(document);
            for (int i = 0; i < document.getNumberOfPages(); ++i) {
                BufferedImage bufferedImage = renderer.renderImageWithDPI(i, DPI);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, IMG_TYPE, out);
                result.add(out.toByteArray());
            }
        }
        return result;
    }

    /**
     * 垂直拼接图片
     *
     * @param names  源文件（完整路径）
     * @param target 目标文件（完整路径）
     * @return
     * @throws IOException
     */
    private static boolean verticalJoinTarget(List<String> names, String target) throws IOException {
        try {
            BufferedImage image = verticalJoinBufferedImage(names);
            if (image != null) {
                ImageIO.write(image, "png", new File(target));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 垂直拼接图片，按照第一张图片的大小进行拼接，尽量选择分辨率一致的图片
     *
     * @param names
     * @return
     * @throws IOException
     */
    private static BufferedImage verticalJoinBufferedImage(List<String> names) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(names.get(0)));
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        BufferedImage bg = new BufferedImage(width, height * names.size(), BufferedImage.TYPE_INT_RGB);
        try {
            Graphics2D g = bg.createGraphics();
            for (int i = 0; i < names.size(); i++) {
                g.drawImage(ImageIO.read(new File(names.get(i))), 0, i * height, width, height, null);
            }
            g.dispose();
            return bg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
