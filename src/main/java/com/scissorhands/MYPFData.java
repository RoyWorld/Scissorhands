package com.scissorhands;

import com.scissorhands.util.ChromeBrowserDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by RoyChan on 2017/9/16.
 */
public class MYPFData {

    public static WebDriver chromeDriver;
    private static final String url = "http://piaofang.maoyan.com/?ver=normal";
    private static final String picPath = "E:\\mypic";
    private static final String cropPath = "E:\\mypic\\cropImg";
    private static final String tranPicPath = "E:\\mypic\\tranPic";
    private static final int index = 4;

    private static List<String> movieNames;
    private static List<WebElement> pfWebElement;
    private static List<Integer> curpfLengths;

    static class Location{
        int left;
        int top;
        int width;
        int height;

        public Location(int left, int top, int width, int height) {
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static void init(){
        chromeDriver = ChromeBrowserDriver.getInstance();
        chromeDriver.get(url);
    }

    //取电影名字
    private static List<String> getMoviesName(){
        return IntStream.range(1, 24)
                .mapToObj(i ->{
                    String s = String.format(".//*[@id='ticket_tbody']/ul[%s]/li[1]/b", i);
                    WebElement webElement = MYPFData.chromeDriver.findElement(By.xpath(s));
                    System.out.println("movie_name: " + webElement.getText());
                    return webElement.getText();
                })
                .collect(Collectors.toList());
    }

    //票房元素
    private static List<WebElement> getPiaoFang(){
        return IntStream.range(1, 24)
                .mapToObj(i ->{
                    String s = String.format(".//*[@id='ticket_tbody']/ul[%s]/li[2]/b", i);
                    WebElement webElement = MYPFData.chromeDriver.findElement(By.xpath(s));
                    return webElement;
                })
                .collect(Collectors.toList());
    }

    /**
     * 截图函数
     * @param imagePath
     * @param scrollTop
     */
    private static void snapShot(String imagePath, int scrollTop){
        //窗口最大化
        chromeDriver.manage().window().maximize();

        //调用js滚动页面
        String scrollJs = String.format("window.scrollTo(0, %s);", scrollTop);
        JavascriptExecutor js;
        if (chromeDriver instanceof JavascriptExecutor){
            js = (JavascriptExecutor)chromeDriver;
        }else {
            System.out.println("the driver do not extend JavascriptExecutor");
            return;
        }
        js.executeScript(scrollJs);

        //执行截图
        TakesScreenshot takesScreenshot = (TakesScreenshot) chromeDriver;
        byte[] bais = takesScreenshot.getScreenshotAs(OutputType.BYTES);
        System.out.println(String.format("执行截图, imgPath: %s", imagePath));

        try{
            //图片输出流
            Path file = Paths.get(imagePath);
            Files.write(file, bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 抠图函数
     * @param imgPath
     * @param element
     * @param cropPath
     * @param scrollTop
     */
    private static void cropImg(String imgPath, WebElement element, String cropPath, int scrollTop){

        //获取页面元素及其位置、尺寸
        Point location = element.getLocation();
        Dimension size = element.getSize();

        //计算抠取区域的绝对坐标
        int left = location.getX();
        int top = location.getY() - scrollTop;

        try {
            //图片读取
            BufferedImage bufferedImage = ImageIO.read(new File(imgPath));
            //基础抠图
            BufferedImage destImage = baseCropImg(bufferedImage, new Location(left, top, size.getWidth(), size.getHeight()));
            //保存图片
            saveImg(cropPath, destImage, "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 基础抠图
     * @param srcImage
     * @param location
     */
    private static BufferedImage baseCropImg(BufferedImage srcImage, Location location){
        //抠取区域图片
        return srcImage.getSubimage(location.getLeft(), location.getTop(), location.getWidth(), location.getHeight());

    }

    /**
     * 图片放大
     * @param srcImage
     * @return
     */
    private static BufferedImage resizedImg(BufferedImage srcImage){
        //图片放大
        int newImageWidth = srcImage.getWidth() * 3;
        int newImageHeight = srcImage.getHeight() * 3;
        BufferedImage resizedImage = new BufferedImage(newImageWidth , newImageHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(srcImage, 0, 0, newImageWidth, newImageHeight, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * 图片保存
     * @param destPath
     */
    private static void saveImg(String destPath, BufferedImage destImage, String type){
        try {
            //图片输出流
            FileOutputStream fops = new FileOutputStream(new File(destPath));
            //图片写入，目标格式和实际格式相区别，会有颜色空间问题
            ImageIO.write(destImage, type, fops);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 开始进行截图
     */
    public static void start(){
        //截图
        //200，这个值是经过实际度量出来的，不同的浏览器和屏幕需要重新设定
        //576，这个值是通过48(行高)和12(行数)计算出来的
        String imgPath = picPath + File.separator + "%s";
        snapShot(String.format(imgPath, "1.png"), 200);
        snapShot(String.format(imgPath, "2.png"), 776);

        //取电影名字
        movieNames = getMoviesName();
        //取票房元素
        pfWebElement = getPiaoFang();

        //遍历处理图片
        for (int i = 0; i < 23; i++) {
            String movieName = movieNames.get(i);
            String cropImg = cropPath + File.separator + movieName + "_" + index + ".png";

            if (i < 12){
                cropImg(String.format(imgPath, "1.png"), pfWebElement.get(i), cropImg, 200);
            }else {
                cropImg(String.format(imgPath, "2.png"), pfWebElement.get(i), cropImg, 776);
            }
        }

    }

    /**
     * 取票房位数
     */
    private static void getCurpfLengths(){
        curpfLengths = pfWebElement.stream()
                .map(p -> (p.getText().length()))
                .collect(Collectors.toList());
    }

    /**
     * 切割成单个数字
     * @param movieIndex
     * @param picIndex
     */
    public static void singleDigitImg(int movieIndex, int picIndex){
        getCurpfLengths();

        int length = curpfLengths.get(movieIndex);

        String name = movieNames.get(movieIndex);

        try {
            //图片读取
            BufferedImage bufferedImage = ImageIO.read(new File(String.format(cropPath + File.separator + name + "_%s.png", picIndex)));
            //图片转灰度图
            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            op.filter(bufferedImage, bufferedImage);

            for (int i = 0; i < length; i++) {
                String singleDigitImg = String.format(tranPicPath + File.separator + "digit_%s_%s_%s.png", index, name, i);
                System.out.println(singleDigitImg);
                BufferedImage singleBufferedImg = null;
                if (i < length - 3){
                    //切分整数部分
                    //实验得出必须+1
                    singleBufferedImg = baseCropImg(bufferedImage, new Location(1+i*6, 0, 6, 16));
                }else if (i > length - 3){
                    //切分小数部分
                    //实验得出5是比较准确的
                    singleBufferedImg = baseCropImg(bufferedImage, new Location((int) (1+(i-1)*6+5), 0, 6, 16));
                }
                if (singleBufferedImg != null){
                    //保存图片
                    saveImg(singleDigitImg, resizedImg(singleBufferedImg), "png");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MYPFData.init();
        MYPFData.start();
        for (int i = 0; i < 23; i++) {
            final int i_final = i;
            new Thread(() -> {
                MYPFData.singleDigitImg(i_final, 4);
            }).start();
        }
    }

}
