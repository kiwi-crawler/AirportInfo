package com.birdbug.service;

import java.util.List;

import com.birdbug.model.AirportInfo;

/**
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:32:15
* @version AirportInfo-1.0
**/
public interface AirportInfoService {

	public List<AirportInfo> getAll();
	public List<AirportInfo> getByPage(int pageNo);
}
