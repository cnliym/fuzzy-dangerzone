package com.biziit.taxi.mapapi;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

import com.biziit.taxi.BuildConfig;
import com.google.gson.Gson;
 
 
public class ConverUtil
{//开线程 AsynceTask
    private static final String TAG = "ConverUtil";

	// 根据地址获取对应的经纬度
    public static double[] getLocationInfo(String address)
    {
		// 定义一个HttpClient，用于向指定地址发送请求
		HttpClient client = new DefaultHttpClient();
		// 向指定地址发送GET请求
		HttpGet httpGet = new HttpGet("http://maps.google."
				+ "com/maps/api/geocode/json?address=" + address
				+ "ka&sensor=false");
		StringBuilder sb = new StringBuilder();

		try {
			// 获取服务器的响应
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			// 获取服务器响应的输入流
			InputStream stream = entity.getContent();
			int b;
			// 循环读取服务器响应
			while ((b = stream.read()) != -1) {
				sb.append((char) b);
			}
//			if(BuildConfig.DEBUG) {
//				Log.d(TAG, sb.toString());
//			}
//			Gson gson = new Gson();  
//			GeoResultList testResult = gson.fromJson(sb.toString(), GeoResultList.class); 
			// 将服务器返回的字符串转换为JSONObject对象
			JSONObject jsonObject = new JSONObject(sb.toString());
			// 从JSONObject对象中取出代表位置的location属性
			JSONObject location = jsonObject.getJSONArray("results")
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location");
			// 获取经度信息
			double longitude = location.getDouble("lng");
			// 获取纬度信息
			double latitude = location.getDouble("lat");
			// 将经度、纬度信息组成double[]数组
			return new double[] { longitude, latitude };
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new double[] { 0, 0 };
	}
    
    // 根据经纬度获取对应的地址
    public static String getAddress(double longitude
        , double latitude)
    {
        // 定义一个HttpClient，用于向指定地址发送请求
        HttpClient client = new DefaultHttpClient();
        // 向指定地址发送GET请求
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/"
            + "geocode/json?latlng="
            + latitude + "," + longitude 
            + "&sensor=false&region=cn");
        StringBuilder sb = new StringBuilder();
        try
        {
            // 执行请求
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            // 获取服务器响应的字符串
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1)
            {
                sb.append((char) b);
            }
//            Gson gson = new Gson();  
//            GeoResultList testResult = gson.fromJson(responseData, GeoResultList.class);  
//            System.out.println(testResult);  
            // 把服务器相应的字符串转换为JSONObject
            JSONObject jsonObj = new JSONObject(sb.toString());
            // 解析出响应结果中的地址数据
            return jsonObj.getJSONArray("results").getJSONObject(0)
                .getString("formatted_address");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }   
}