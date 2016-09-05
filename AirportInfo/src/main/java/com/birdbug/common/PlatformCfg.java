package com.birdbug.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:31:10
* @version AirportInfo-1.0
**/
public class PlatformCfg extends PropertyPlaceholderConfigurer{

	private static Map<String, Object> ctxPropertiesMap;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,Properties props) throws BeansException 
	{
		super.processProperties(beanFactoryToProcess, props);
		ctxPropertiesMap = new HashMap<String, Object>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			ctxPropertiesMap.put(keyStr, value);
		}
	}

	public static String getContextProperty(String name) {
		return ctxPropertiesMap.get(name).toString();
	}
}
