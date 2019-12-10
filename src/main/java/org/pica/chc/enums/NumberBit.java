package org.pica.chc.enums;

public enum NumberBit {
	
	NUM1("num1"),NUM2("num2"),NUM3("num3"),NUM4("num4");
	private String code;
	

	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}

	private NumberBit(String code) {
		this.code = code;
	}
	
}
