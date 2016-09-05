package com.birdbug.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import com.birdbug.common.ImageMap;


/**
* @author   作者 E-mail:pjm0008@163.com
* @date 2016年8月29日 下午10:32:02
* @version AirportInfo-1.0
**/
@JsonAutoDetect
@Entity
@Table(name="AirportInfo")
public class AirportInfo {
	@Id
	@Column(name="ID", nullable=false)
	private int id;
	
	@Column(name="BeginTime", nullable=false)
	private String beginTime;
	
	@Column(name="EndTime", nullable=false)
	private String endTime;
	
	@Column(name="AirNo", nullable=false)
	private String airNo = "";
	
	@Column(name="SubAirNo", nullable=false)
	private String subAirNo = "";

	@Column(name="AirCom", nullable=true)
	private String airCom;
	
	@Column(name="Terminal", nullable=false)
	private String terminal;
	
	@Column(name="Destination", nullable=false)
	private String destination = "";
	
	@Column(name="Counter", nullable=false)
	private String counter;
	
	@Column(name="status", nullable=false)
	private String status;
	
	public void setFieldValue(String key, Object val) {
		switch(key) {
		case "ID":
			this.setId(Integer.parseInt("" + val));
			break;
		case "显示计划时间":
			this.setBeginTime("" + val);
			break;
		case "显示计划到达时间":
			this.setEndTime("" + val);
			break;
		case "主航班号":
			this.setAirNo(val + airNo);
			break;
		case "子航班号":
			if (val != null) {
				String tmp = "" + val;
				val = tmp.replaceAll("<div class='HangBan_list'><div class='List'><ul><li>", " ")
						.replaceAll("</li><li>", "  ").replaceAll("</li></ul></div></div>",	"");
			}
			this.setSubAirNo("" + val);
			break;
		case "航空公司":
			if (val != null) {
				String tmp = "" + val;
				String img = ImageMap.getImg(tmp);
				//String imgstr = "<img src=\"../airportimg/" + img + "\">";
				this.setAirCom(img + "#" + val);
			}
			//this.setAirCom("" + val);
			break;
		case "候机楼":
			this.setTerminal("" + val);
			break;
		case "目的地":
			this.setDestination(val +"<br>" + destination);
			break;
		case "经停地":
			String tmp = (val==null || val.equals(""))?"":(destination + val + "(经停地)");
			this.setDestination(tmp);
			break;
		case "值机柜台":
			this.setCounter("" + val);
			break;
		case "状态":
			if (val != null) {
				String tmp1 = "" + val;
				if (tmp1.contains("延误")) {
					String div_start = "<div style=\"height:30px;width:80px;background:red;margin:0px 32%;padding:5px 20px 5px 20px;border-radius:5px;\">";
					String div_end = "</div>";
					this.setStatus(div_start + "延误" + div_end);
				}else if (tmp1.contains("结束登机")) {
					String div_start = "<div style=\"height:30px;width:80px;background:green;margin:0px 32%;padding:5px 10px 5px 10px;border-radius:5px;\">";
					String div_end = "</div>";
					this.setStatus(div_start + "结束登机" + div_end);
				}else if (tmp1.contains("正在登机")) {
					String div_start = "<div style=\"height:30px;width:80px;background:orange;margin:0px 32%;padding:5px 10px 5px 10px;border-radius:5px;\">";
					String div_end = "</div>";
					this.setStatus(div_start + "正在登机" + div_end);
				} else {					
					this.setStatus("" + val);
				}
			}
			break;
		
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAirNo() {
		return airNo;
	}

	public void setAirNo(String airNo) {
		this.airNo = airNo;
	}

	

	public String getSubAirNo() {
		return subAirNo;
	}

	public void setSubAirNo(String subAirNo) {
		this.subAirNo = subAirNo;
	}

	public String getAirCom() {
		return airCom;
	}

	public void setAirCom(String airCom) {
		this.airCom = airCom;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	
	
}
