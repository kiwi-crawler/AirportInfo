package com.birdbug.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.birdbug.common.Const;
import com.birdbug.common.PlatformCfg;
import com.birdbug.model.AirportInfo;
import com.birdbug.service.AirportInfoService;
import com.birdbug.util.ExtJSReturn;
import com.birdbug.util.Util;


/**
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:31:28
* @version AirportInfo-1.0
**/
@Controller
@RequestMapping("/AirportInfo") 
public class AirportInfoController {
	private static final Logger LOG = Logger.getLogger(AirportInfoController.class);
	@Autowired
	private AirportInfoService airportInfoService;

	 @RequestMapping("/getAll.action")
	 @ResponseBody
	 public Map<String, Object> getAll (HttpServletRequest request, int start, int limit) throws Exception {
			try {
				HttpSession session = request.getSession(true);
				String user = (String)session.getAttribute(Const.CURRENT_USER);
				if (user == null) {
					user = Util.getIpAddress(request);
					session.setAttribute(Const.CURRENT_USER, user);
				}
				String countStr = (String)session.getAttribute(Const.USER_COUNT);
				LOG.info("request.getAttribute-->user_count:" + countStr);
				if (countStr == null) {
					countStr = "0";
					session.setAttribute(Const.USER_COUNT, "0");
				}
				int count = Integer.parseInt(countStr);
				LOG.info("Login user:" + user + "; request times:" + count);
				int pageNum = Integer.parseInt(PlatformCfg.getContextProperty(Const.TOTAL_PAGE_NUM));
				List<AirportInfo> list = airportInfoService.getByPage(count % pageNum);
				count = count == 10000 ? 0 : count;
				session.setAttribute(Const.USER_COUNT, "" + (++count));
				LOG.info("User:" + user + "'s count:" + count);
				return ExtJSReturn.mapDatas(list.size(), list);
			} catch (Exception e) {
				return ExtJSReturn.mapError(e.getMessage());
			}
	}
	 
}
