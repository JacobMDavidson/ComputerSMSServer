package com.jacobmdavison.computersmsserver;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SMSMessage {
	private String number;
	private String body;
	private String type;

	@XmlElement
	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	@XmlElement
	public void setBody(String body) {
		this.body = body;
	}

	public String getBody() {
		return body;
	}

	@XmlElement
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
