package com.phonegap.plugins.cropPlugin;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;

public class BitmapUtils {

	public BitmapUtils() {
		// TODO Auto-generated constructor stub
	}

	
	public  Bitmap scaleDown(Bitmap realImage, float maxImageSize, int width ,int height ,
	        boolean filter) {
	    //float ratio = Math.min(
	            //(float) maxImageSize / realImage.getWidth(),
	            //(float) maxImageSize / realImage.getHeight());
	    //int width = Math.round((float) ratio * realImage.getWidth());
	    //int height = Math.round((float) ratio * realImage.getHeight());

	    Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
	            height, filter);
	    return newBitmap;
	}
	
	public Bitmap getScaledBitmap(Bitmap source, int scalePercentage) {
		try {

			int dstWidth = source.getWidth() * scalePercentage / 100;
			int dstHeight = source.getHeight() * scalePercentage / 100;

			return Bitmap
					.createScaledBitmap(source, dstWidth, dstHeight, false);

		} catch (Exception e) {
		}
		return null;
	}

	
	
	public static int getCameraPhotoOrientation(Context context, Uri imageUri) {
		int rotate = 0;
		try {
			context.getContentResolver().notifyChange(imageUri, null);
			File imageFile = new File(imageUri.getPath());
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotate;
	}

	
}
