package org.xpen.hello.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

/**
 * AES/CBC/PKCS7Padding 加密/解密测试
 *
 */
public class AesTest {
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    @Test
    public void testDecrypt() throws Exception {
        byte[] key = {100, 101, 54, 52, 100, 101, 101, 53, 51, 56, 98, 52, 57, 54, 56, 102};
        String iv = "63343433613466343862333365353333";
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Hex.decodeHex(iv));
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        
        FileInputStream inputStream = new FileInputStream(new File("C:\\ftptemp\\小白实操\\【教学目标】——眼神\\tmp\\cipher\\0.ts"));
        FileOutputStream outputStream = new FileOutputStream("C:\\ftptemp\\小白实操\\【教学目标】——眼神\\tmp\\cipher\\0tttt.ts");
        byte[] buffer = new byte[128];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

}
