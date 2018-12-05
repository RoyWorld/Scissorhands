package com.scissorhands;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by RoyChan on 2017/9/25.
 */
public class FileUtil {

    /**
     * 创建目录
     * @param path
     * @throws Exception
     */
    public static  void mkDir(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                throw new Exception("创建目录失败：" + path + "!",e);
            }
        }
    }

    public static String removePathString(String pathStr, String removePathStr){
        String[] pathStrArray = pathStr.split("\\\\");
        List<String> pathList = new LinkedList<>(Arrays.asList(pathStrArray));
        int index = pathList.indexOf(removePathStr);
        if (index == -1) {
            System.out.println("removePathStr not found");
        } else {
            pathList.remove(index);
        }
        return combinaPathString(pathList.stream().toArray(String[]::new));
    }

    private static String combinaPathString(String... pathStrs){
        String result = "";
        for (String pathStr : pathStrs){
            result = result + pathStr + File.separator;
        }
        return result;
    }

    public static void main (String[] args) throws Exception{
        String dir = "D:\\project\\Dolphins\\src";
        System.out.println(FileUtil.printDirectoryTree(new File(dir)));
    }


    /**
     * 删除目录（包括目录下的所有文件）
     * @param dir
     */
    public static void rmvDir(String dir) {
        File file = new File(dir);
        rmvFile(file);
    }

    /**
     * 删除文件或目录（包括目录下的所有文件）
     * @param dir
     */
    public static void rmvFile(File dir) {
        // 删除子文件/子目录
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();

            for (int i = 0; i < children.length; i++) {
                rmvFile(children[i]);
            }
        }
        // 删除文件/目录
        try {
            dir.delete();
        } catch (Exception e) {
            // Nothing
        }
        return;
    }

    /**
     * Pretty print the directory tree and its file names.
     *
     * @param folder
     *            must be a folder.
     * @return
     */
    public static String printDirectoryTree(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        int indent = 0;
        StringBuilder sb = new StringBuilder();
        printDirectoryTree(folder, indent, sb);
        return sb.toString();
    }

    private static void printDirectoryTree(File folder, int indent,
                                           StringBuilder sb) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(folder.getName());
        sb.append("/");
        sb.append("\n");
        List<String> filesName = Stream.of(folder.listFiles()).map(file -> file.getName()).collect(Collectors.toList());
        FileFilter fileFilter = null;
        for (File file : folder.listFiles(fileFilter)) {
            if (file.isDirectory()) {
                printDirectoryTree(file, indent + 1, sb);
            } else {
                printFile(file, indent + 1, sb);
            }
        }

    }

    private static void printFile(File file, int indent, StringBuilder sb) {
        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(file.getName());
        sb.append("\n");
    }

    private static String getIndentString(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("|  ");
        }
        return sb.toString();
    }

    public static boolean deleteFile(String destFilePath)
    {
        File file = new File(destFilePath);
        if (file.exists()) {
            return file.delete();
        }

        return true;
    }

//    public static void zipDir(String srcDIr, String outFile) throws Exception {
//        File inputDir = new File(srcDIr);
//        FileOutputStream fos = new FileOutputStream(outFile);
//        ZipOutputStream zos = new ZipOutputStream(fos);
//        zos.setEncoding("UTF-8");
//        zipfile(inputDir.listFiles(), "", zos);
//        zos.close();
//        fos.close();
//    }
//
//    public static void zipDir2(String srcDIr, String outFile) throws Exception {
//        File inputDir = new File(srcDIr);
//        FileOutputStream fos = new FileOutputStream(outFile);
//        ZipOutputStream zos = new ZipOutputStream(fos);
//        zos.setEncoding("UTF-8");
//        File[] files = new File[1];
//        files[0] = inputDir;
//        zipfile(files, "", zos);
//        zos.close();
//        fos.close();
//    }

//    public static void zipfile(File[] files, String baseFolder, ZipOutputStream zos)
//            throws Exception {
//        byte[] buffer = new byte[2048];
//
//        // 输入问题
//        FileInputStream fis = null;
//        // 压缩条目
//        ZipEntry entry = null;
//        // 数据长度
//        int count = 0;
//        for (File file : files) {
//            if (file.isDirectory()) {
//                // 压缩子目录
//                String subFolder = baseFolder + file.getName() + File.separator;
//                zipfile(file.listFiles(), subFolder, zos);
//                continue;
//            }
//            entry = new ZipEntry(baseFolder + file.getName());
//            // 加入压缩条目
//            zos.putNextEntry(entry);
//            fis = new FileInputStream(file);
//            // 读取文件数据
//            while ((count = fis.read(buffer, 0, buffer.length)) != -1)
//                // 写入压缩文件
//                zos.write(buffer, 0, count);
//            fis.close();
//        }
//    }



    public static String getAbsoluteUrl(String relativeUrl) throws URISyntaxException {
        String classPath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (classPath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"
            // 截取路径中的jar包名
            classPath = classPath.substring(0, classPath.lastIndexOf("/") + 1);
        }
        URI base = new URI(classPath);
        URI abs = base.resolve(relativeUrl);
        return abs.toString();
    }
}
