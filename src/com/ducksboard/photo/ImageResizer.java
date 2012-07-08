package com.ducksboard.photo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;


public class ImageResizer {

    private ContentResolver resolver;

    public ImageResizer(ContentResolver resolver) {
        this.resolver = resolver;
    }

    public void resize(Uri uri, int targetWidth, int targetHeight,
            OutputStream out) throws FileNotFoundException, IOException {
        InputStream is = resolver.openInputStream(uri);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opts);
        is.close();

        int srcWidth = opts.outWidth;
        int srcHeight = opts.outHeight;
        int sampleSize = 1;

        while (srcWidth / 2 > targetWidth || srcHeight / 2 > targetHeight) {
            srcWidth /= 2;
            srcHeight /= 2;
            sampleSize *= 2;
        }

        float scale = Math.min((float) targetWidth / srcWidth,
                (float) targetHeight / srcHeight);
        if (scale > 1.0f) {
            scale = 1.0f;
        }

        float rotation = getRotation(uri);

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = sampleSize;
        opts.inDither = false;
        opts.inScaled = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

        is = resolver.openInputStream(uri);
        Bitmap sampled = BitmapFactory.decodeStream(is, null, opts);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postRotate(rotation);

        Bitmap scaled = Bitmap.createBitmap(sampled, 0, 0, sampled.getWidth(),
                sampled.getHeight(), matrix, true);
        scaled.compress(Bitmap.CompressFormat.JPEG, 90, out);
        is.close();
    }

    private float getRotation(Uri uri) {
        if (uri.getScheme().equals("content")) {
            return getContentRotation(uri);
        } else if (uri.getScheme().equals("file")) {
            return getFileRotation(uri);
        } else {
            return 0f;
        }
    }

    private float getContentRotation(Uri uri) {
        Cursor cursor = resolver.query(uri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
                null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return 0f;
    }

    private float getFileRotation(Uri uri) {
        try {
            ExifInterface exif = new ExifInterface(uri.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90f;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180f;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270f;
            default:
                return 0f;
            }
        } catch (IOException e) {
            return 0f;
        }
    }
}
