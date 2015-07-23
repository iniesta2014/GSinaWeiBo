
package weibo4android.logic.ui.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class PicUtill {
    /**
     * 根据一个网络连接(URL)获取bitmapDrawable图像
     * 
     * @param imageUri
     * @return
     */
    public static BitmapDrawable getfriendicon(URL imageUri) {

        BitmapDrawable icon = null;
        try {
            HttpURLConnection hp = (HttpURLConnection) imageUri
                    .openConnection();
            icon = new BitmapDrawable(hp.getInputStream());// 将输入流转换成bitmap
            hp.disconnect();// 关闭连接
        } catch (Exception e) {
        }
        return icon;
    }

    /**
     * 根据一个网络连接(String)获取bitmapDrawable图像
     * 
     * @param imageUri
     * @return
     */
    public static BitmapDrawable getcontentPic(String imageUri) {
        URL imgUrl = null;
        try {
            imgUrl = new URL(imageUri);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        BitmapDrawable icon = null;
        try {
            HttpURLConnection hp = (HttpURLConnection) imgUrl.openConnection();
            icon = new BitmapDrawable(hp.getInputStream());// 将输入流转换成bitmap
            hp.disconnect();// 关闭连接
        } catch (Exception e) {
        }
        return icon;
    }

    /**
     * 根据一个网络连接(URL)获取bitmap图像
     * 
     * @param imageUri
     * @return
     */
    public static Bitmap getusericon(URL imageUri) {
        // 显示网络上的图片
        URL myFileUrl = imageUri;
        Bitmap bitmap = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 根据一个网络连接(String)获取bitmap图像
     * 
     * @param imageUri
     * @return
     * @throws MalformedURLException
     */
    public static Bitmap getbitmap(String imageUri) throws MalformedURLException {
        // 显示网络上的图片
        URL myFileUrl = new URL(imageUri);
        Bitmap bitmap = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
