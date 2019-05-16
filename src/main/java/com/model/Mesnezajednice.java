package com.model;
import java.io.Serializable;

public class Mesnezajednice implements Serializable {
	private static final long serialVersionUID = 1L;
	private int sifmz;
	private String user;
	private String nazmz;
	private int nista;
	private String pozmz;
	private double procmz;
	private String zirmz;

	public Mesnezajednice() {
	}

	public int getSifmz() {
		return this.sifmz;
	}
	public void setSifmz(int sifmz) {
		this.sifmz = sifmz;
	}
	public String getUser() {
		return this.user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getNazmz() {
		return this.nazmz;
	}
	public void setNazmz(String nazmz) {
		this.nazmz = nazmz;
	}
	public int getNista() {
		return this.nista;
	}
	public void setNista(int nista) {
		this.nista = nista;
	}
	public String getPozmz() {
		return this.pozmz;
	}
	public void setPozmz(String pozmz) {
		this.pozmz = pozmz;
	}
	public double getProcmz() {
		return this.procmz;
	}
	public void setProcmz(double procmz) {
		this.procmz = procmz;
	}
	public String getZirmz() {
		return this.zirmz;
	}
	public void setZirmz(String zirmz) {
		this.zirmz = zirmz;
	}
}