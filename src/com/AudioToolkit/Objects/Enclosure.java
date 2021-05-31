package com.AudioToolkit.Objects;

public class Enclosure {

	String name;
	int id;
	int type; //0 for sealed, 1 for ported
	int shape; //0 for rect, 1 for wedge, 2 for cylinder
	int numports;
	double Fb, portarea; //set to 0 for no port
	double Vb, thickness;
	String size;  // "X,X,X" in order shown on screen
					// LxWxH for rect
					// TopxBotxWidthXheight for wedge
					// LengthxRadius for cylinder
	double portratio; // 0 is sq port, 1 is spl port (unimplemented)
	double portlength;
	
	public Enclosure (int id, String name, int type, int shape, double Vb, double thickness, String size, double Fb,
			double portarea, int numports, double portlength, double portratio) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.shape = shape;
		this.Vb = Vb;
		this.thickness = thickness;
		this.size = size;
		this.Fb = Fb;
		this.portarea = portarea;
		this.numports = numports;
		this.portlength = portlength;
		this.portratio = portratio;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getShape() {
		return shape;
	}
	public void setShape(int shape) {
		this.shape = shape;
	}
	public double getFb() {
		return Fb;
	}
	public void setFb(double fb) {
		this.Fb = fb;
	}
	public double getPortarea() {
		return portarea;
	}
	public void setPortarea(double portarea) {
		this.portarea = portarea;
	}
	public double getThickness() {
		return thickness;
	}
	public void setThickness(double thickness) {
		this.thickness = thickness;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public int getNumports() {
		return numports;
	}
	public void setNumports(int numports) {
		this.numports = numports;
	}
	public double getVb() {
		return Vb;
	}
	public void setVb(double vb) {
		Vb = vb;
	}
	public double getPortlength() {
		return portlength;
	}
	public void setPortlength(double portlength) {
		this.portlength = portlength;
	}
	public double getPortratio() {
		return portratio;
	}
	public void setPortratio(double portratio) {
		this.portratio = portratio;
	}
}
