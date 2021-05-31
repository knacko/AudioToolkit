package com.AudioToolkit.Objects;

public class Subwoofer {
	
	int id = -1;
	String name;
	double Qes, Qts, Vb, Vas, Fs, Xmax, Sd, Pemax;
	
	public double getPemax() {
		return Pemax;
	}

	public void setPemax(double pemax) {
		Pemax = pemax;
	}

	public Subwoofer(int id, String name, double Qes, double Qts, double Vas, double Fs, double Pemax, double Xmax, double Sd) {
		
		this.id = id;
		this.name = name;
		this.Qes = Qes;
		this.Qts = Qts;
		this.Vas = Vas;
		this.Fs = Fs;
		this.Xmax = Xmax;
		this.Sd = Sd;	
		this.Pemax = Pemax;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getQes() {
		return Qes;
	}

	public void setQes(double qes) {
		Qes = qes;
	}

	public double getQts() {
		return Qts;
	}

	public void setQts(double qts) {
		Qts = qts;
	}

	public double getVb() {
		return Vb;
	}

	public void setVb(double vb) {
		Vb = vb;
	}

	public double getVas() {
		return Vas;
	}

	public void setVas(double vas) {
		Vas = vas;
	}

	public double getFs() {
		return Fs;
	}

	public void setFs(double fs) {
		Fs = fs;
	}

	public double getXmax() {
		return Xmax;
	}

	public void setXmax(double xmax) {
		Xmax = xmax;
	}

	public double getSd() {
		return Sd;
	}

	public void setSd(double sd) {
		Sd = sd;
	}	
	
	public double getVd() {
		
		return getSd()*getXmax()*0.0001/1000;
	}
	
	public String toString() {
		
		String s = "";
		
		s += "id(" + id + "), ";
		s += "name(" + name + "), ";
		s += "Qes(" + Qes + "), ";
		s += "Qts(" + Qts + "), ";
		s += "Vas(" + Vas + "), ";
		s += "Fs(" + Fs + "), ";
		s += "Xmax(" + Xmax + "), ";
		s += "Sd(" + Sd + "), ";
		s += "Pemax(" + Pemax + ")";
				
		return s;
	}
	
}
