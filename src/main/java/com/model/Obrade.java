package com.model;
import java.io.Serializable;

public class Obrade  implements Serializable {
	private static final long serialVersionUID = 1L;
	private int mesec;
	private String brojobrade;
	private String vrstaplate;
	private String strvrsta;

	public Obrade() {
	}

	public int getMesec() {
		return this.mesec;
	}
	public void setMesec(int mesec) {
		this.mesec = mesec;
	}
	public String getBrojobrade() {
		return this.brojobrade;
	}
	public void setBrojobrade(String brojobrade) {
		this.brojobrade = brojobrade;
	}
	public String getVrstaplate() {
		return this.vrstaplate;
	}
	public void setVrstaplate(String vrstaplate) {
		this.vrstaplate = vrstaplate;
	}
	public String getStrvrstaplate() {
		return this.strvrsta;
	}
	public void setStrvrstaplate(String strvrsta) {
		this.strvrsta = strvrsta;
	}


}
