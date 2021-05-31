package com.AudioToolkit.Objects;

public class Vehicle {
	
	//private variables
	int id;
	String make;
	String model;
	String year;
	String body;
	String trim;
	String speakers;
	
	// Empty constructor
	public Vehicle(){
		
	}
	// constructor
	public Vehicle(int id, String make, String model, String year, String body, String trim, String speakers){
		this.id = id;
		this.make = make;
		this.model = model;
		this.year = year;
		this.body = body;
		this.trim = trim;
		this.speakers = speakers;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getTrim() {
		return trim;
	}
	public void setTrim(String trim) {
		this.trim = trim;
	}
	public String getSpeakers() {
		return speakers;
	}
	public void setSpeakers(String speakers) {
		this.speakers = speakers;
	}
	
}
