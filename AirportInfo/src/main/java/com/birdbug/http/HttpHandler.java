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
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:28:18
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
					if ("虹桥(T1)".equals(entry.getValue())) {
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
	//	String data = "ok$$$[{\"ID\":\"6153\\",\"航向\":\"出发\",\"主航班号\":\"MU5111\",\"子航班号\":\"<div class='HangBan_list'><div class='List'><ul><li>CZ9244</li><li>CZ9244</li><li>CZ9244</li></ul></div></div>\",\"计划到达时间\":\"2016-08-21 15:10:00\",\"实际到达时间\":\"\",\"预计到达时间\":\"2016-08-21 14:59:00\",\"计划出发时间\":\"2016-08-21 13:00:00\",\"实际出发时间\":\"2016-08-21 13:13:00\",\"预计出发时间\":\"\",\"出发地\":\"上海 虹桥\",\"经停地\":\"\",\"目的地\":\"北京 首都\",\"候机楼\":\"虹桥(T2)\",\"改降机场\":\"\",\"登机门状态\":\"C\",\"状态\":\"实际出发13:13\",\"值机柜台\":\"C\",\"行李传送带\":\"\",\"值机区域1\":\"C11\",\"值机区域2\":\"D11\",\"值机区域3\":\"ISC-D\",\"ROWNUM_\":\"1\",\"航空公司\":\"中国东方航空公司\",\"显示计划时间\":\"13:00\",\"显示计划到达时间\":\"15:10\",\"时间显示\":\"2016-08-21\",\"具体时间\":\"13:00\",\"出发地代号\":\"SHA\",\"目的地代号\":\"PEK\"},{\"ID\":\"1671\",\"航向\":\"出发\",\"主航班号\":\"ZH9812\",\"子航班号\":\"<div class='HangBan_list'><div class='List'><ul><li>CA3341</li><li>CA3341</li><li>CA3341</li></ul></div></div>\",\"计划到达时间\":\"2016-08-21 15:40:00","实际到达时间":"","预计到达时间":"2016-08-21 15:46:00","计划出发时间":"2016-08-21 13:05:00","实际出发时间":"2016-08-21 13:45:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"深圳","候机楼":"浦东(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:45","值机柜台":"K","行李传送带":"","值机区域1":"2K28","值机区域2":"2K29","值机区域3":"ISK","ROWNUM_":"2","航空公司":"深圳航空公司","显示计划时间":"13:05","显示计划到达时间":"15:40","时间显示":"2016-08-21","具体时间":"13:05","出发地代号":"PVG","目的地代号":"SZX"},{"ID":"902","航向":"出发","主航班号":"FM9545","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>MU9545</li></ul></div></div>","计划到达时间":"2016-08-21 15:25:00","实际到达时间":"","预计到达时间":"2016-08-21 16:01:00","计划出发时间":"2016-08-21 13:05:00","实际出发时间":"2016-08-21 13:51:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"银川","候机楼":"浦东(T1)","改降机场":"","登机门状态":"C","状态":"实际出发13:51","值机柜台":"A-B","行李传送带":"","值机区域1":"1A01","值机区域2":"1B13","值机区域3":"ISA-B","ROWNUM_":"3","航空公司":"上海航空公司","显示计划时间":"13:05","显示计划到达时间":"15:25","时间显示":"2016-08-21","具体时间":"13:05","出发地代号":"PVG","目的地代号":"INC"},{"ID":"6264","航向":"出发","主航班号":"MU2247","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>CZ9373</li><li>CZ9373</li><li>CZ9373</li></ul></div></div>","计划到达时间":"2016-08-21 15:00:00","实际到达时间":"","预计到达时间":"2016-08-21 15:36:00","计划出发时间":"2016-08-21 13:10:00","实际出发时间":"2016-08-21 13:54:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"揭阳 潮汕","候机楼":"浦东(T1)","改降机场":"","登机门状态":"C","状态":"实际出发13:54","值机柜台":"A-B","行李传送带":"","值机区域1":"1A01","值机区域2":"1B13","值机区域3":"ISA-B","ROWNUM_":"4","航空公司":"中国东方航空公司","显示计划时间":"13:10","显示计划到达时间":"15:00","时间显示":"2016-08-21","具体时间":"13:10","出发地代号":"PVG","目的地代号":"SWA"},{"ID":"2049","航向":"出发","主航班号":"MU5551","子航班号":"","计划到达时间":"2016-08-21 14:50:00","实际到达时间":"","预计到达时间":"2016-08-21 17:10:00","计划出发时间":"2016-08-21 13:10:00","实际出发时间":"","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"临沂","候机楼":"浦东(T1)","改降机场":"","登机门状态":"","状态":"<span style='color:#FD9F02'>延误</span>","值机柜台":"A-B","行李传送带":"","值机区域1":"1A03","值机区域2":"1B26","值机区域3":"ISA-B","ROWNUM_":"5","航空公司":"中国东方航空公司","显示计划时间":"13:10","显示计划到达时间":"14:50","时间显示":"2016-08-21","具体时间":"13:10","出发地代号":"PVG","目的地代号":"LYI"},{"ID":"6206","航向":"出发","主航班号":"MU2156","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>JR2156</li><li>JR2156</li></ul></div></div>","计划到达时间":"2016-08-21 15:45:00","实际到达时间":"","预计到达时间":"2016-08-21 15:22:00","计划出发时间":"2016-08-21 13:10:00","实际出发时间":"2016-08-21 13:31:00","预计出发时间":"","出发地":"上海 虹桥","经停地":"西安 咸阳","目的地":"西宁","候机楼":"虹桥(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:31","值机柜台":"B","行李传送带":"","值机区域1":"B01","值机区域2":"C10","值机区域3":"ISB-C","ROWNUM_":"6","航空公司":"中国东方航空公司","显示计划时间":"13:10","显示计划到达时间":"15:45","时间显示":"2016-08-21","具体时间":"13:10","出发地代号":"SHA","目的地代号":"XNN"},{"ID":"1421","航向":"出发","主航班号":"9C8867","子航班号":"","计划到达时间":"2016-08-21 16:15:00","实际到达时间":"","预计到达时间":"2016-08-21 15:41:00","计划出发时间":"2016-08-21 13:10:00","实际出发时间":"2016-08-21 13:31:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"重庆","候机楼":"浦东(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:31","值机柜台":"M","行李传送带":"","值机区域1":"2M01","值机区域2":"2M11","值机区域3":"ISM","ROWNUM_":"7","航空公司":"中国春秋航空公司","显示计划时间":"13:10","显示计划到达时间":"16:15","时间显示":"2016-08-21","具体时间":"13:10","出发地代号":"PVG","目的地代号":"CKG"},{"ID":"6269","航向":"出发","主航班号":"MU2254","子航班号":"","计划到达时间":"2016-08-21 14:05:00","实际到达时间":"2016-08-21 14:09:00","预计到达时间":"2016-08-21 14:10:00","计划出发时间":"2016-08-21 13:10:00","实际出发时间":"2016-08-21 13:25:00","预计出发时间":"","出发地":"上海 虹桥","经停地":"盐城","目的地":"西安 咸阳","候机楼":"虹桥(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:25","值机柜台":"B","行李传送带":"","值机区域1":"B01","值机区域2":"C10","值机区域3":"ISB-C","ROWNUM_":"8","航空公司":"中国东方航空公司","显示计划时间":"13:10","显示计划到达时间":"14:05","时间显示":"2016-08-21","具体时间":"13:10","出发地代号":"SHA","目的地代号":"XIY"},{"ID":"5807","航向":"出发","主航班号":"MF8568","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>MU8568</li><li>MU8568</li><li>MU8568</li></ul></div></div>","计划到达时间":"2016-08-21 15:05:00","实际到达时间":"","预计到达时间":"2016-08-21 14:36:00","计划出发时间":"2016-08-21 13:15:00","实际出发时间":"2016-08-21 13:17:00","预计出发时间":"","出发地":"上海 虹桥","经停地":"","目的地":"厦门","候机楼":"虹桥(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:17","值机柜台":"A","行李传送带":"","值机区域1":"A02","值机区域2":"A21","值机区域3":"ISA","ROWNUM_":"9","航空公司":"厦门航空有限公司","显示计划时间":"13:15","显示计划到达时间":"15:05","时间显示":"2016-08-21","具体时间":"13:15","出发地代号":"SHA","目的地代号":"XMN"},{"ID":"1059","航向":"出发","主航班号":"CZ6506","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>MU3682</li><li>MF4116</li><li>MU3682</li><li>MF4116</li><li>MF4116</li><li>MU3682</li></ul></div></div>","计划到达时间":"2016-08-21 15:50:00","实际到达时间":"","预计到达时间":"2016-08-21 15:30:00","计划出发时间":"2016-08-21 13:15:00","实际出发时间":"2016-08-21 13:39:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"沈阳 桃仙","候机楼":"浦东(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:39","值机柜台":"L","行李传送带":"","值机区域1":"2L01","值机区域2":"2L34","值机区域3":"ISL","ROWNUM_":"10","航空公司":"中国南方航空公司","显示计划时间":"13:15","显示计划到达时间":"15:50","时间显示":"2016-08-21","具体时间":"13:15","出发地代号":"PVG","目的地代号":"SHE"},{"ID":"1897","航向":"出发","主航班号":"SC8744","子航班号":"","计划到达时间":"2016-08-21 14:55:00","实际到达时间":"2016-08-21 14:26:00","预计到达时间":"2016-08-21 14:28:00","计划出发时间":"2016-08-21 13:15:00","实际出发时间":"2016-08-21 13:21:00","预计出发时间":"","出发地":"上海 虹桥","经停地":"","目的地":"青岛","候机楼":"虹桥(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:21","值机柜台":"B","行李传送带":"","值机区域1":"B01","值机区域2":"B18","值机区域3":"ISB","ROWNUM_":"11","航空公司":"山东航空公司","显示计划时间":"13:15","显示计划到达时间":"14:55","时间显示":"2016-08-21","具体时间":"13:15","出发地代号":"SHA","目的地代号":"TAO"},{"ID":"1130","航向":"出发","主航班号":"CZ6755","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>MU4821</li><li>MU4821</li></ul></div></div>","计划到达时间":"2016-08-21 15:25:00","实际到达时间":"","预计到达时间":"2016-08-21 15:28:00","计划出发时间":"2016-08-21 13:15:00","实际出发时间":"2016-08-21 13:48:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"丹东","候机楼":"浦东(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:48","值机柜台":"L","行李传送带":"","值机区域1":"2L01","值机区域2":"2L34","值机区域3":"ISL","ROWNUM_":"12","航空公司":"中国南方航空公司","显示计划时间":"13:15","显示计划到达时间":"15:25","时间显示":"2016-08-21","具体时间":"13:15","出发地代号":"PVG","目的地代号":"DDG"},{"ID":"1112","航向":"出发","主航班号":"CZ6534","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>MF4140</li><li>MU4402</li><li>MF4140</li><li>MU4402</li><li>MF4140</li><li>MU4402</li></ul></div></div>","计划到达时间":"2016-08-21 15:15:00","实际到达时间":"","预计到达时间":"2016-08-21 14:48:00","计划出发时间":"2016-08-21 13:15:00","实际出发时间":"2016-08-21 13:24:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"大连","候机楼":"浦东(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:24","值机柜台":"L","行李传送带":"","值机区域1":"2L01","值机区域2":"2L34","值机区域3":"ISL","ROWNUM_":"13","航空公司":"中国南方航空公司","显示计划时间":"13:15","显示计划到达时间":"15:15","时间显示":"2016-08-21","具体时间":"13:15","出发地代号":"PVG","目的地代号":"DLC"},{"ID":"6321","航向":"出发","主航班号":"MU2508","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>HO1745</li><li>CZ9175</li><li>CZ9175</li><li>HO1745</li><li>CZ9175</li><li>HO1745</li></ul></div></div>","计划到达时间":"2016-08-21 15:20:00","实际到达时间":"","预计到达时间":"2016-08-21 15:01:00","计划出发时间":"2016-08-21 13:20:00","实际出发时间":"2016-08-21 13:35:00","预计出发时间":"","出发地":"上海 虹桥","经停地":"","目的地":"武汉 天河","候机楼":"虹桥(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:35","值机柜台":"B","行李传送带":"","值机区域1":"B01","值机区域2":"C10","值机区域3":"ISB-C","ROWNUM_":"14","航空公司":"中国东方航空公司","显示计划时间":"13:20","显示计划到达时间":"15:20","时间显示":"2016-08-21","具体时间":"13:20","出发地代号":"SHA","目的地代号":"WUH"},{"ID":"6337","航向":"出发","主航班号":"MU2692","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>KL4834</li><li>AF5206</li><li>AF5206</li><li>KL4834</li><li>AF5206</li><li>KL4834</li></ul></div></div>","计划到达时间":"2016-08-21 15:25:00","实际到达时间":"","预计到达时间":"2016-08-21 15:38:00","计划出发时间":"2016-08-21 13:20:00","实际出发时间":"2016-08-21 14:11:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"武汉 天河","候机楼":"浦东(T1)","改降机场":"","登机门状态":"C","状态":"实际出发14:11","值机柜台":"C","行李传送带":"","值机区域1":"1C01","值机区域2":"1C13","值机区域3":"ISC","ROWNUM_":"15","航空公司":"中国东方航空公司","显示计划时间":"13:20","显示计划到达时间":"15:25","时间显示":"2016-08-21","具体时间":"13:20","出发地代号":"PVG","目的地代号":"WUH"},{"ID":"5875","航向":"出发","主航班号":"HO1075","子航班号":"","计划到达时间":"2016-08-21 14:55:00","实际到达时间":"","预计到达时间":"2016-08-21 14:33:00","计划出发时间":"2016-08-21 13:20:00","实际出发时间":"2016-08-21 13:30:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"长白山","候机楼":"浦东(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:30","值机柜台":"J","行李传送带":"","值机区域1":"2J21","值机区域2":"2J34","值机区域3":"ISJ","ROWNUM_":"16","航空公司":"吉祥航空公司","显示计划时间":"13:20","显示计划到达时间":"14:55","时间显示":"2016-08-21","具体时间":"13:20","出发地代号":"PVG","目的地代号":"NBS"},{"ID":"2319","航向":"出发","主航班号":"MU5819","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>HO1783</li><li>CZ9582</li><li>CZ9582</li><li>HO1783</li><li>CZ9582</li><li>HO1783</li></ul></div></div>","计划到达时间":"2016-08-21 17:15:00","实际到达时间":"","预计到达时间":"2016-08-21 17:01:00","计划出发时间":"2016-08-21 13:25:00","实际出发时间":"2016-08-21 14:05:00","预计出发时间":"","出发地":"上海 虹桥","经停地":"","目的地":"丽江","候机楼":"虹桥(T2)","改降机场":"","登机门状态":"C","状态":"实际出发14:05","值机柜台":"B","行李传送带":"","值机区域1":"B01","值机区域2":"C10","值机区域3":"ISB-C","ROWNUM_":"17","航空公司":"中国东方航空公司","显示计划时间":"13:25","显示计划到达时间":"17:15","时间显示":"2016-08-21","具体时间":"13:25","出发地代号":"SHA","目的地代号":"LJG"},{"ID":"5855","航向":"出发","主航班号":"HO1017","子航班号":"","计划到达时间":"2016-08-21 15:35:00","实际到达时间":"","预计到达时间":"2016-08-21 15:45:00","计划出发时间":"2016-08-21 13:25:00","实际出发时间":"2016-08-21 13:53:00","预计出发时间":"","出发地":"上海 虹桥","经停地":"张家界 大庸","目的地":"昆明","候机楼":"虹桥(T2)","改降机场":"","登机门状态":"C","状态":"实际出发13:53","值机柜台":"E","行李传送带":"","值机区域1":"E01","值机区域2":"E18","值机区域3":"ISE","ROWNUM_":"18","航空公司":"吉祥航空公司","显示计划时间":"13:25","显示计划到达时间":"15:35","时间显示":"2016-08-21","具体时间":"13:25","出发地代号":"SHA","目的地代号":"KMG"},{"ID":"773","航向":"出发","主航班号":"CZ3359","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>MU3160</li><li>MF1213</li><li>MU3160</li><li>MF1213</li><li>MU3160</li><li>MF1213</li></ul></div></div>","计划到达时间":"2016-08-21 16:55:00","实际到达时间":"","预计到达时间":"2016-08-21 16:55:00","计划出发时间":"2016-08-21 13:30:00","实际出发时间":"2016-08-21 14:30:00","预计出发时间":"","出发地":"上海 浦东","经停地":"","目的地":"成都 双流","候机楼":"浦东(T2)","改降机场":"","登机门状态":"C","状态":"实际出发14:30","值机柜台":"L","行李传送带":"","值机区域1":"2L01","值机区域2":"2L34","值机区域3":"ISL","ROWNUM_":"19","航空公司":"中国南方航空公司","显示计划时间":"13:30","显示计划到达时间":"16:55","时间显示":"2016-08-21","具体时间":"13:30","出发地代号":"PVG","目的地代号":"CTU"},{"ID":"1594","航向":"出发","主航班号":"FM9313","子航班号":"<div class='HangBan_list'><div class='List'><ul><li>MU9313</li><li>MU9313</li><li>MU9313</li></ul></div></div>","计划到达时间":"2016-08-21 15:50:00","实际到达时间":"","预计到达时间":"2016-08-21 15:16:00","计划出发时间":"2016-08-21 13:30:00","实际出发时间":"","预计出发时间":"","出发地":"上海 虹桥","经停地":"","目的地":"广州 白云","候机楼":"虹桥(T2)","改降机场":"","登机门状态":"C","状态":" 结束登机","值机柜台":"C","行李传送带":"","值机区域1":"C11","值机区域2":"D11","值机区域3":"ISC-D","ROWNUM_":"20","航空公司":"上海航空公司","显示计划时间":"13:30","显示计划到达时间":"15:50","时间显示":"2016-08-21","具体时间":"13:30","出发地代号":"SHA","目的地代号":"CAN"}]$$$78";
	
		HttpHandler handler = new HttpHandler(new CrawlConfig());
		String url = "http://flights.ctrip.com/actualtime/depart-sha.p48/";
		
		System.out.println(handler.doGet(url));
		
	}
}
