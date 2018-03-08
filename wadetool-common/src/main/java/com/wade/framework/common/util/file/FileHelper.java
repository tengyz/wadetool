package com.wade.framework.common.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 文件处理工具类
 * @Description 文件处理工具类 
 * @ClassName   FileHelper 
 * @Date        2017年6月23日 上午10:19:11 
 * @Author      yz.teng
 */
public class FileHelper {
    private static final Logger log = Logger.getLogger(FileHelper.class);
    
    /**
     * 解压zip格式压缩包   
     * 对应的是ant.jar
     * @param sourceZip
     * @param destDir
     * @throws Exception
     * @Date        2017年6月23日 上午11:10:24 
     * @Author      yz.teng
     */
    
    private static void unzip(String sourceZip, String destDir) throws Exception {
        try {
            //            Project p = new Project();
            //            Expand e = new Expand();
            //            e.setProject(p);
            //            e.setSrc(new File(sourceZip));
            //            e.setOverwrite(false);
            //            e.setDest(new File(destDir));
            //            /*   
            //            ant下的zip工具默认压缩编码为UTF-8编码，   
            //                                而winRAR软件压缩是用的windows默认的GBK或者GB2312编码   
            //                                所以解压缩时要制定编码格式   
            //            */
            //            e.setEncoding("UTF-8");
            //            e.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("[/FileHelper/unzip]:" + e.fillInStackTrace());
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "解压zip格式压缩包异常！！！");
        }
    }
    
    /**
     * 解压rar格式压缩包。   
     * 对应的是java-unrar-0.3.jar，但是java-unrar-0.3.jar又会用到commons-logging-1.1.1.jar
     * @param sourceRar
     * @param destDir
     * @throws Exception
     * @Date        2017年6月23日 上午11:10:44 
     * @Author      yz.teng
     */
    private static void unrar(String sourceRar, String destDir) throws Exception {
        //        Archive a = null;
        //        FileOutputStream fos = null;
        //        try {
        //            a = new Archive(new File(sourceRar));
        //            FileHeader fh = a.nextFileHeader();
        //            while (fh != null) {
        //                if (!fh.isDirectory()) {
        //                    //1 根据不同的操作系统拿到相应的 destDirName 和 destFileName    
        //                    String compressFileName = "";
        //                    //先判断是否有中文
        //                    if (existZH(destDir)) {
        //                        compressFileName = fh.getFileNameW();
        //                    }
        //                    else {
        //                        compressFileName = fh.getFileNameString().trim();
        //                    }
        //                    
        //                    String destFileName = "";
        //                    String destDirName = "";
        //                    //非windows系统     
        //                    if (File.separator.equals("/")) {
        //                        destFileName = destDir + compressFileName.replaceAll("\\\\", "/");
        //                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
        //                        //windows系统      
        //                    }
        //                    else {
        //                        destFileName = destDir + compressFileName.replaceAll("/", "\\\\");
        //                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
        //                    }
        //                    //2创建文件夹     
        //                    File dir = new File(destDirName);
        //                    if (!dir.exists() || !dir.isDirectory()) {
        //                        dir.mkdirs();
        //                    }
        //                    //3解压缩文件     
        //                    fos = new FileOutputStream(new File(destFileName));
        //                    a.extractFile(fh, fos);
        //                    fos.close();
        //                    fos = null;
        //                }
        //                fh = a.nextFileHeader();
        //            }
        //            a.close();
        //            a = null;
        //        }
        //        catch (Exception e) {
        //            throw e;
        //        }
        //        finally {
        //            if (fos != null) {
        //                try {
        //                    fos.close();
        //                    fos = null;
        //                }
        //                catch (Exception e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //            if (a != null) {
        //                try {
        //                    a.close();
        //                    a = null;
        //                }
        //                catch (Exception e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }
    }
    
    public static boolean existZH(String str) {
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            return true;
        }
        return false;
    }
    
    /**   
     * 解压缩   
     */
    public static void deCompress(String sourceFile, String destDir) throws Exception {
        //保证文件夹路径最后是"/"或者"\"     
        char lastChar = destDir.charAt(destDir.length() - 1);
        if (lastChar != '/' && lastChar != '\\') {
            destDir += File.separator;
        }
        //根据类型，进行相应的解压缩     
        String type = sourceFile.substring(sourceFile.lastIndexOf(".") + 1);
        if (type.equals("zip")) {
            FileHelper.unzip(sourceFile, destDir);
        }
        else if (type.equals("rar")) {
            FileHelper.unrar(sourceFile, destDir);
        }
        else {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "只支持zip和rar格式的压缩包！！！！");
        }
    }
    
    /**
     * 读取某个文件夹下的所有文件
     * @param filepath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @Date        2017年6月23日 上午11:09:44 
     * @Author      yz.teng
     */
    public static IDataList readfile(String filepath) throws FileNotFoundException, IOException {
        IDataList list = new DataArrayList();
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                //文件
                IDataMap data = new DataHashMap();
                data.put("path", file.getAbsolutePath());
                list.add(data);
            }
            else if (file.isDirectory()) {
                //文件夹
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "\\" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        IDataMap data = new DataHashMap();
                        data.put("path", readfile.getAbsolutePath());
                        list.add(data);
                    }
                    else if (readfile.isDirectory()) {
                        readfileSub(filepath + "\\" + filelist[i], list);
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("[/FileHelper/readfile]:" + e.fillInStackTrace());
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "读取文件异常！！！");
        }
        //        log.info("list=:" + list);
        return list;
    }
    
    public static void readfileSub(String filepath, IDataList list) throws FileNotFoundException, IOException {
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                //文件
                IDataMap data = new DataHashMap();
                data.put("path", file.getPath());
                list.add(data);
            }
            else if (file.isDirectory()) {
                //文件夹
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "\\" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        IDataMap data = new DataHashMap();
                        data.put("path", readfile.getPath());
                        list.add(data);
                    }
                    else if (readfile.isDirectory()) {
                        readfileSub(filepath + "\\" + filelist[i], list);
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("[/FileHelper/readfile]:" + e.fillInStackTrace());
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "读取文件异常！！！");
        }
        //        log.info("list=:" + list);
    }
    
    /**
     * 删除某个文件夹,及下的所有文件夹和文件
     * @param delpath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @Date        2017年6月23日 上午11:08:58 
     * @Author      yz.teng
     */
    public static boolean delFolder(String delpath) throws FileNotFoundException, IOException {
        try {
            
            File file = new File(delpath);
            if (!file.isDirectory()) {
                file.delete();
            }
            else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(delpath + "\\" + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                    }
                    else if (delfile.isDirectory()) {
                        delFolder(delpath + "\\" + filelist[i]);
                    }
                }
                file.delete();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("[/FileHelper/deletefile]:" + e.fillInStackTrace());
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "删除文件异常！！！");
            
        }
        return true;
    }
    
    /**
     * 获取客户端浏览器类型、编码下载文件名
     * 
     * @param request
     * @param fileName
     * @return String
     * @author fantasy
     * @date 2014-1-9
     */
    public static String encodeFileName(HttpServletRequest request, String fileName) {
        String userAgent = request.getHeader("User-Agent");
        String rtn = "";
        try {
            String new_filename = URLEncoder.encode(fileName, "UTF8");
            // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
            rtn = "filename=\"" + new_filename + "\"";
            if (userAgent != null) {
                userAgent = userAgent.toLowerCase();
                // IE浏览器，只能采用URLEncoder编码
                if (userAgent.indexOf("msie") != -1) {
                    rtn = "filename=\"" + new_filename + "\"";
                }
                // Opera浏览器只能采用filename*
                else if (userAgent.indexOf("opera") != -1) {
                    rtn = "filename*=UTF-8''" + new_filename;
                }
                // Safari浏览器，只能采用ISO编码的中文输出
                else if (userAgent.indexOf("safari") != -1) {
                    rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
                }
                // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
                else if (userAgent.indexOf("applewebkit") != -1) {
                    //                    new_filename = MimeUtility.encodeText(fileName, "UTF8", "B");
                    rtn = "filename=\"" + new_filename + "\"";
                }
                // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
                else if (userAgent.indexOf("mozilla") != -1) {
                    rtn = "filename*=UTF-8''" + new_filename;
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("[/FileHelper/encodeFileName]:" + e.fillInStackTrace());
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "获取客户端浏览器类型、编码下载文件名异常！！！");
        }
        return rtn;
    }
    
    /**
     * 文件下载
     *  
     * @param request
     * @param response
     * @param contentType
     * @param filePath
     * @param fileName
     * @throws Exception void
     * @author fantasy 
     * @date 2013-12-10
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, String filePath, String fileName) throws Exception {
        request.setCharacterEncoding("UTF-8");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            long fileLength = new File(filePath).length();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-disposition", "attachment;" + encodeFileName(request, fileName));
            response.setHeader("Content-Length", String.valueOf(fileLength));
            bis = new BufferedInputStream(new FileInputStream(filePath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("[/FileHelper/download]:" + e.fillInStackTrace());
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "文件下载异常！！！");
        }
        finally {
            bis.close();
            bos.close();
        }
        
    }
    
    /**
     * 多文件下载
     * @param request
     * @param response
     * @param list(filePath,fileName)
     * @throws Exception 
     * @Date        2017年7月26日 上午11:23:47 
     * @Author      yz.teng
     */
    public static void downloads(HttpServletRequest request, HttpServletResponse response, IDataList list) throws Exception {
        
        String tempUrl = "";//临时文件存储路径
        String tmpPath = "";
        if (null != list && list.size() > 0) {
            IDataMap getData = list.first();
            String filePath = getData.getString("filePath");
            String fileName = getData.getString("fileName");
            tmpPath = filePath + "_temp_zip" + File.separator;
            if (!(new File(tmpPath).exists())) {
                new File(tmpPath).mkdirs();
            }
            tempUrl = tmpPath + fileName + ".zip";
        }
        
        File zipFile = new File(tempUrl); // 定义压缩文件名称  
        ZipOutputStream zipOut = null;// 声明压缩流对象  
        //将要压缩的文件加入到压缩输出流中
        try {
            zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            for (int i = 0; i < list.size(); i++) {
                IDataMap getData = list.getData(i);
                String filePath = getData.getString("filePath");
                String fileName = getData.getString("fileName");
                File file = new File(filePath);
                try {
                    zipOut.putNextEntry(new ZipEntry(fileName)); // 设置ZipEntry对象
                    // 支持中文  
                    //                    zipOut.setEncoding("GBK");
                    //将文件写入到压缩文件中
                    writeFile(file, zipOut);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    log.error("[/FileHelper/downloads]:" + e.fillInStackTrace());
                    Thrower.throwException(BizExceptionEnum.ERROR_MSG, "zipOut.putNextEntry文件下载异常！！！");
                }
                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("[/FileHelper/downloads]:" + e.fillInStackTrace());
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "zipOut.putNextEntry文件下载异常！！！");
        }
        finally {
            zipOut.close();
        }
        
        try {
            // 以流的形式下载文件。
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(zipFile));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            request.setCharacterEncoding("UTF-8");
            //清空response
            response.reset();
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;" + encodeFileName(request, zipFile.getName()));
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            zipFile.delete(); //将生成的服务器端文件删除
            //删除目录
            File getDir = new File(tmpPath);
            getDir.delete();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            log.error("[/FileHelper/downloads]:" + ex.fillInStackTrace());
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "文件下载异常！！！");
        }
    }
    
    /**
     * write file
     * @param file
     * @param out
     * @param full_name
     * @param real_name
     * @param xml
     * @param dataset
     * @return File
     * @throws Exception
     */
    private static void writeFile(File file, OutputStream zipout) throws Exception {
        FileInputStream in = new FileInputStream(file);
        FileMan.writeInputToOutput(in, zipout, true);
        in.close();
    }
    
    public static void main(String args[]) throws Exception {
        
        //        FileHelper.unrar("Z:\\jar-ant.rar", "Z:\\");
        //        FileHelper.unzip("Z:\\JavaScript你必须掌握的8个基本知识点.rar.zip", "Z:\\");
        
        //        System.out.println("list=:" + FileHelper.readfile("Z:\\apache-tomcat-7.0.39"));
        
        FileHelper.delFolder("Z:\\jar-ant");
    }
}
