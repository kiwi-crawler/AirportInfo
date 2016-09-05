package com.birdbug.dao;


import java.util.List;

import com.birdbug.model.AirportInfo;

/**
* @author   ���� E-mail:pjm0008@163.com
* @date 2016��8��29�� ����10:31:45
* @version AirportInfo-1.0
**/
public interface AirportInfoDao {
	public List<AirportInfo> getAll();
	public List<AirportInfo> getByPage(int pageNo);
}
