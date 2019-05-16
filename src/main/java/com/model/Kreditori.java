package com.model;
import java.io.Serializable;

public class Kreditori implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int sifobs;
	private String nazobs;
	private String zirobs;
	private String pozobs;
	private int naobs;
	
	public Kreditori(){
		
	}
	public Kreditori(int _sifobs,String _nazobs,String _zirobs,String _pozobs,int _naobs){
		this.sifobs = _sifobs;
		this.nazobs = _nazobs;
		this.zirobs = _zirobs;
		this.pozobs = _pozobs;
		this.naobs = _naobs;
	}	
	public int getSifobs(){
		return sifobs;
	}
	public void setSifobs(int _sifobs){
		sifobs = _sifobs;
	}
	public String getNazobs(){
		return nazobs;
	}
	public void setNazobs(String _nazobs){
		nazobs = _nazobs;
	}
	public String getZirobs(){
		return zirobs;
	}
	public void setZirobs(String _zirobs){
		zirobs = _zirobs;
	}
	public String getPozobs(){
		return pozobs;
	}
	public void setPozobs(String _pozobs){
		pozobs = _pozobs;
	}
	public int getNaobs(){
		return naobs;
	}
	public void setNaobs(int _naobs){
		naobs = _naobs;
	}
	
}
