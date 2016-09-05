package com.birdbug.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.birdbug.common.Const;
import com.birdbug.common.PlatformCfg;
import com.birdbug.http.CrawlConfig;
import com.birdbug.http.HttpHandler;
import com.birdbug.model.AirportInfo;
import com.birdbug.util.Util;


/**
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:31:54
* @version AirportInfo-1.0
**/
@Component("httpCache")
@Scope("singleton")
public class HttpCache {

	private static final Logger LOG = Logger.getLogger(HttpCache.class);
	private static List<AirportInfo> dataPool = Collections.synchronizedList(new ArrayList<AirportInfo>());
	private static final int CAPACITY = Integer.parseInt(PlatformCfg.getContextProperty(Const.DATA_POOL_CAPACITY));
	
	public HttpCache() {
		refreshDataPool();
	}
	
	private void refreshDataPool() {
		String nowTime = Util.getTime(0);
		String beginTime = Util.getTime(Integer.parseInt(PlatformCfg.getContextProperty(Const.BEFORE_MIN_NUM)));
		String endTime = Const.FINAL_AFTER_MIN_NUM;
		String timespan = beginTime + "-" + endTime;
		int yesterday=-1,today=-1,tomorrow=-1;
		int res = Util.compareTime(beginTime, nowTime);
		LOG.info("nowTime:" + nowTime + " beginTime:" + beginTime + " compare result:" + res);
		if (res < 0) {
			List<AirportInfo> list0 = fetchAirInfo(Const.TIMEDAYS_YESTERDAY, timespan, CAPACITY);
			yesterday = list0.size();
			dataPool.clear();
			dataPool.addAll(list0);
			int remain = CAPACITY - dataPool.size();
			if (remain > 0) {
				beginTime = Const.FINAL_BEFORE_MIN_NUM;
				endTime = Const.FINAL_AFTER_MIN_NUM;
				timespan = beginTime + "-" + endTime;
				List<AirportInfo> list1 = fetchAirInfo(Const.TIMEDAYS_TODAY, timespan, remain);
				today = list1.size();
				dataPool.addAll(list1);
			}
			
		} else {
			List<AirportInfo> list1 = fetchAirInfo(Const.TIMEDAYS_TODAY, timespan, CAPACITY);
			today = list1.size();
			dataPool.clear();
			dataPool.addAll(list1);
			int remain = CAPACITY - dataPool.size();
			if (remain > 0) {
				beginTime = Const.FINAL_BEFORE_MIN_NUM;
				endTime = Const.FINAL_AFTER_MIN_NUM;
				timespan = beginTime + "-" + endTime;
				List<AirportInfo> list2 = fetchAirInfo(Const.TIMEDAYS_TOMORROW, timespan, remain);
				tomorrow = list2.size();
				dataPool.addAll(list2);
			}
		}
		
		LOG.info("Data pool size: " + dataPool.size() + "; yesterday:" + yesterday + 
				"; today:" + today + "; tomorrow:" + tomorrow + ";");
	}
	
	public List<AirportInfo> getByIndex(int from, int limit) {
		List<AirportInfo> list = new ArrayList<AirportInfo>();
		LOG.info("getByIndex: from=" + from + " limit=" + limit);
		synchronized(dataPool) {			
			int currSize = dataPool.size();
			if (currSize > 0) {
				int fromindex = from;
				int endindex = (fromindex + limit) > currSize ? (currSize - 1) : fromindex + limit;
				list = dataPool.subList(fromindex, endindex);
			}
			LOG.info("Current dataPool'size:" + currSize + " Current page'size:" + list.size());
		}
		return list;
	}
	
	public List<AirportInfo> getAll() {
		return dataPool;
	}
	
	private List<AirportInfo> fetchAirInfo(String timeDays, String timespan, int limit) {
		LOG.info("fetchAirInfo from:" + timespan);
		List<AirportInfo> list = new ArrayList<AirportInfo>();
		HttpHandler handler = new HttpHandler(new CrawlConfig());
		String url = PlatformCfg.getContextProperty(Const.AIR_INFO_URL);
		Map<String,String> params = new HashMap<String,String>();
		params.put(Const.CURRENTPAGE, PlatformCfg.getContextProperty(Const.CURRENTPAGE));
		params.put(Const.PAGESIZE, "" + limit);
		params.put(Const.FLIGHTTYPE, PlatformCfg.getContextProperty(Const.FLIGHTTYPE));
		params.put(Const.DIRECTION, PlatformCfg.getContextProperty(Const.DIRECTION));
		params.put(Const.AIRCITIES, PlatformCfg.getContextProperty(Const.AIRCITIES));
		params.put(Const.AIRCITIES2, PlatformCfg.getContextProperty(Const.AIRCITIES2));
		params.put(Const.AIRCOMPANIES, PlatformCfg.getContextProperty(Const.AIRCOMPANIES));
		params.put(Const.TIMEDAYS, timeDays);
		params.put(Const.TIMESPAN, timespan);
		params.put(Const.FLIGHTNUM, PlatformCfg.getContextProperty(Const.FLIGHTNUM));
		String data = handler.doPost(url, params);
		list = handler.parseData(data);
		return list;
	}
	
}
