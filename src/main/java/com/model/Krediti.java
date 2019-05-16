package com.model;
import java.io.Serializable;

public class Krediti implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pre;
	private int sifra;
	private int sifobs;
	private String partija;
	private int vrstaplate;
	private String user;
	private int godina;
	private String dam;
	private double dug;
	private int naobs;
	private String nazobs;
	private double rata;
	private int rbr;

	public Krediti() {
	}

	public int getPre() {
		return this.pre;
	}
	public void setPre(int pre) {
		this.pre = pre;
	}
	public int getSifra() {
		return this.sifra;
	}
	public void setSifra(int sifra) {
		this.sifra = sifra;
	}
	public int getSifobs() {
		return this.sifobs;
	}
	public void setSifobs(int sifobs) {
		this.sifobs = sifobs;
	}
	public String getPartija() {
		return this.partija;
	}
	public void setPartija(String partija) {
		this.partija = partija;
	}
	public int getVrstaplate() {
		return this.vrstaplate;
	}
	public void setVrstaplate(int vrstaplate) {
		this.vrstaplate = vrstaplate;
	}
	public String getUser() {
		return this.user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getGodina() {
		return this.godina;
	}
	public void setGodina(int godina) {
		this.godina = godina;
	}
	
	
	public String getDam() {
		return this.dam;
	}

	public void setDam(String dam) {
		this.dam = dam;
	}

	public double getDug() {
		return this.dug;
	}

	public void setDug(double dug) {
		this.dug = dug;
	}

	public int getNaobs() {
		return this.naobs;
	}

	public void setNaobs(int naobs) {
		this.naobs = naobs;
	}

	public String getNazobs() {
		return this.nazobs;
	}

	public void setNazobs(String nazobs) {
		this.nazobs = nazobs;
	}

	public double getRata() {
		return this.rata;
	}

	public void setRata(double rata) {
		this.rata = rata;
	}

	public int getRbr() {
		return this.rbr;
	}

	public void setRbr(int rbr) {
		this.rbr = rbr;
	}
	
	
	
	
	
	
	
	
}
