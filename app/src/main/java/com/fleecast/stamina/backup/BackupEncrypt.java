package com.fleecast.stamina.backup;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class BackupEncrypt {

    private static final String salt = "t784";
    private static final String cryptPassword = "";
    private static final String fileToBeCrypted = "c:\\Temp\\sampleFile.conf";
    private static final String fileToBeDecrypted = "c:\\Temp\\sampleFile.conf.crypt";
    private static final String fileDecryptedOutput = "c:\\Temp\\sampleFile.conf.decrypted";

    public void WriteBackUp(File outputFile, ArrayList<BackUpNotesStruct> noteInfoStructs) {

        FileOutputStream fos = null;
        ObjectOutputStream out = null;

        try {
            fos = new FileOutputStream(outputFile);
            out = new ObjectOutputStream(fos);
            //if (encryptionKey != null || !encryptionKey.isEmpty())
            //   out.writeBoolean(IS_ENCRYPTED);
            out.writeObject(noteInfoStructs);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void encryptFile(File path,String password,File tmpFile ) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        FileInputStream fis = new FileInputStream(tmpFile);
        FileOutputStream fos = new FileOutputStream(path);
        byte[] key = (salt + password).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = Arrays.copyOf(key,16);
        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);

        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        cos.flush();
        cos.close();
        fis.close();
    }

    public static void writeEncryptKey(Context context, String password) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        String str = "key_phrase";

        File traceFile = new File((context).getExternalFilesDir(null), "encrypt.key");

        InputStream fis = new ByteArrayInputStream(str.getBytes());
        FileOutputStream fos = new FileOutputStream(traceFile.getAbsolutePath());
        byte[] key = (salt + password).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = Arrays.copyOf(key,16);
        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        cos.flush();
        cos.close();
        fos.close();
        fis.close();
        //decryptFile(traceFile.getAbsolutePath(),"k",traceFile.getAbsolutePath() + (".crypt1"));
    }

    public static void removeEncryptKey(Context context) {
        File traceFile = new File((context).getExternalFilesDir(null), "encrypt.key");
        if(traceFile.exists())
            traceFile.delete();

    }

    public static boolean isThereEncryptKey(Context context) {
        File traceFile = new File((context).getExternalFilesDir(null), "encrypt.key");
        return traceFile.exists();


    }

    public static boolean testEncryptKey(Context context, String password) {

        File traceFile = new File((context).getExternalFilesDir(null), "encrypt.key");

        try {
            FileInputStream fis = new FileInputStream(traceFile);

            FileOutputStream fos = new FileOutputStream(traceFile.getAbsolutePath() + ".decrypt");

            byte[] key = (salt + password).getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-256");

            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec sks = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);

            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[8];
            while ((b = cis.read(d)) != -1) {
                fos.write(d, 0, b);
            }
            fos.flush();
            fos.close();
            cis.close();
            return true;
        }
         catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void decryptFile(String path,String password, String outPath) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(path);
        FileOutputStream fos = new FileOutputStream(outPath);
        byte[] key = (salt + password).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = Arrays.copyOf(key,16);
        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);

        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }

}