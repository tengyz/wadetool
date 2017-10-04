package com.wade.framework.common.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件工具
 * 
 * @Description 文件工具
 * @ClassName FileTools
 * @Date 2016年9月21日 上午10:02:42
 * @Author tengyizu
 */
public class FileTools {
    public static final int BUFF_LEN = 2048;
    
    public static void copyFile(File toFile, File fromFile, String excludeFileName, boolean cover) throws Exception {
        if (!fromFile.exists())
            return;
        
        if (excludeFileName != null) {
            if (fromFile.getAbsolutePath().indexOf(excludeFileName) >= 0)
                return;
        }
        
        if (fromFile.isFile()) {
            if (toFile.exists()) {
                if (toFile.isFile()) {
                    if (!cover) {
                        System.out.println("file " + toFile.getAbsolutePath() + " is exists");
                        return;
                    }
                }
                else {
                    toFile = new File(toFile, fromFile.getName());
                }
            }
            else {
                boolean succ = toFile.getParentFile().mkdirs();
                if (!succ)
                    return;
            }
            
            System.out.println("copy file from " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath());
            byte[] buf = new byte[BUFF_LEN];
            FileInputStream fi = new FileInputStream(fromFile);
            FileOutputStream fo = new FileOutputStream(toFile);
            
            int len = 0;
            while ((len = fi.read(buf)) > 0) {
                fo.write(buf, 0, len);
            }
            fi.close();
            fo.close();
        }
        else {
            if (toFile.isFile()) {
                System.out.println("复制目录出错：目标目录为文件！");
                return;
            }
            boolean succ = toFile.mkdirs();
            if (!succ)
                return;
            for (File file : fromFile.listFiles()) {
                copyFile(new File(toFile + File.separator + file.getName()), file, excludeFileName, cover);
            }
        }
    }
    
    public static void copyFile(File toFile, File fromFile, boolean cover) throws Exception {
        copyFile(toFile, fromFile, null, cover);
    }
    
    public static void copyFile(String toFilePath, String fromFilePath, String excludeFileName, boolean cover) throws Exception {
        copyFile(new File(toFilePath), new File(fromFilePath), excludeFileName, cover);
    }
    
    public static void copyFile(String toFilePath, String fromFilePath, boolean cover) throws Exception {
        copyFile(toFilePath, fromFilePath, null, cover);
    }
    
    public static void copyFile(File toFile, File fromFile) throws Exception {
        copyFile(toFile, fromFile, false);
    }
    
    public static void copyFile(String toFilePath, String fromFilePath) throws Exception {
        copyFile(toFilePath, fromFilePath, false);
    }
    
    public static void unzipFile(String toDirPath, String zipFilePath, String excludeFileName, boolean cover) throws Exception {
        if (!toDirPath.endsWith(File.separator))
            toDirPath = toDirPath + File.separator;
        
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            System.out.println(new File(zipFilePath).getAbsolutePath());
            ZipFile zipFile = new ZipFile(zipFilePath);
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>)zipFile.entries();
            
            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = enu.nextElement();
                String entryName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    // new File(toDirPath + File.separator
                    // +zipEntry.getName()).mkdirs();
                    continue;
                }
                if (excludeFileName != null) {
                    if (entryName.indexOf(excludeFileName) >= 0)
                        continue;
                }
                File file = new File(toDirPath + zipEntry.getName());
                
                if (file.exists() && !cover) {
                    System.out.println(entryName + " is exists!");
                    continue;
                }
                
                System.out.println("release file:" + entryName);
                bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    if (!parent.mkdirs())
                        return;
                }
                FileOutputStream fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, BUFF_LEN);
                
                int count;
                byte[] array = new byte[BUFF_LEN];
                while ((count = bis.read(array, 0, BUFF_LEN)) != -1) {
                    bos.write(array, 0, count);
                }
                
                bos.flush();
            }
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        finally {
            if (bos != null)
                bos.close();
            if (bis != null)
                bis.close();
        }
    }
    
    public static void deepSearchDo(File file, ToDo doer) throws Exception {
        if (file.exists()) {
            if (file.isFile())
                doer.toDo(file);
            else {
                for (File subFile : file.listFiles()) {
                    deepSearchDo(subFile, doer);
                }
            }
        }
    }
    
    public static void deepThreadSearchDo(File file, ToDo doOper) throws Exception {
        deepThreadSearchDo(file, doOper, new FileFilter() {
            @Override
            public boolean checkPass(File file) {
                return true;
            }
        });
    }
    
    public static void deepThreadSearchDo(File file, ToDo doOper, FileFilter filter) throws Exception {
        new DeepOperThread(file, doOper, filter).start();
    }
    
    public static void main(String[] args) throws Exception {
        // copyFile("F:\\Lin\\工具集\\acctcomp\\acctserv\\web\\acctcomp",
        // "F:\\Lin\\工具集\\acctcomp\\acctcomp\\web\\acctcomp", "CVS", true);
        unzipFile("F:\\Lin\\工具集\\acctcomp\\acctserv\\web\\\\", "F:\\Lin\\工具集\\acctcomp\\acctcomp\\web\\acctcomp.zip", "CVS", false);
    }
}

interface ToDo {
    public void toDo(File file) throws Exception;
}

interface FileFilter {
    public boolean checkPass(File file);
}

class DeepOperThread extends Thread {
    private ToDo doOper = null;
    
    private File file = null;
    
    private FileFilter filter = null;
    
    public DeepOperThread(File file, ToDo doOper, FileFilter filter) {
        this.doOper = doOper;
        this.file = file;
        this.filter = filter;
    }
    
    public void run() {
        if (!file.exists())
            return;
        if (!filter.checkPass(file))
            return;
        
        if (file.isFile()) {
            try {
                doOper.toDo(file);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        else {
            for (File subFile : file.listFiles()) {
                new DeepOperThread(subFile, doOper, filter).start();
            }
        }
    }
    
}
