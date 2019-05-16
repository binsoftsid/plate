package com.model;
import java.io.Serializable;

public class Banke  implements Serializable {
	private static final long serialVersionUID = 1L;
	private int sifban;
	private int isplata;
	private String nazban;
	private String opis;
	private String pozban;
	private String zirban;
	private String juzer;

	public Banke() {
	}

	public int getSifban() {
		return this.sifban;
	}
	public void setSifban(int sifban) {
		this.sifban = sifban;
	}
	public int getIsplata() {
		return this.isplata;
	}
	public void setIsplata(int isplata) {
		this.isplata = isplata;
	}
	public String getNazban() {
		return this.nazban;
	}
	public void setNazban(String nazban) {
		this.nazban = nazban;
	}
	public String getOpis() {
		return this.opis;
	}
	public void setOpis(String opis) {
		this.opis = opis;
	}
	public String getPozban() {
		return this.pozban;
	}
	public void setPozban(String pozban) {
		this.pozban = pozban;
	}
	public String getZirban() {
		return this.zirban;
	}
	public void setZirban(String zirban) {
		this.zirban = zirban;
	}
	public String getJuzer() {
		return this.zirban;
	}
	public void setJuzer(String juzer) {
		this.juzer = juzer;
	}	
}
