package com.duke.hangman.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.duke.hangman.util.Constant;

import android.util.Log;

public class NetHttpPost {
	
	public static void excute(final JSONObject request, final NetHttpResponse response){
//		Log.d("hangman", "duke excute");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
//				Log.d("hangman", "duke run");
				String result = "";
				HttpClient httpclient = new DefaultHttpClient();	
				HttpPost httpRequest = new HttpPost(Constant.REQUEST_URL);
				
				httpRequest.addHeader("Content-Type", "application/json");
				try {
					httpRequest.setEntity(new StringEntity(request.toString())); 
					HttpResponse httpResponse = httpclient.execute(httpRequest);	
					Log.d("hangman","httpResponse:" + httpResponse.getStatusLine().getStatusCode());
					
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	
						result += EntityUtils.toString(httpResponse.getEntity());	
						Log.d("IAP","11" );
					}else{
						JSONObject obj = new JSONObject();
						obj.put("error", true);
						result = obj.toString();
					}
					response.onsuccess(result);
					Log.d("hangman","result:" + result);
					
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();	
				} catch (ClientProtocolException e) {
					e.printStackTrace();	
				} catch (IOException e) {
					e.printStackTrace();	
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}).start();

	}
	
	public interface NetHttpResponse{
		public void onsuccess(String resultStr);
	}
}
