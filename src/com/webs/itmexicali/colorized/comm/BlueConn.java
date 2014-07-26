package com.webs.itmexicali.colorized.comm;

import com.webs.itmexicali.colorized.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BlueConn {

	
    // Message types sent from the BlueInterfaceService Handler
    public static final int MESSAGE_STATE_CHANGE = 1, MESSAGE_READ = 2, MESSAGE_WRITE = 3, MESSAGE_DEVICE_NAME = 4, MESSAGE_TOAST = 5, SYNC_CONNECTION = 6;
    
    // Intent request codes
    public static final int	REQUEST_CONNECT_DEVICE = 1, REQUEST_ENABLE_BT = 2, REQUEST_LOGIN = 3;
    
    // Key names received from the BlueInterfaceService Handler
    public static final String DEVICE_NAME = "device_name", TOAST = "toast";

    // Name of the connected device
    @SuppressWarnings("unused")
	private static String mConnectedDeviceName = null;
        
    // Local Bluetooth adapter
    private static BluetoothAdapter mBluetoothAdapter = null;
    
    //this class TAG
    private static final String TAG = "-BlueConn";
    
    //Constant to get an object from the dictionary
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    
   
    /** The Handler that gets connection information back from the
    *  BluethootService to update the title bar for devices of 11- API level */
   @SuppressLint("HandlerLeak")
   private final static Handler mHandler = new Handler() {    	
   @Override
   public void handleMessage(Message msg) {
           switch (msg.what) {
           
           case MESSAGE_STATE_CHANGE:
               if(Const.D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
               switch (msg.arg1) {
               case BluetoothService.STATE_CONNECTED:
                   //Let arduino Know the ratio we're going to use.
                   break;
               case BluetoothService.STATE_CONNECTING:
                   break;
               case BluetoothService.STATE_LISTEN:
               case BluetoothService.STATE_NONE:
                   break;
               }
               break;
               
           case SYNC_CONNECTION:
           	if(Const.D)
       			Log.i(TAG,"SYNCED Connection: "+msg.obj);
           	break;
               
           case MESSAGE_DEVICE_NAME:
               // save the connected device's name
               mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
               //MessageManager.getIns().mUIsend("Connected to " + mConnectedDeviceName, false);
               break;
               
           case MESSAGE_TOAST:
        	   //MessageManager.getIns().mUIsend(msg.getData().getString(TOAST), false);
               break;
               
           case MESSAGE_WRITE:
               //This will be handled by MessageManager
               break;
               
           case MESSAGE_READ:
           	//This will be handled by MessageManager
               break;
           }
       }
   };
    
    
	
	/** Makes the device discoverable by other bluetooth devices 
	 * @param ctx, activity context */
    public static void ensureDiscoverable(Context ctx) {
        if(Const.D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=  BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            ctx.startActivity(discoverableIntent);
        }
    }
    
    /** Get local Bluetooth adapter */
    public static BluetoothAdapter getBlueAdapter(){
    	if(mBluetoothAdapter == null)
    		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	return mBluetoothAdapter;
    }
    
    /** Connect to given device (only 1 device at the time can be connected
     * @param data an intent containing the Device's MAC address as string*/
    public static boolean connectToDevice(Intent data){
    	 // Get the device MAC address
        String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
        // Get the BLuetoothDevice object
        BluetoothDevice device=null ;
        try{
        	device = BlueConn.getBlueAdapter().getRemoteDevice(address);
        // Attempt to connect to the device
        }catch(IllegalArgumentException e){ 
        	Log.e(TAG,"@Main-getRemoteDevice(address)",e);
        	return false;
        }
       
        if(device!=null)
        	BluetoothService.mBlueService.connect(device);
        
        return true;
    }
    
    /** Initialize the BlueInterfaceService to perform bluetooth connections */
    public static void setupBT() {
        Log.d(TAG, "setupBT()");
        if (BluetoothService.mBlueService == null){
	        BluetoothService.mBlueService = new BluetoothService(mHandler);
        }
    }    
    
    /** Stop the Bluetooth services */
    public static void destroyBT(){
        if (BluetoothService.mBlueService != null){
        	BluetoothService.mBlueService.stop();
        }
    }
    
    /** Request bluetooth to be enabled. If it's not enabled, it's requested to the user
     * else we setup the BT services
     * @param activity The activity which will request the user to activate bluetooth*/
    public static void requestBluetoothEnabled(Activity activity){
		// setup the Bluetooth session
        if (getBlueAdapter().isEnabled()) { 
        	setupBT();        	
        } else {
            // If BT is not on, request that it be enabled.
        	if(Const.D) Log.e(TAG, " BLUETOOTH IS NOT ENABLED");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // setupBT() will then be called during onActivityResult
        }
	}
    
    /** Start listening for other bluetooth devices that want to connect with us
     * ONLY if the bluetooth service has ben Setup (setupBT()) and it hasn't been
     * started already s     */
    public static void requestBlueServiceStart(){
	    if (BluetoothService.mBlueService != null) {
	        // Only if the state is STATE_NONE, do we know that we haven't started already
	        if (BluetoothService.mBlueService.getState() == BluetoothService.STATE_NONE) {
	          // Start the Bluetooth chat services
	          BluetoothService.mBlueService.start();
	        }
	    }
    }
    
}

