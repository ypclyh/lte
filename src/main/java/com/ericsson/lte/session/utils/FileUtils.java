package com.ericsson.lte.session.utils;

import com.ericsson.lte.session.entity.ResponseResult;

import java.io.*;

public class FileUtils {
    public static void writeSessionToFile(ResponseResult responseResult) {
        String fileName = "E:" + File.separator + responseResult.getResponseBean().getUrl() + File.separator + responseResult.getResponseBean().getStartTime() + ".txt";
        File aFile = new File(fileName);
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(aFile);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(responseResult);
            objectOutputStream.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            close(fileOutputStream, objectOutputStream);
        }
    }

    private static void close(FileOutputStream fileOutputStream, ObjectOutputStream objectOutputStream) {
        if (objectOutputStream != null) {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        writeSessionToFile(null);
    }
}
