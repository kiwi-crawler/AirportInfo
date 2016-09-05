package com.birdbug.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import com.birdbug.common.ImageMap;


/**
* @author   ���� E-mail:pjm0008@163.com
* @date 2016��8��29�� ����10:32:02
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
		case "��ʾ�ƻ�ʱ��":
			this.setBeginTime("" + val);
			break;
		case "��ʾ�ƻ�����ʱ��":
			this.setEndTime("" + val);
			break;
		case "�������":
			this.setAirNo(val + airNo);
			break;
		case "�Ӻ����":
			if (val != null) {
				String tmp = "" + val;
				val = tmp.replaceAll("<div class='HangBan_list'><div class='List'><ul><li>", " ")
						.replaceAll("</li><li>", "  ").replaceAll("</li></ul></div></div>",	"");
			}
			this.setSubAirNo("" + val);
			break;
		case "���չ�˾":
			if (val != null) {
				String tmp = "" + val;
				String img = ImageMap.getImg(tmp);
				//String imgstr = "<img src=\"../airportimg/" + img + "\">";
				this.setAirCom(img + "#" + val);
			}
			//this.setAirCom("" + val);
			break;
		case "���¥":
			this.setTerminal("" + val);
			break;
		case "Ŀ�ĵ�":
			this.setDestination(val +"<br>" + destination);
			break;
		case "��ͣ��":
			String tmp = (val==null || val.equals(""))?"":(destination + val + "(��ͣ��)");
			this.setDestination(tmp);
			break;
		case "ֵ����̨":
			this.setCounter("" + val);
			break;
		case "״̬":
			if (val != null) {
				String tmp1 = "" + val;
				if (tmp1.contains("����")) {
					String div_start = "<div style=\"height:30px;width:80px;background:red;margin:0px 32%;padding:5px 20px 5px 20px;border-radius:5px;\">";
					String div_end = "</div>";
					this.setStatus(div_start + "����" + div_end);
				}else if (tmp1.contains("�����ǻ�")) {
					String div_start = "<div style=\"height:30px;width:80px;background:green;margin:0px 32%;padding:5px 10px 5px 10px;border-radius:5px;\">";
					String div_end = "</div>";
					this.setStatus(div_start + "�����ǻ�" + div_end);
				}else if (tmp1.contains("���ڵǻ�")) {
					String div_start = "<div style=\"height:30px;width:80px;background:orange;margin:0px 32%;padding:5px 10px 5px 10px;border-radius:5px;\">";
					String div_end = "</div>";
					this.setStatus(div_start + "���ڵǻ�" + div_end);
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
