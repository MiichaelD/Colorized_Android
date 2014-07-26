package com.webs.itmexicali.colorized;

import java.io.InputStream;
import java.util.HashMap;

import com.webs.itmexicali.colorized.comm.ServerConn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapLoader {
	
	static HashMap<String,Bitmap> bitmaps=new HashMap<String,Bitmap>();

	
	/** load the origial Bitmap from URL*/
	public static Bitmap resizeImage(Context ctx, String URL, float w, float h) {
		
		if (bitmaps.containsKey(URL))
			return bitmaps.get(URL);
		
		Bitmap BitmapOrg = null;
		InputStream is = null;
		try {
			is = ServerConn.Connect(-1, URL, null).getInputStream();
			BitmapOrg = BitmapFactory.decodeStream(is);
			bitmaps.put(URL, resizeImage(BitmapOrg, w, h));
		} catch (Exception e) {
			if (Const.D){
				Log.e(Const.TAG+" - BitmapLoader","Problem fetching Bitmap from: "+URL);
				e.printStackTrace();
			}
			Log.e(Const.TAG+" - BitmapLoader","loading default Bitmap for: " + URL);
			BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.ic_launcher);
		}
		
        return resizeImage(BitmapOrg, w, h);
    }
	
	
	/** load the origial Bitmap from resource ID	 */
	public static Bitmap resizeImage(Context ctx, int resId, float w, float h) {
		String key = Integer.toString(resId);
		if (bitmaps.containsKey(key))
			return bitmaps.get(key);
		
        Bitmap BitmapOrg;
        BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),resId);
        
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
