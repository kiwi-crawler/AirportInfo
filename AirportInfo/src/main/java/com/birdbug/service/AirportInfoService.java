package com.birdbug.service;

import java.util.List;

import com.birdbug.model.AirportInfo;

/**
* @author   ���� E-mail:pjm0008@163.com
* @date 2016��8��29�� ����10:32:15
* @version AirportInfo-1.0
**/
public interface AirportInfoService {

	public List<AirportInfo> getAll();
	public List<AirportInfo> getByPage(int pageNo);
}
