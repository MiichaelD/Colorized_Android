package com.webs.itmexicali.colorized.drawcomps;

import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapLoader {
	
	static HashMap<String,Bitmap> bitmaps=new HashMap<String,Bitmap>();

	
	/** load the origial Bitmap from resource ID	 */
	public static Bitmap getImage(Context ctx, int resId) {
		String key = Integer.toString(resId);
		if (bitmaps.containsKey(key))
			return bitmaps.get(key);
		
        Bitmap BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),resId);
        
        bitmaps.put(key, BitmapOrg);
        return BitmapOrg;

    }
	
	/** load the origial Bitmap from resource ID and resize it */
	public static Bitmap resizeImage(Context ctx, int resId, float w, float h) {
		String key = Integer.toString(resId)+"_"+w+"_"+h;
		if (bitmaps.containsKey(key))
			return bitmaps.get(key);
		
        Bitmap BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),resId);
        
        bitmaps.put(key, resizeImage(BitmapOrg, w, h));
        return bitmaps.get(key);

    }
	
	/** Resize a resource image to be shown on canvas */
	public static Bitmap resizeImage(Bitmap BitmapOrg, float w, float h) {        
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        float newWidth = w;
        float newHeight = h;

        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);

        // make a Drawable from Bitmap to allow to set the Bitmap
        // to the ImageView, ImageButton or what ever
        return resizedBitmap;

      }
}
