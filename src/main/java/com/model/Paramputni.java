package com.model;
import java.io.Serializable;

public class Paramputni  implements Serializable {
	private static final long serialVersionUID = 1L;
	private int rbr;
	private double neoporeziviiznos;
	private double porez;

	public Paramputni() {
	}

	public int getRbr() {
		return this.rbr;
	}
	public void setRbr(int rbr) {
		this.rbr = rbr;
	}
	public double getNeoporiznos() {
		return this.neoporeziviiznos;
	}
	public void setNeoporiznos(double neoporeziviiznos) {
		this.neoporeziviiznos = neoporeziviiznos;
	}
	public double getPorez() {
		return this.porez;
	}
	public void setPorez(double porez) {
		this.porez = porez;
	}

}