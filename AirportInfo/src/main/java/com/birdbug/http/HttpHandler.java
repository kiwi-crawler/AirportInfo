package com.birdbug.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.birdbug.model.AirportInfo;

/**
* @author   ���� E-mail:pjm0008@163.com
* @date 2016��8��29�� ����10:28:18
* @version 1.0
* AirportInfo - com.birdbug.common
**/
public class HttpHandler {
	private static Logger LOG = Logger.getLogger(HttpHandler.class);

	private PoolingHttpClientConnectionManager connectionManager;
	private CloseableHttpClient httpClient;

	public HttpHandler(CrawlConfig config) {

		RequestConfig requestConfig = RequestConfig.custom().setExpectContinueEnabled(false)
				.setCookieSpec(CookieSpecs.DEFAULT).setRedirectsEnabled(false)
				.setSocketTimeout(config.getSocketTimeout()).setConnectTimeout(config.getConnectionTimeout()).build();

		RegistryBuilder<ConnectionSocketFactory> connRegistryBuilder = RegistryBuilder.create();
		connRegistryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);

		Registry<ConnectionSocketFactory> connRegistry = connRegistryBuilder.build();
		connectionManager = new PoolingHttpClientConnectionManager(connRegistry);
		connectionManager.setMaxTotal(config.getMaxTotalConnections());
		connectionManager.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.setDefaultRequestConfig(requestConfig);
		clientBuilder.setConnectionManager(connectionManager);
		clientBuilder.setUserAgent(config.getUserAgentString());
		clientBuilder.setDefaultHeaders(config.getDefaultHeaders());
		httpClient = clientBuilder.build();
	}

	public String doGet(String url) {
		String content = null;
		
		LOG.info("send get request:" + url);
		HttpGet httpGet = new HttpGet();
		try {
			httpGet.setURI(new URI(url));
			CloseableHttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 200 && statusCode <= 299) {
				content = EntityUtils.toString(response.getEntity());
			}
			LOG.info("To fetch request return :" + content);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("To send get request has exceptions, details:" + e.getMessage());
		}
		
		return content;
	}
	
	public String doPost(String url, Map<String, String> param) {
		String content = null;
		LOG.info("send post request:" + url);
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> postParams = new ArrayList<>();
		for (String key : param.keySet()) {
			postParams.add(new BasicNameValuePair(key, param.get(key)));
			LOG.debug("param-name:" + key + "param-value:" + param.get(key));
		}
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, "UTF-8");
			httpPost.setEntity(entity);
			CloseableHttpResponse response = httpClient.execute(httpPost);

			// Setting HttpStatus
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 200 && statusCode <= 299) {
				content = EntityUtils.toString(response.getEntity());
			}
			LOG.info("To fetch request return :" + content);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("To fetch request has exception!" + e.getMessage());
		}
		return content;
	}
	
	public List<AirportInfo> parseData(String data) {
		List<AirportInfo> list = new ArrayList<AirportInfo>();
		if (data != null && data.contains("[") && data.contains("]")) {
			String json = data.substring(data.indexOf("["), data.lastIndexOf("]")+1);
			JSONArray arr = JSON.parseArray(json);
			LOG.info("plan to parse data out " + arr.size() + " records!");
			for (int i=0 ; i<arr.size(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				AirportInfo ai = new AirportInfo();
				for (Entry<String, Object> entry : obj.entrySet()) {
					if ("����(T1)".equals(entry.getValue())) {
						continue;
					}
					ai.setFieldValue(entry.getKey(), entry.getValue());
				}
				list.add(ai);
			}
			LOG.info("actual parse data out " + list.size() + " records!");
		}
		return list;
	}
	
	public static void main(String args[]) {
		/*HttpHandler handler = new HttpHandler(new CrawlConfig());
		String url = "http://www.shanghaiairport.com/ajax/flights/search.aspx?action=getData";
		Map<String,String> params = new HashMap<String,String>();
		params.put("currentPage", "1");
		params.put("pageSize", "20");
		params.put("flightType", "1");
		params.put("direction", "1");
		params.put("airCities", "");
		params.put("airCities2", "");
		params.put("airCompanies", "");
		params.put("timeDays", "0");
		params.put("timeSpan", "13:00-15:00");
		params.put("flightNum", "");
		System.out.println(handler.doPost(url, params));*/
	//	String data = "ok$$$[{\"ID\":\"6153\\",\"����\":\"����\",\"�������\":\"MU5111\",\"�Ӻ����\":\"<div class='HangBan_list'><div class='List'><ul><li>CZ9244</li><li>CZ9244</li><li>CZ9244</li></ul></div></div>\",\"�ƻ�����ʱ��\":\"2016-08-21 15:10:00\",\"ʵ�ʵ���ʱ��\":\"\",\"Ԥ�Ƶ���ʱ��\":\"2016-08-21 14:59:00\",\"�ƻ�����ʱ��\":\"2016-08-21 13:00:00\",\"ʵ�ʳ���ʱ��\":\"2016-08-21 13:13:00\",\"Ԥ�Ƴ���ʱ��\":\"\",\"������\":\"�Ϻ� ����\",\"��ͣ��\":\"\",\"Ŀ�ĵ�\":\"���� �׶�\",\"���¥\":\"����(T2)\",\"�Ľ�����\":\"\",\"�ǻ���״̬\":\"C\",\"״̬\":\"ʵ�ʳ���13:13\",\"ֵ����̨\":\"C\",\"����ʹ�\":\"\",\"ֵ������1\":\"C11\",\"ֵ������2\":\"D11\",\"ֵ������3\":\"ISC-D\",\"ROWNUM_\":\"1\",\"���չ�˾\":\"�й��������չ�˾\",\"��ʾ�ƻ�ʱ��\":\"13:00\",\"��ʾ�ƻ�����ʱ��\":\"15:10\",\"ʱ����ʾ\":\"2016-08-21\",\"����ʱ��\":\"13:00\",\"�����ش���\":\"SHA\",\"Ŀ�ĵش���\":\"PEK\"},{\"ID\":\"1671\",\"����\":\"����\",\"�������\":\"ZH9812\",\"�Ӻ����\":\"<div class='HangBan_list'><div class='List'><ul><li>CA3341</li><li>CA3341</li><li>CA3341</li></ul></div></div>\",\"�ƻ�����ʱ��\":\"2016-08-21 15:40:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:46:00","�ƻ�����ʱ��":"2016-08-21 13:05:00","ʵ�ʳ���ʱ��":"2016-08-21 13:45:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"����","���¥":"�ֶ�(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:45","ֵ����̨":"K","����ʹ�":"","ֵ������1":"2K28","ֵ������2":"2K29","ֵ������3":"ISK","ROWNUM_":"2","���չ�˾":"���ں��չ�˾","��ʾ�ƻ�ʱ��":"13:05","��ʾ�ƻ�����ʱ��":"15:40","ʱ����ʾ":"2016-08-21","����ʱ��":"13:05","�����ش���":"PVG","Ŀ�ĵش���":"SZX"},{"ID":"902","����":"����","�������":"FM9545","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>MU9545</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:25:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 16:01:00","�ƻ�����ʱ��":"2016-08-21 13:05:00","ʵ�ʳ���ʱ��":"2016-08-21 13:51:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"����","���¥":"�ֶ�(T1)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:51","ֵ����̨":"A-B","����ʹ�":"","ֵ������1":"1A01","ֵ������2":"1B13","ֵ������3":"ISA-B","ROWNUM_":"3","���չ�˾":"�Ϻ����չ�˾","��ʾ�ƻ�ʱ��":"13:05","��ʾ�ƻ�����ʱ��":"15:25","ʱ����ʾ":"2016-08-21","����ʱ��":"13:05","�����ش���":"PVG","Ŀ�ĵش���":"INC"},{"ID":"6264","����":"����","�������":"MU2247","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>CZ9373</li><li>CZ9373</li><li>CZ9373</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:00:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:36:00","�ƻ�����ʱ��":"2016-08-21 13:10:00","ʵ�ʳ���ʱ��":"2016-08-21 13:54:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"���� ����","���¥":"�ֶ�(T1)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:54","ֵ����̨":"A-B","����ʹ�":"","ֵ������1":"1A01","ֵ������2":"1B13","ֵ������3":"ISA-B","ROWNUM_":"4","���չ�˾":"�й��������չ�˾","��ʾ�ƻ�ʱ��":"13:10","��ʾ�ƻ�����ʱ��":"15:00","ʱ����ʾ":"2016-08-21","����ʱ��":"13:10","�����ش���":"PVG","Ŀ�ĵش���":"SWA"},{"ID":"2049","����":"����","�������":"MU5551","�Ӻ����":"","�ƻ�����ʱ��":"2016-08-21 14:50:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 17:10:00","�ƻ�����ʱ��":"2016-08-21 13:10:00","ʵ�ʳ���ʱ��":"","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"����","���¥":"�ֶ�(T1)","�Ľ�����":"","�ǻ���״̬":"","״̬":"<span style='color:#FD9F02'>����</span>","ֵ����̨":"A-B","����ʹ�":"","ֵ������1":"1A03","ֵ������2":"1B26","ֵ������3":"ISA-B","ROWNUM_":"5","���չ�˾":"�й��������չ�˾","��ʾ�ƻ�ʱ��":"13:10","��ʾ�ƻ�����ʱ��":"14:50","ʱ����ʾ":"2016-08-21","����ʱ��":"13:10","�����ش���":"PVG","Ŀ�ĵش���":"LYI"},{"ID":"6206","����":"����","�������":"MU2156","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>JR2156</li><li>JR2156</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:45:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:22:00","�ƻ�����ʱ��":"2016-08-21 13:10:00","ʵ�ʳ���ʱ��":"2016-08-21 13:31:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� ����","��ͣ��":"���� ����","Ŀ�ĵ�":"����","���¥":"����(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:31","ֵ����̨":"B","����ʹ�":"","ֵ������1":"B01","ֵ������2":"C10","ֵ������3":"ISB-C","ROWNUM_":"6","���չ�˾":"�й��������չ�˾","��ʾ�ƻ�ʱ��":"13:10","��ʾ�ƻ�����ʱ��":"15:45","ʱ����ʾ":"2016-08-21","����ʱ��":"13:10","�����ش���":"SHA","Ŀ�ĵش���":"XNN"},{"ID":"1421","����":"����","�������":"9C8867","�Ӻ����":"","�ƻ�����ʱ��":"2016-08-21 16:15:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:41:00","�ƻ�����ʱ��":"2016-08-21 13:10:00","ʵ�ʳ���ʱ��":"2016-08-21 13:31:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"����","���¥":"�ֶ�(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:31","ֵ����̨":"M","����ʹ�":"","ֵ������1":"2M01","ֵ������2":"2M11","ֵ������3":"ISM","ROWNUM_":"7","���չ�˾":"�й����ﺽ�չ�˾","��ʾ�ƻ�ʱ��":"13:10","��ʾ�ƻ�����ʱ��":"16:15","ʱ����ʾ":"2016-08-21","����ʱ��":"13:10","�����ش���":"PVG","Ŀ�ĵش���":"CKG"},{"ID":"6269","����":"����","�������":"MU2254","�Ӻ����":"","�ƻ�����ʱ��":"2016-08-21 14:05:00","ʵ�ʵ���ʱ��":"2016-08-21 14:09:00","Ԥ�Ƶ���ʱ��":"2016-08-21 14:10:00","�ƻ�����ʱ��":"2016-08-21 13:10:00","ʵ�ʳ���ʱ��":"2016-08-21 13:25:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� ����","��ͣ��":"�γ�","Ŀ�ĵ�":"���� ����","���¥":"����(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:25","ֵ����̨":"B","����ʹ�":"","ֵ������1":"B01","ֵ������2":"C10","ֵ������3":"ISB-C","ROWNUM_":"8","���չ�˾":"�й��������չ�˾","��ʾ�ƻ�ʱ��":"13:10","��ʾ�ƻ�����ʱ��":"14:05","ʱ����ʾ":"2016-08-21","����ʱ��":"13:10","�����ش���":"SHA","Ŀ�ĵش���":"XIY"},{"ID":"5807","����":"����","�������":"MF8568","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>MU8568</li><li>MU8568</li><li>MU8568</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:05:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 14:36:00","�ƻ�����ʱ��":"2016-08-21 13:15:00","ʵ�ʳ���ʱ��":"2016-08-21 13:17:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� ����","��ͣ��":"","Ŀ�ĵ�":"����","���¥":"����(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:17","ֵ����̨":"A","����ʹ�":"","ֵ������1":"A02","ֵ������2":"A21","ֵ������3":"ISA","ROWNUM_":"9","���չ�˾":"���ź������޹�˾","��ʾ�ƻ�ʱ��":"13:15","��ʾ�ƻ�����ʱ��":"15:05","ʱ����ʾ":"2016-08-21","����ʱ��":"13:15","�����ش���":"SHA","Ŀ�ĵش���":"XMN"},{"ID":"1059","����":"����","�������":"CZ6506","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>MU3682</li><li>MF4116</li><li>MU3682</li><li>MF4116</li><li>MF4116</li><li>MU3682</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:50:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:30:00","�ƻ�����ʱ��":"2016-08-21 13:15:00","ʵ�ʳ���ʱ��":"2016-08-21 13:39:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"���� ����","���¥":"�ֶ�(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:39","ֵ����̨":"L","����ʹ�":"","ֵ������1":"2L01","ֵ������2":"2L34","ֵ������3":"ISL","ROWNUM_":"10","���չ�˾":"�й��Ϸ����չ�˾","��ʾ�ƻ�ʱ��":"13:15","��ʾ�ƻ�����ʱ��":"15:50","ʱ����ʾ":"2016-08-21","����ʱ��":"13:15","�����ش���":"PVG","Ŀ�ĵش���":"SHE"},{"ID":"1897","����":"����","�������":"SC8744","�Ӻ����":"","�ƻ�����ʱ��":"2016-08-21 14:55:00","ʵ�ʵ���ʱ��":"2016-08-21 14:26:00","Ԥ�Ƶ���ʱ��":"2016-08-21 14:28:00","�ƻ�����ʱ��":"2016-08-21 13:15:00","ʵ�ʳ���ʱ��":"2016-08-21 13:21:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� ����","��ͣ��":"","Ŀ�ĵ�":"�ൺ","���¥":"����(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:21","ֵ����̨":"B","����ʹ�":"","ֵ������1":"B01","ֵ������2":"B18","ֵ������3":"ISB","ROWNUM_":"11","���չ�˾":"ɽ�����չ�˾","��ʾ�ƻ�ʱ��":"13:15","��ʾ�ƻ�����ʱ��":"14:55","ʱ����ʾ":"2016-08-21","����ʱ��":"13:15","�����ش���":"SHA","Ŀ�ĵش���":"TAO"},{"ID":"1130","����":"����","�������":"CZ6755","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>MU4821</li><li>MU4821</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:25:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:28:00","�ƻ�����ʱ��":"2016-08-21 13:15:00","ʵ�ʳ���ʱ��":"2016-08-21 13:48:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"����","���¥":"�ֶ�(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:48","ֵ����̨":"L","����ʹ�":"","ֵ������1":"2L01","ֵ������2":"2L34","ֵ������3":"ISL","ROWNUM_":"12","���չ�˾":"�й��Ϸ����չ�˾","��ʾ�ƻ�ʱ��":"13:15","��ʾ�ƻ�����ʱ��":"15:25","ʱ����ʾ":"2016-08-21","����ʱ��":"13:15","�����ش���":"PVG","Ŀ�ĵش���":"DDG"},{"ID":"1112","����":"����","�������":"CZ6534","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>MF4140</li><li>MU4402</li><li>MF4140</li><li>MU4402</li><li>MF4140</li><li>MU4402</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:15:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 14:48:00","�ƻ�����ʱ��":"2016-08-21 13:15:00","ʵ�ʳ���ʱ��":"2016-08-21 13:24:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"����","���¥":"�ֶ�(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:24","ֵ����̨":"L","����ʹ�":"","ֵ������1":"2L01","ֵ������2":"2L34","ֵ������3":"ISL","ROWNUM_":"13","���չ�˾":"�й��Ϸ����չ�˾","��ʾ�ƻ�ʱ��":"13:15","��ʾ�ƻ�����ʱ��":"15:15","ʱ����ʾ":"2016-08-21","����ʱ��":"13:15","�����ش���":"PVG","Ŀ�ĵش���":"DLC"},{"ID":"6321","����":"����","�������":"MU2508","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>HO1745</li><li>CZ9175</li><li>CZ9175</li><li>HO1745</li><li>CZ9175</li><li>HO1745</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:20:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:01:00","�ƻ�����ʱ��":"2016-08-21 13:20:00","ʵ�ʳ���ʱ��":"2016-08-21 13:35:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� ����","��ͣ��":"","Ŀ�ĵ�":"�人 ���","���¥":"����(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:35","ֵ����̨":"B","����ʹ�":"","ֵ������1":"B01","ֵ������2":"C10","ֵ������3":"ISB-C","ROWNUM_":"14","���չ�˾":"�й��������չ�˾","��ʾ�ƻ�ʱ��":"13:20","��ʾ�ƻ�����ʱ��":"15:20","ʱ����ʾ":"2016-08-21","����ʱ��":"13:20","�����ش���":"SHA","Ŀ�ĵش���":"WUH"},{"ID":"6337","����":"����","�������":"MU2692","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>KL4834</li><li>AF5206</li><li>AF5206</li><li>KL4834</li><li>AF5206</li><li>KL4834</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:25:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:38:00","�ƻ�����ʱ��":"2016-08-21 13:20:00","ʵ�ʳ���ʱ��":"2016-08-21 14:11:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"�人 ���","���¥":"�ֶ�(T1)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���14:11","ֵ����̨":"C","����ʹ�":"","ֵ������1":"1C01","ֵ������2":"1C13","ֵ������3":"ISC","ROWNUM_":"15","���չ�˾":"�й��������չ�˾","��ʾ�ƻ�ʱ��":"13:20","��ʾ�ƻ�����ʱ��":"15:25","ʱ����ʾ":"2016-08-21","����ʱ��":"13:20","�����ش���":"PVG","Ŀ�ĵش���":"WUH"},{"ID":"5875","����":"����","�������":"HO1075","�Ӻ����":"","�ƻ�����ʱ��":"2016-08-21 14:55:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 14:33:00","�ƻ�����ʱ��":"2016-08-21 13:20:00","ʵ�ʳ���ʱ��":"2016-08-21 13:30:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"����ɽ","���¥":"�ֶ�(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:30","ֵ����̨":"J","����ʹ�":"","ֵ������1":"2J21","ֵ������2":"2J34","ֵ������3":"ISJ","ROWNUM_":"16","���չ�˾":"���麽�չ�˾","��ʾ�ƻ�ʱ��":"13:20","��ʾ�ƻ�����ʱ��":"14:55","ʱ����ʾ":"2016-08-21","����ʱ��":"13:20","�����ش���":"PVG","Ŀ�ĵش���":"NBS"},{"ID":"2319","����":"����","�������":"MU5819","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>HO1783</li><li>CZ9582</li><li>CZ9582</li><li>HO1783</li><li>CZ9582</li><li>HO1783</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 17:15:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 17:01:00","�ƻ�����ʱ��":"2016-08-21 13:25:00","ʵ�ʳ���ʱ��":"2016-08-21 14:05:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� ����","��ͣ��":"","Ŀ�ĵ�":"����","���¥":"����(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���14:05","ֵ����̨":"B","����ʹ�":"","ֵ������1":"B01","ֵ������2":"C10","ֵ������3":"ISB-C","ROWNUM_":"17","���չ�˾":"�й��������չ�˾","��ʾ�ƻ�ʱ��":"13:25","��ʾ�ƻ�����ʱ��":"17:15","ʱ����ʾ":"2016-08-21","����ʱ��":"13:25","�����ش���":"SHA","Ŀ�ĵش���":"LJG"},{"ID":"5855","����":"����","�������":"HO1017","�Ӻ����":"","�ƻ�����ʱ��":"2016-08-21 15:35:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:45:00","�ƻ�����ʱ��":"2016-08-21 13:25:00","ʵ�ʳ���ʱ��":"2016-08-21 13:53:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� ����","��ͣ��":"�żҽ� ��ӹ","Ŀ�ĵ�":"����","���¥":"����(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���13:53","ֵ����̨":"E","����ʹ�":"","ֵ������1":"E01","ֵ������2":"E18","ֵ������3":"ISE","ROWNUM_":"18","���չ�˾":"���麽�չ�˾","��ʾ�ƻ�ʱ��":"13:25","��ʾ�ƻ�����ʱ��":"15:35","ʱ����ʾ":"2016-08-21","����ʱ��":"13:25","�����ش���":"SHA","Ŀ�ĵش���":"KMG"},{"ID":"773","����":"����","�������":"CZ3359","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>MU3160</li><li>MF1213</li><li>MU3160</li><li>MF1213</li><li>MU3160</li><li>MF1213</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 16:55:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 16:55:00","�ƻ�����ʱ��":"2016-08-21 13:30:00","ʵ�ʳ���ʱ��":"2016-08-21 14:30:00","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� �ֶ�","��ͣ��":"","Ŀ�ĵ�":"�ɶ� ˫��","���¥":"�ֶ�(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":"ʵ�ʳ���14:30","ֵ����̨":"L","����ʹ�":"","ֵ������1":"2L01","ֵ������2":"2L34","ֵ������3":"ISL","ROWNUM_":"19","���չ�˾":"�й��Ϸ����չ�˾","��ʾ�ƻ�ʱ��":"13:30","��ʾ�ƻ�����ʱ��":"16:55","ʱ����ʾ":"2016-08-21","����ʱ��":"13:30","�����ش���":"PVG","Ŀ�ĵش���":"CTU"},{"ID":"1594","����":"����","�������":"FM9313","�Ӻ����":"<div class='HangBan_list'><div class='List'><ul><li>MU9313</li><li>MU9313</li><li>MU9313</li></ul></div></div>","�ƻ�����ʱ��":"2016-08-21 15:50:00","ʵ�ʵ���ʱ��":"","Ԥ�Ƶ���ʱ��":"2016-08-21 15:16:00","�ƻ�����ʱ��":"2016-08-21 13:30:00","ʵ�ʳ���ʱ��":"","Ԥ�Ƴ���ʱ��":"","������":"�Ϻ� ����","��ͣ��":"","Ŀ�ĵ�":"���� ����","���¥":"����(T2)","�Ľ�����":"","�ǻ���״̬":"C","״̬":" �����ǻ�","ֵ����̨":"C","����ʹ�":"","ֵ������1":"C11","ֵ������2":"D11","ֵ������3":"ISC-D","ROWNUM_":"20","���չ�˾":"�Ϻ����չ�˾","��ʾ�ƻ�ʱ��":"13:30","��ʾ�ƻ�����ʱ��":"15:50","ʱ����ʾ":"2016-08-21","����ʱ��":"13:30","�����ش���":"SHA","Ŀ�ĵش���":"CAN"}]$$$78";
	
		HttpHandler handler = new HttpHandler(new CrawlConfig());
		String url = "http://flights.ctrip.com/actualtime/depart-sha.p48/";
		
		System.out.println(handler.doGet(url));
		
	}
}
