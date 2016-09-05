package com.birdbug.util;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:32:27
* @version AirportInfo-1.0
**/
public class Util {

	private static final Logger LOG = Logger.getLogger(Util.class);
	
	public static String getTime(int b) {
		Format formatter=new SimpleDateFormat("HH:mm");
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE,b);
		return formatter.format(cal.getTime());
	}
	
	/**
	 * -1:begin>end; 0:begin==end; 1:begin<end;
	 * @param begin
	 * @param end
	 * @return
	 */
	public static int compareTime(String begin, String end) {
		String b[] = begin.split(":");
		String e[] = end.split(":");
		if (Integer.parseInt(e[0]) < Integer.parseInt(b[0])) {
			return -1;
		} else if (Integer.parseInt(e[0]) == Integer.parseInt(b[0])) {
			if (Integer.parseInt(e[1]) < Integer.parseInt(b[1])) {
				return -1;
			} else if (Integer.parseInt(e[1]) == Integer.parseInt(b[1])) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}
	
	/**
	 * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public final static String getIpAddress(HttpServletRequest request) throws IOException {
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

		String ip = request.getHeader("X-Forwarded-For");
		
			LOG.debug("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
				
					LOG.debug("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
				
					LOG.debug("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
				
					LOG.debug("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
				
					LOG.debug("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
				
					LOG.debug("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}
}
