package com.model;
import java.io.Serializable;

public class Doprinosi implements Serializable {
	private static final long serialVersionUID = 1L;
	private int pre;
	private int sifdop;
	private int vrstaplate;
	private String juzer;
	private String nazdop;
	private String pozdop;
	private double procdop;
	private String sifra;
	private int vrdop;
	private String zirdop;

	public Doprinosi() {
	}
	public int getPre() {
		return this.pre;
	}
	public void setPre(int pre) {
		this.pre = pre;
	}
	public int getSifdop() {
		return this.sifdop;
	}
	public void setSifdop(int sifdop) {
		this.sifdop = sifdop;
	}
	public int getVrstaplate() {
		return this.vrstaplate;
	}
	public void setVrstaplate(int vrstaplate) {
		this.vrstaplate = vrstaplate;
	}
	public String getJuzer() {
		return this.juzer;
	}
	public void setJuzer(String juzer) {
		this.juzer = juzer;
	}
	public String getNazdop() {
		return this.nazdop;
	}
	public void setNazdop(String nazdop) {
		this.nazdop = nazdop;
	}
	public String getPozdop() {
		return this.pozdop;
	}
	public void setPozdop(String pozdop) {
		this.pozdop = pozdop;
	}
	public double getProcdop() {
		return this.procdop;
	}
	public void setProcdop(double procdop) {
		this.procdop = procdop;
	}
	public String getSifra() {
		return this.sifra;
	}
	public void setSifra(String sifra) {
		this.sifra = sifra;
	}
	public int getVrdop() {
		return this.vrdop;
	}
	public void setVrdop(int vrdop) {
		this.vrdop = vrdop;
	}
	public String getZirdop() {
		return this.zirdop;
	}
	public void setZirdop(String zirdop) {
		this.zirdop = zirdop;
	}
}
