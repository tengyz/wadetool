package com.wade.framework.common.util.file;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;

public class RemoveBom {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入需要去除UTF-8文件BOM的根路径：");
        String fileName = br.readLine();
        
        if (fileName == null)
            return;
        File file = new File(fileName);
        
        FileTools.deepThreadSearchDo(file, new ToDo() {
            @Override
            public void toDo(File file) throws Exception {
                try {
                    trimBom(file.getAbsolutePath());
                }
                catch (Exception e) {
                    System.out.println(file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        });
        
    }
    
    /**
     * 读取流中前面的字符，看是否有bom，如果有bom，将bom头先读掉丢弃
     * 
     * @param in
     * @return
     * @throws java.io.IOException
     */
    public static InputStream getInputStream(InputStream in) throws IOException {
        
        PushbackInputStream testin = new PushbackInputStream(in, 2);
        int ch = testin.read();
        if (ch != 0xEF) {
            testin.unread(ch);
        }
        else if ((ch = testin.read()) != 0xBB) {
            testin.unread(ch);
            testin.unread(0xEF);
        }
        else if (testin.read() != 0xBF) {
            System.out.println("错误的UTF-8格式文件");
        }
        else {
            // 不需要做，这里是bom头被读完了
            // // System.out.println("still exist bom");
            return testin;
        }
        testin.close();
        return null;
    }
    
    /**
     * 根据一个文件名，读取完文件，干掉bom头。
     * 
     * @param fileName
     * @throws java.io.IOException
     */
    public static void trimBom(String fileName) throws IOException {
        
        FileInputStream fin = new FileInputStream(fileName);
        // 开始写临时文件
        InputStream in = getInputStream(fin);
        if (in == null) {
            fin.close();
            return;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte b[] = new byte[4096];
        
        int len = 0;
        while (in.available() > 0) {
            len = in.read(b, 0, 4096);
            // out.write(b, 0, len);
            bos.write(b, 0, len);
        }
        
        in.close();
        fin.close();
        bos.close();
        
        // 临时文件写完，开始将临时文件写回本文件。
        System.out.println("[" + fileName + "]");
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(bos.toByteArray());
        out.close();
        System.out.println("处理文件" + fileName);
    }
    
}
