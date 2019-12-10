package org.pica.chc.dto;

public class CertificateDto {

	// 每行显示的字符个数大小
	private static final int ROW_CHARACTER_SIZE = 16;
	// 用户id
	private String id;
	// 证书用户姓名
	private String username;
	// 医院
	private String hospital;
	// 分区名称
	private String zonename;
	// 全国排名
	private String allposi;
	// 分区排名
	private String zoneposi;
	// 证书编号
	private String serial;
	//奖项等级
	private String level;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getZonename() {
		return zonename;
	}

	public void setZonename(String zonename) {
		this.zonename = zonename;
	}

	public String getAllposi() {
		return allposi;
	}

	public void setAllposi(String allposi) {
		this.allposi = allposi;
	}

	public String getZoneposi() {
		return zoneposi;
	}

	public void setZoneposi(String zoneposi) {
		this.zoneposi = zoneposi;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}



	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public String toString() {
		//@与#作为辅助符号
		return "  " + hospital + " @" + username + "# 在2019年国家基层高血压防治管理知识竞赛中，荣获"+level+"（全国第" + allposi + "名，"
				+ zonename + "地区第" + zoneposi + "名），特此表彰。";
	}

}
