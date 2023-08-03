package org.xpen.hello.compress;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
 
public class ZipUtils {
 
    /**
     * 解压
     */
    public static void decompressZip(String zipPath, String descDir) {
        
        File zipFile = new File(zipPath);
        boolean flag = false;
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding"));
            String encoding = getEncoding(zipPath);
            zip = new ZipFile(zipFile);
            zip.setCharset(Charset.forName(encoding));
            zip.extractAll(descDir);
            
            //必须关闭，要不然这个zip文件一直被占用着，要删删不掉，改名也不可以，移动也不行，整多了，系统还崩了。
            zip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    @SuppressWarnings("unchecked")
    private static String getEncoding(String path) throws Exception {
        String encoding = "GBK";
        ZipFile zipFile = new ZipFile(path);
        zipFile.setCharset(Charset.forName(encoding));
        List<FileHeader> list = zipFile.getFileHeaders();
        for (int i = 0; i < list.size(); i++) {
            FileHeader fileHeader = list.get(i);
            String fileName = fileHeader.getFileName();
            if (isMessyCode(fileName)) {
                encoding = "UTF-8";
                break;
            }
        }
        zipFile.close();
        return encoding;
    }
 
    private static boolean isMessyCode(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 当从Unicode编码向某个字符集转换时，如果在该字符集中没有对应的编码，则得到0x3f（即问号字符?）
            // 从其他字符集向Unicode编码转换时，如果这个二进制数在该字符集中没有标识任何的字符，则得到的结果是0xfffd
            if ((int) c == 0xfffd) {
                // 存在乱码
                return true;
            }
        }
        return false;
    }
 
}
