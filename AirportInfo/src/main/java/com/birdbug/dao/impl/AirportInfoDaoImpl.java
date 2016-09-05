package com.birdbug.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.birdbug.common.Const;
import com.birdbug.common.PlatformCfg;
import com.birdbug.dao.AirportInfoDao;
import com.birdbug.db.HttpCache;
import com.birdbug.http.CrawlConfig;
import com.birdbug.http.HttpHandler;
import com.birdbug.model.AirportInfo;
import com.birdbug.util.Util;

/**
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:31:38
* @version AirportInfo-1.0
**/
@Component("airportInfoDao")
public class AirportInfoDaoImpl implements AirportInfoDao {

	private static final Logger LOG = Logger.getLogger(AirportInfoDaoImpl.class); 

	@Resource
	private HttpCache httpCache;
	
	@Override
	public List<AirportInfo> getByPage(int pageNo) {
		int limit = Integer.parseInt(PlatformCfg.getContextProperty(Const.VIEW_PAGE_SIZE));
		int start = pageNo * limit;
		LOG.info("pageNo:" + pageNo + " start:" + start + " limit:" + limit);
		List<AirportInfo> list = httpCache.getByIndex(start, limit);
		LOG.info("From httpCache get:" + list.size() + " records!");
		return list;
	}

	@Override
	public List<AirportInfo> getAll() {
		String beginTime = Util.getTime(Integer.parseInt(PlatformCfg.getContextProperty(Const.BEFORE_MIN_NUM)));
		String endTime = Util.getTime(Integer.parseInt(PlatformCfg.getContextProperty(Const.AFTER_MIN_NUM)));
		List<AirportInfo> list = new ArrayList<AirportInfo>();
		if (Util.compareTime(beginTime, endTime) == -1) {
			String timespan1 = beginTime + "-23:59";
			LOG.info("!!!critical time!!!\nLoad up:" + timespan1);
			list = fetchAirInfo(Const.TIMEDAYS_TODAY, timespan1);
			String timespan2 = "00:00-" + endTime;
			LOG.info("!!!critical time!!!\nLoad down:" + timespan2);
			list.addAll(fetchAirInfo(Const.TIMEDAYS_TOMORROW, timespan2));
		} else {
			String timespan = beginTime + "-" + endTime;
			LOG.info("beginTime:" + beginTime + "\tendTime:" + endTime);
			list = fetchAirInfo(PlatformCfg.getContextProperty(Const.TIMEDAYS), timespan);
		}
		LOG.info("Total fetch " + list.size() + " records!");
		return list;
	}
	
	private List<AirportInfo> fetchAirInfo(String timeDays, String timespan) {
		List<AirportInfo> list = new ArrayList<AirportInfo>();
		HttpHandler handler = new HttpHandler(new CrawlConfig());
		String url = PlatformCfg.getContextProperty(Const.AIR_INFO_URL);
		Map<String,String> params = new HashMap<String,String>();
		params.put(Const.CURRENTPAGE, PlatformCfg.getContextProperty(Const.CURRENTPAGE));
		params.put(Const.PAGESIZE, PlatformCfg.getContextProperty(Const.PAGESIZE));
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
