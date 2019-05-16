package com.model;
import java.io.Serializable;

public class Users implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private int enabled;
	private String role;
	private int firma;
	private int godina;
	private String ime;
	private String prezime;
	private String nazivfirme;
	
	public Users() {
	}
	
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getEnabled() {
		return this.enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public String getRole() {
		return this.role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public int getFirma() {
		return this.firma;
	}
	public void setFirma(int firma) {
		this.firma = firma;
	}
	public int getGodina() {
		return this.godina;
	}
	public void setGodina(int godina) {
		this.godina = godina;
	}
	public String getIme() {
		return this.ime;
	}
	public void setIme(String ime) {
		this.ime = ime;
	}
	public String getPrezime() {
		return this.prezime;
	}
	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}
	public String getNazivfirme() {
		return this.nazivfirme;
	}
	public void setNazivfirme(String nazivfirme) {
		this.nazivfirme = nazivfirme;
	}
	
	
	
}
