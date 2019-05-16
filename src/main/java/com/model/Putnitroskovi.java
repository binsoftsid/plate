package com.model;
import java.io.Serializable;
import java.util.Date;


public class Putnitroskovi  implements Serializable {
	private static final long serialVersionUID = 1L;
	private int brisplate;
	private double bruto;
	private Date datum;
	private double iznosporeza;
	private int mesec;
	private double neoporiznos;
	private double neto;
	private double porez;
	private int pre;
	private int sifra;
	private double ukupaniznos;
	private String juzer;
	
	
	public Putnitroskovi() {
	}

	public int getBrisplate() {
		return this.brisplate;
	}
	public void setBrisplate(int brisplate) {
		this.brisplate = brisplate;
	}

	public double getBruto() {
		return this.bruto;
	}
	public void setBruto(double bruto) {
		this.bruto = bruto;
	}

	public Date getDatum() {
		return this.datum;
	}
	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public double getIznosporeza() {
		return this.iznosporeza;
	}
	public void setIznosporeza(double iznosporeza) {
		this.iznosporeza = iznosporeza;
	}

	public int getMesec() {
		return this.mesec;
	}
	public void setMesec(int mesec) {
		this.mesec = mesec;
	}

	public double getNeoporiznos() {
		return this.neoporiznos;
	}
	public void setNeoporiznos(double neoporiznos) {
		this.neoporiznos = neoporiznos;
	}

	public double getNeto() {
		return this.neto;
	}
	public void setNeto(double neto) {
		this.neto = neto;
	}

	public double getPorez() {
		return this.porez;
	}
	public void setPorez(double porez) {
		this.porez = porez;
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

	public double getUkupaniznos() {
		return this.ukupaniznos;
	}
	public void setUkupaniznos(double ukupaniznos) {
		this.ukupaniznos = ukupaniznos;
	}

	public String getJuzer() {
		return this.juzer;
	}
	public void setJuzer(String juzer) {
		this.juzer = juzer;
	}

}
