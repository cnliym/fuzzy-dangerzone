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
{//���߳� AsynceTask
    private static final String TAG = "ConverUtil";

	// ���ݵ�ַ��ȡ��Ӧ�ľ�γ��
    public static double[] getLocationInfo(String address)
    {
		// ����һ��HttpClient��������ָ����ַ��������
		HttpClient client = new DefaultHttpClient();
		// ��ָ����ַ����GET����
		HttpGet httpGet = new HttpGet("http://maps.google."
				+ "com/maps/api/geocode/json?address=" + address
				+ "ka&sensor=false");
		StringBuilder sb = new StringBuilder();

		try {
			// ��ȡ����������Ӧ
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			// ��ȡ��������Ӧ��������
			InputStream stream = entity.getContent();
			int b;
			// ѭ����ȡ��������Ӧ
			while ((b = stream.read()) != -1) {
				sb.append((char) b);
			}
//			if(BuildConfig.DEBUG) {
//				Log.d(TAG, sb.toString());
//			}
//			Gson gson = new Gson();  
//			GeoResultList testResult = gson.fromJson(sb.toString(), GeoResultList.class); 
			// �����������ص��ַ���ת��ΪJSONObject����
			JSONObject jsonObject = new JSONObject(sb.toString());
			// ��JSONObject������ȡ������λ�õ�location����
			JSONObject location = jsonObject.getJSONArray("results")
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location");
			// ��ȡ������Ϣ
			double longitude = location.getDouble("lng");
			// ��ȡγ����Ϣ
			double latitude = location.getDouble("lat");
			// �����ȡ�γ����Ϣ���double[]����
			return new double[] { longitude, latitude };
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new double[] { 0, 0 };
	}
    
    // ���ݾ�γ�Ȼ�ȡ��Ӧ�ĵ�ַ
    public static String getAddress(double longitude
        , double latitude)
    {
        // ����һ��HttpClient��������ָ����ַ��������
        HttpClient client = new DefaultHttpClient();
        // ��ָ����ַ����GET����
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/"
            + "geocode/json?latlng="
            + latitude + "," + longitude 
            + "&sensor=false&region=cn");
        StringBuilder sb = new StringBuilder();
        try
        {
            // ִ������
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            // ��ȡ��������Ӧ���ַ���
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1)
            {
                sb.append((char) b);
            }
//            Gson gson = new Gson();  
//            GeoResultList testResult = gson.fromJson(responseData, GeoResultList.class);  
//            System.out.println(testResult);  
            // �ѷ�������Ӧ���ַ���ת��ΪJSONObject
            JSONObject jsonObj = new JSONObject(sb.toString());
            // ��������Ӧ����еĵ�ַ����
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