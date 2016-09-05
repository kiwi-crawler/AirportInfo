package com.birdbug.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:32:21
* @version AirportInfo-1.0
**/
@Component
public class ExtJSReturn {

	/**
	 * Generates modelMap to return in the modelAndView
	 * @param contacts
	 * @return
	 */
	public static Map<String, Object> mapDatas(int total, List<?> datas) {
		Map<String, Object> modelMap = new HashMap<String, Object>(3);
		modelMap.put("total", total);
		modelMap.put("data", datas);
		modelMap.put("success", true);
		
		return modelMap;
	}
	
	/**
	 * Generates modelMap to return in the modelAndView
	 * @param contacts
	 * @return
	 */
	public static Map<String, Object> mapDatas(long total, List<?> datas) {
		Map<String, Object> modelMap = new HashMap<String, Object>(3);
		modelMap.put("total", total);
		modelMap.put("data", datas);
		modelMap.put("success", true);
		
		return modelMap;
	}
	
	/**
	 * Generates modelMap to return in the modelAndView
	 * @param contacts
	 * @return
	 */
	public static Map<String, Object> mapData(Object data) {
		Map<String, Object> modelMap = new HashMap<String,Object>(2);
		modelMap.put("data", data);
		modelMap.put("success", true);
		
		return modelMap;
	}
	
	public static Map<String, Object> mapMessage(String msg) {
		Map<String, Object> modelMap = new HashMap<String,Object>(2);
		modelMap.put("message", msg);
		modelMap.put("success", true);
		
		return modelMap;
	}
	
	public static Map<String, Object> mapData3(Object data) {
		Map<String, Object> modelMap = new HashMap<String,Object>(2);
		modelMap.put("path", data);
		modelMap.put("success", true);
		
		return modelMap;
	}
	
	public static Map<String, Object> mapMenu(Object data) {
		Map<String, Object> modelMap = new HashMap<String,Object>(2);
		modelMap.put("protocol", data);
		
		return modelMap;
	}
	
	/**
	 * Generates modelMap to return in the modelAndView
	 * @param contacts
	 * @return
	 */
	public static Map<String, Object> mapSuccess() {
		Map<String, Object> modelMap = new HashMap<String,Object>(2);
		modelMap.put("success", true);
		
		return modelMap;
	}
	
	/**
	 * Generates modelMap to return in the modelAndView
	 * @param contacts
	 * @return
	 */
	public static Map<String, Object> mapFailure() {
		Map<String, Object> modelMap = new HashMap<String,Object>(2);
		modelMap.put("success", false);
		
		return modelMap;
	}
	
	/**
	 * Generates modelMap to return in the modelAndView in case
	 * of exception
	 * @param msg message
	 * @return
	 */
	public static Map<String,Object> mapError(String msg){
		Map<String,Object> modelMap = new HashMap<String,Object>(2);
		modelMap.put("message", msg);
		modelMap.put("success", false);

		return modelMap;
	} 
}
