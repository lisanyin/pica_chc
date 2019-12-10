package org.pica.chc.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CertificateDto {

	// ÿ����ʾ���ַ�������С
	private static final int ROW_CHARACTER_SIZE = 16;
	// �û�id
	private String id;
	// ֤���û�����
	private String username;
	// ҽԺ
	private String hospital;
	// ��������
	private String zonename;
	// ȫ������
	private String allposi;
	// ��������
	private String zoneposi;
	// ֤����
	private String serial;
	//����ȼ�
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
		//@��#��Ϊ��������
		return "  " + hospital + " @" + username + "# ��2019����һ����Ѫѹ���ι���֪ʶ�����У��ٻ�"+level+"��ȫ����" + allposi + "����"
				+ zonename + "������" + zoneposi + "�������ش˱��á�";
	}

}
