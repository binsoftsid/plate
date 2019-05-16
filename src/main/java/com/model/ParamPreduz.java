package com.model;
import java.io.Serializable;

public class ParamPreduz implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int rbr;
	private double koeficijent;
	private double neopor;
	private double pio;
	private double zdr;
	private double nezap;
	private double porez;
	
	public ParamPreduz(){
	
	}
	public ParamPreduz(int _rbr,double _koeficijent,double _neopor,double _pio,
			double _zdr,double _nezap,double _porez){
		this.rbr = _rbr;
		this.koeficijent = _koeficijent;
		this.neopor = _neopor;
		this.pio = _pio;
		this.zdr = _zdr;
		this.nezap = _nezap;
		this.porez = _porez;
		
	}
	public int getRbr(){
		return rbr;
	}
	public void setRbr(int _rbr){
		rbr = _rbr;
	}
	public double getKoeficijent(){
		return koeficijent;
	}
	public void setKoeficijent(double _koeficijent){
		koeficijent = _koeficijent;
	}
	public double getNeopor(){
		return neopor;
	}
	public void setNeopor(double _neopor){
		neopor = _neopor;
	}
	public double getPio(){
		return pio;
	}
	public void setPio(double _pio){
		pio = _pio;
	}
	public double getZdr(){
		return zdr;
	}
	public void setZdr(double _zdr){
		zdr = _zdr;
	}
	public double getNezap(){
		return nezap;
	}
	public void setNezap(double _nezap){
		nezap = _nezap;
	}
	public double getPorez(){
		return porez;
	}
	public void setPorez(double _porez){
		porez = _porez;
	}

}
