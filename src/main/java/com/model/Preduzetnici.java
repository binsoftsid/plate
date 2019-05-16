package com.model;
import java.io.Serializable;

public class Preduzetnici  implements Serializable  {
	private static final long serialVersionUID = 1L;

	private int pre;
	private int sifra;
	private String adresa;
	private String email;
	private String ime;
	private String jmbg;
	private String matbr;
	private String mesto;
	private String napomena;
	private String nazivfirme;
	private int pbroj;
	private String pib;
	private String prezime;
	private String sifraopstine;
	private String tekuci;
	private String telefon;
	private String user;

	public Preduzetnici() {
	
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
	public String getUser() {
		return this.user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getAdresa() {
		return this.adresa;
	}
	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIme() {
		return this.ime;
	}
	public void setIme(String ime) {
		this.ime = ime;
	}
	public String getJmbg() {
		return this.jmbg;
	}
	public void setJmbg(String jmbg) {
		this.jmbg = jmbg;
	}
	public String getMatbr() {
		return this.matbr;
	}
	public void setMatbr(String matbr) {
		this.matbr = matbr;
	}
	public String getMesto() {
		return this.mesto;
	}
	public void setMesto(String mesto) {
		this.mesto = mesto;
	}
	public String getNapomena() {
		return this.napomena;
	}
	public void setNapomena(String napomena) {
		this.napomena = napomena;
	}
	public String getNazivfirme() {
		return this.nazivfirme;
	}
	public void setNazivfirme(String nazivfirme) {
		this.nazivfirme = nazivfirme;
	}
	public int getPbroj() {
		return this.pbroj;
	}
	public void setPbroj(int pbroj) {
		this.pbroj = pbroj;
	}
	public String getPib() {
		return this.pib;
	}
	public void setPib(String pib) {
		this.pib = pib;
	}
	public String getPrezime() {
		return this.prezime;
	}
	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}
	public String getSifraopstine() {
		return this.sifraopstine;
	}
	public void setSifraopstine(String sifraopstine) {
		this.sifraopstine = sifraopstine;
	}
	public String getTekuci() {
		return this.tekuci;
	}
	public void setTekuci(String tekuci) {
		this.tekuci = tekuci;
	}
	public String getTelefon() {
		return this.telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}



}
