package com.scissorhands;

import com.scissorhands.util.ZipUtil;

import java.io.*;

/**
 * Created by RoyChan on 2017/9/18.
 */
public class FileTest {


    private void test1(){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\电影\\1709jbiuygwdfiug1.srt")))) {

            File file = new File("E:\\电影\\1709jbiuygwdfiug11.srt");
            if (!file.exists())
                file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            int i = 1;
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("-->")){
                    writer.write(String.valueOf(i++) + "\n");
                }
                writer.write(line + "\n");
            }
            writer.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static String[] merge(String srcPath, String fileName) throws IOException {
        long mills = System.currentTimeMillis();
        String destPath = srcPath + "/merge_" + fileName;
        //logger.debug("源路径为： " + srcPath);
        //logger.debug("目标路径为： " + destPath);
        File f = new File(srcPath);
        File ff = new File(destPath);

        FileUtil.rmvDir(destPath);
        ff.mkdir();
        File[] files = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith("zip") && name.startsWith(fileName))
                    return true;
                else
                    return false;
            }
        });
        //logger.debug("正在合并，请耐心等待！");
        for (File file : files) {
            //logger.debug("The file to unzip is: " + file.getAbsolutePath());
            ZipUtil.unZip(file.getAbsolutePath(), destPath + "/");
        }
        String examName = null;
        String[] filenames = new File(destPath).list();
        for (String string : filenames) {
            //logger.debug(string);
            if (examName == null) examName = string;
            else if (examName != null && !examName.equals(string))
                System.out.println("存在不同Job的文件，合并结果有误。");//logger.error("存在不同Job的文件，合并结果有误。");
        }
        String fname = srcPath + "/" + fileName + ".zip";
        ZipUtil.zipDir(destPath, fname);
        //logger.debug("The merged file is: " + fname);
        //logger.debug("合并成功？" + new File(fname).exists());
        FileUtil.rmvDir(destPath);
        //todo 这里应该将其它子节点传过来的包删掉 测试阶段先不删，留着
        for (File file2Del : files) {
            //logger.debug("The file to delete is: " + file2Del.getAbsolutePath());
            FileUtil.rmvFile(file2Del);
        }
        long cost = System.currentTimeMillis() - mills;
        //logger.debug("success!耗时 ： " + cost);
        return new String[]{examName, fname};
    }

    public static void main(String[] args) throws IOException {
        FileTest.merge("C:\\Users\\aaa\\Desktop\\文档\\报表相关\\zip", "bbb");

//        System.out.println(s);
    }
}
