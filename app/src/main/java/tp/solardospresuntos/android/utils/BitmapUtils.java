package tp.solardospresuntos.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by filiperodrigues on 22/05/17.
 */

public class BitmapUtils {

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static Bitmap maskBitmap(Bitmap original, Bitmap mask) {
        if(mask == null) {
            return original;
        }
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        Rect src = new Rect(0, 0, original.getWidth() - 1, original.getHeight() - 1);
        Rect dest;

        if (original.getWidth() > original.getHeight()) {
            float imageRatio = original.getWidth() / (float) original.getHeight();

            float newOriginalWidth = mask.getHeight() * imageRatio;
            float shift = (newOriginalWidth - mask.getWidth()) / 2;

            dest = new Rect((int) -shift, 0, (int) Math.ceil(mask.getWidth() + shift), mask.getHeight());
        } else {
            float imageRatio = original.getHeight() / (float) original.getWidth();

            float newOriginalHeight = mask.getWidth() * imageRatio;
            float shift = (newOriginalHeight - mask.getHeight()) / 2;

            dest = new Rect(0, (int) -shift, mask.getWidth(), (int) Math.ceil(mask.getHeight() + shift));
        }

        mCanvas.drawBitmap(original, src, dest, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);

        return result;
    }

    public static String getResizedJpegBase64(Bitmap userImage, int maxSize, int quality) {
        String imageBase64;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Bitmap scaled = BitmapUtils.resize(userImage, maxSize, maxSize);
        if (scaled != null) {
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        } else {
            userImage.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        }

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imageBase64;
    }
}
