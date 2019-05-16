package com.model;
import java.io.Serializable;

public class Kvalifikacije  implements Serializable {
	private static final long serialVersionUID = 1L;
	private int sifkval;
	private String nazkval;
	
	public Kvalifikacije() {
	}
	public int getSifkval() {
		return this.sifkval;
	}
	public void setSifkval(int sifkval) {
		this.sifkval = sifkval;
	}
	public String getNazkval() {
		return this.nazkval;
	}
	public void setNazkval(String nazkval) {
		this.nazkval = nazkval;
	}
}
