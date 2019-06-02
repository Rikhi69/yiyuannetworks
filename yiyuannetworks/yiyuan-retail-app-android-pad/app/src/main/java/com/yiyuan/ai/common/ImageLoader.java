package com.yiyuan.ai.common;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.widget.ImageView;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Copyright © 2018 Yiyuan Networks 上海义援网络科技有限公司. All rights reserved.
 *
 *  @author Created by Wangpeng on 2018/8/29 01:44.
 * Usage:
 */
public class ImageLoader {

    private static class ImageLoaderHolder {
        private static final ImageLoader INSTANCE = new ImageLoader();
    }

    private ImageLoader() {
    }

    public static ImageLoader getInstance() {
        return ImageLoaderHolder.INSTANCE;
    }



    /**
     * 显示本地图片
     * @param filePath
     * @param imageView
     */
    public void display(String filePath, ImageView imageView) {
        //处理图片被系统自动旋转的问题，但是比较慢
//        try {
//            imageView.setImageBitmap(rotateBitmapByDegree(BitmapFactory.decodeStream(new FileInputStream(filePath)),getBitmapDegree(filePath)));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        imageView.setImageURI(Uri.fromFile(new File(filePath)));
    }

    /**
     * 读取图片的旋转的角度
     * ExifInterface支持3中传参数的方式，
     * 1.指定文件路径
     * 2.通过FileDescriptor对象
     * 3.从原始的输入流
     * @param path 图片绝对路径
     * @return
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }



    /**
     * 保存文件到本地
     * @param destination
     * @param input
     * @throws IOException
     */
    private static String saveToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        File file = new File(destination);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        FileOutputStream downloadFile = new FileOutputStream(file);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        downloadFile.close();
        input.close();
        return destination;
    }

    /**
     * 根据url获取文件类型
     * @param urlpath
     * @return
     */
    private static String getFileType(String urlpath) {
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            return HttpURLConnection.guessContentTypeFromStream(new BufferedInputStream(conn.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
