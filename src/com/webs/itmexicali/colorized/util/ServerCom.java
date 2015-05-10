package com.webs.itmexicali.colorized.util;

//http://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests

import java.util.Scanner;
import java.net.*;
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

public class ServerCom extends server.ServerCom{

	private static ServerCom instance;
	
	private ServerCom(){
		
	}
	
	public static ServerCom shared(){
		if(instance == null){
			instance = new ServerCom();
		}
		return instance;
	}



@Override
public boolean isNetworkAvailable() {
	// TODO Auto-generated method stub
	return false;
}
}