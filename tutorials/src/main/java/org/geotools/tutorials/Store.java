package org.geotools.tutorials;

public class Store {
	private int id;
	private int company;
	private String name;
	private String postcode;
	private String address;
	private double latitude;
	private double longitude;
	private int zone;
	
	Store(int id, int company, String name, String postcode, String address, double latitude, double longitude, int zone){
		this.id = id;
		this.company = company;
		this.name = name;
		this.postcode = postcode;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.zone = zone;
	}
	public double getLatitude(){
		return this.latitude;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public String getPostcode(){
		return this.postcode;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getZone(){
		return this.zone;
	}
	
	public int getID(){
		return this.id;
	}
	
	public int getCompany(){
		return this.company;
	}
}
