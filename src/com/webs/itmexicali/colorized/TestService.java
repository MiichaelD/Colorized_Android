package com.webs.itmexicali.colorized;

import java.io.DataOutputStream;
import java.io.IOException;

import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.util.Const;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

//http://stackoverflow.com/questions/14928197/how-to-simulate-touch-from-background-service-with-sendevent-or-other-way/31623908#31623908
//https://android.googlesource.com/platform/frameworks/base/+/HEAD/cmds/input/src/com/android/commands/input/Input.java
public class TestService extends Service{

	Process m_process = null;
	DataOutputStream m_dataOut = null;
	
	public final String TAG = TestService.class.getSimpleName();
	private final int offsetX = 225, offsetY = 200;
	private float windowWidth = 0, windowHeight = 0;
	
	private WindowManager windowManager;
//	private ImageView chatHead;
	private Button[] buttons = new Button[4];
	
	  @Override
	  public IBinder onBind(Intent intent) {
	    // Not used
	    return null;
	  }
	
	  @SuppressWarnings("deprecation")
	@Override
	  public void onCreate() {
	    super.onCreate();
	    Const.i(TAG, "OnCreate");
	    askForRoot();
	
	    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
	    windowWidth = windowManager.getDefaultDisplay().getWidth();
	    windowHeight = windowManager.getDefaultDisplay().getHeight();
	    
	    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
	        WindowManager.LayoutParams.WRAP_CONTENT,
	        WindowManager.LayoutParams.WRAP_CONTENT,
	        WindowManager.LayoutParams.TYPE_PHONE,
	        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
	        PixelFormat.TRANSLUCENT);
	
	    params.gravity = Gravity.BOTTOM | Gravity.LEFT;
	    params.x = 0;
	    params.y = 150;
	    
	    for(int i = 0; i < buttons.length; i++){
	    	buttons[i] = new Button(this);
    		setActionToButton(buttons[i], i);
	    	switch(i){
	    	case 0:
		    	buttons[i].setText("Left <<");
		    	buttons[i].setBackgroundResource(R.drawable.com_mixpanel_android_arrowleft);
	    		params.x += offsetX;
	    		break;
	    	case 1:
		    	buttons[i].setText("Up ^^");
		    	buttons[i].setBackgroundResource(R.drawable.com_mixpanel_android_arrowleft);
	    		params.x += offsetX;
	    		params.y += offsetY;
	    		break;
	    	case 2:
		    	buttons[i].setText("Down <<");
		    	buttons[i].setBackgroundResource(R.drawable.com_mixpanel_android_arrowleft);
	    		params.y -= offsetY;
	    		break;
	    	case 3:
		    	buttons[i].setText("Right >>");
		    	buttons[i].setBackgroundResource(R.drawable.com_mixpanel_android_arrowright);
	    		params.x += offsetX;
	    		break;
	    	default:
	    		params.x += offsetX;
	    		break;
	    	}
		    windowManager.addView(buttons[i], params);
	    }
	    
	  }
	  
	  private void setActionToButton(Button btn, final int direction){
		  
		  btn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					switch(direction){
					case 0:
						drag(windowWidth/2, windowWidth/4, windowHeight/2, windowHeight/2, 80); 
						break;
					case 1:
						drag(windowWidth/2, windowWidth/2, windowHeight/2, windowHeight/4, 80); 
						break;
					case 2:
						drag(windowWidth/2, windowWidth/2, windowHeight/2, 3*windowHeight/4, 80); 
						break;
					case 3:
						drag(windowWidth/2, 3*windowWidth/4, windowHeight/2, windowHeight/2, 80); 
						break;
					}
					Const.i(TAG, "Button "+direction+" touched");					
				}
  			
  		});
	  }
	  
	  private void drag(float fromX, float toX, float fromY, float toY, int stepCount){
		  runSwipeCommand((int)fromX, (int)toX, (int)fromY, (int)toY, stepCount);
	  }
	  
	  private void askForRoot(){
			try {
				m_process = Runtime.getRuntime().exec("su");
				m_dataOut = new DataOutputStream(m_process.getOutputStream());
				Const.v(TAG, "Process created");
			    Const.i(TAG, "root Granted!");
			} catch (Exception e) { e.printStackTrace();}		 
	  }
	  
	  private void runSwipeCommand(final int fromX, final int toX, final int fromY, final int toY, final int duration){
		  Thread t1 = new Thread(new Runnable(){
			  public void run(){
				try {
					if(m_process != null && m_dataOut != null){
						String cmd = "/system/bin/input swipe "+fromX+" "+fromY+" "+toX+" "+toY+" "+ duration+"\n";
						m_dataOut.writeBytes(cmd);
						Const.v(TAG, "Command executed: "+cmd);
					}
				} catch (IOException e) { e.printStackTrace();}
			 }
	  	});
	  	t1.start();
	  }
	  
	
	  @Override
	  public void onDestroy() {
		Const.i(TAG, "onDestroy");
	    if (buttons != null)
		    for(int i = 0; i < buttons.length; i++)
		    	windowManager.removeView(buttons[i]);
	    
	    if (m_dataOut != null){
	    	try{
	    		m_dataOut.writeBytes("exit\n");
	    		m_dataOut.flush();
	    		m_dataOut.close();
	    		m_dataOut = null;
	    	}catch(Exception e){e.printStackTrace();}
	    }
	    
	    if (m_process != null){
		    try {
				  m_process.waitFor();
			} catch (InterruptedException e){e.printStackTrace();}
	    }
	    

	    super.onDestroy();
	  }
	}

