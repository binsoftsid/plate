package com.model;
import java.io.Serializable;

public class Sindikati  implements Serializable{
	private static final long serialVersionUID = 1L;
	private int pre;
	private int sifsind;
	private String juzer;
	private String nazsind;
	private String pozsind;
	private double procsind;
	private String zirsind;
	
	public Sindikati() {
	}
	public int getPre() {
		return this.pre;
	}
	public void setPre(int pre) {
		this.pre = pre;
	}
	public int getSifsind() {
		return this.sifsind;
	}
	public void setSifsind(int sifsind) {
		this.sifsind = sifsind;
	}
	public String getJuzer() {
		return this.juzer;
	}
	public void setJuzer(String juzer) {
		this.juzer = juzer;
	}	
	public String getNazsind() {
		return this.nazsind;
	}
	public void setNazsind(String nazsind) {
		this.nazsind = nazsind;
	}
	public String getPozsind() {
		return this.pozsind;
	}
	public void setPozsind(String pozsind) {
		this.pozsind = pozsind;
	}
	public double getProcsind() {
		return this.procsind;
	}
	public void setProcsind(double procsind) {
		this.procsind = procsind;
	}
	public String getZirsind() {
		return this.zirsind;
	}
	public void setZirsind(String zirsind) {
		this.zirsind = zirsind;
	}

}
