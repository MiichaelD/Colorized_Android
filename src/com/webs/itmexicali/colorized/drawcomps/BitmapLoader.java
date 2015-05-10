package com.webs.itmexicali.colorized.drawcomps;

import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.util.ServerCom;

import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapLoader {
	
	static HashMap<String,Bitmap> bitmaps=new HashMap<String,Bitmap>();

	
	
	/** load the origial Bitmap from URL*/
	public static Bitmap fetchImage(Context ctx, String URL, boolean saveOnCache) {
		if (bitmaps.containsKey(URL))
			return bitmaps.get(URL);
		
		Bitmap BitmapOrg = null;
		InputStream is = null;
		try {
			is = ServerCom.shared().openConnection(ServerCom.Method.GET, URL).getInputStream();
			BitmapOrg = BitmapFactory.decodeStream(is);
			bitmaps.put(URL, BitmapOrg);
		} catch (Exception e) {
			Log.e("BitmapLoader","Problem fetching Bitmap from: "+URL+" - now loading default Bitmap");
			e.printStackTrace();
			BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.app_icon);
		}
		
        return BitmapOrg;
    }
	
	/** load the origial Bitmap from URL*/
	public static Bitmap resizeImage(Context ctx, String URL, boolean saveOnCache ,float w, float h) {
		Bitmap original = null;
		String key = URL+"_"+w+"_"+h;
		
		if (bitmaps.containsKey(key))
			original =  bitmaps.get(key);
		
		else if (original == null && bitmaps.containsKey(URL))
			original =  bitmaps.get(URL);
		
		else if(original == null)
			original = fetchImage(ctx,URL,saveOnCache);
		
		
		original = resizeImage(original,w,h);
		
		
		if(saveOnCache)
			bitmaps.put(key, original);
		
        return original;
    }
	
	/** load the origial Bitmap from resource ID and return it 
	 * @param ctx context to get the resource from its Id
	 * @param resId the id of the resource to resize
	 * @param saveOnCache if true, it will be kept for future faster access
	 * @return the requested Bitmap */
	public static Bitmap getImage(Context ctx, int resId, boolean saveOnCache) {
		String key = Integer.toString(resId);
		if (bitmaps.containsKey(key))
			return bitmaps.get(key);
		
        Bitmap BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),resId);
        
        if(saveOnCache)
        	bitmaps.put(key, BitmapOrg);
        return BitmapOrg;

    }
	
	/** load the origial Bitmap from resource ID, resize it and return it 
	 * @param ctx context to get the resource from its Id
	 * @param resId the id of the resource to resize
	 * @param saveOnCache if true, it will be kept for future faster access
	 * @param w the width size to be set
	 * @param h the height size to be set
	 * @return the requested bitmap resized*/
	public static Bitmap resizeImage(Context ctx, int resId, boolean saveOnCache ,float w, float h) {
		String key = Integer.toString(resId)+"_"+w+"_"+h;
		if (bitmaps.containsKey(key))
			return bitmaps.get(key);
		
        Bitmap BitmapOrg = getImage(ctx, resId, false);
    	BitmapOrg = resizeImage(BitmapOrg, w, h);
        if(saveOnCache)
        	bitmaps.put(key, BitmapOrg);
        return BitmapOrg;

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
