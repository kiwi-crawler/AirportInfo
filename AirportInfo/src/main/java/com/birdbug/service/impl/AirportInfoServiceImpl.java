package com.birdbug.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.birdbug.dao.AirportInfoDao;
import com.birdbug.model.AirportInfo;
import com.birdbug.service.AirportInfoService;

/**
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:32:10
* @version AirportInfo-1.0
**/
@Service("airportInfoService")
public class AirportInfoServiceImpl implements AirportInfoService {

	@Resource
	private AirportInfoDao airportInfoDao;
	
	@Override
	public List<AirportInfo> getAll() {
		// TODO Auto-generated method stub
		return airportInfoDao.getAll();
	}

	@Override
	public List<AirportInfo> getByPage(int pageNo) {
		// TODO Auto-generated method stub
		return airportInfoDao.getByPage(pageNo);
	}

}
