package com.model;
import java.io.Serializable;

public class Rj implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int Pre;
	private int Jur;
	private String Nazjur;
	
	public Rj(){
	
	}
	public Rj(int _pre,int _jur,String _nazjur){
		this.Pre = _pre;
		this.Jur = _jur;
		this.Nazjur = _nazjur;
		
	}
	public int getPre(){
		return Pre;
	}
	public int getJur(){
		return Jur;
	}
	public String getNazjur(){
		return Nazjur;
	}
	public void setPre(int value){
		Pre = value;
	}
	public void setJur(int value){
		Jur = value;
	}
	public void setNazjur(String value){
		Nazjur = value;
	}

}
