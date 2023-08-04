package com.lelezu.app.xianzhuan.wxapi;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Util {
	
	private static final String TAG = "SDK_Sample.Util";
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	public static byte[] getHtmlByteArray(final String url) {
		URL htmlUrl = null;
		InputStream inStream = null;
		try {
			htmlUrl = new URL(url);
			URLConnection connection = htmlUrl.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			int responseCode = httpConnection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK){
				inStream = httpConnection.getInputStream();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = inputStreamToByte(inStream);

		return data;
	}
	public static byte[] inputStreamToByte(InputStream is) {
		try{
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			byte imgdata[] = bytestream.toByteArray();
			bytestream.close();
			return imgdata;
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}
}
