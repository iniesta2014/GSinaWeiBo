
package weibo4android.logic.ui.imaCache;

import weibo4android.logic.ui.imaCache.ImageLoader.ImageCallback;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class Anseylodar {

    ImageLoader mImageLoader;

    public Anseylodar() {
        mImageLoader = new ImageLoader();
    }

    /**
     * 加载图片
     * 
     * @param imageView
     * @param url
     */
    public void showimgAnsy(ImageView imageView, String url) {
        imageView.setTag(url);
        Bitmap bitmap = mImageLoader.loadImage(url, 2, getImagelodarcallback(imageView));
        if (bitmap != null) {
            if (imageView.getTag().equals(url)) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 获取 callback接口
     * 
     * @param url
     * @param imageView
     * @return
     */
    private static ImageCallback getImagelodarcallback(final ImageView imageView) {

        return new ImageCallback() {
            @Override
            public void loadedImage(String path, Bitmap bitmap) {
                Log.i("tag", path + "---" + imageView.getTag().toString());
                if (path.equals(imageView.getTag().toString())) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        };
    }
}
