package org.geotools.tutorials;

import java.util.ArrayList;

public class Company {
	private int id;
	private String name;
	private String address;
	private String pc;
	private String entered = "Not set yet";
	private String logo = "Not set yet";
	private String telephone = "Not set yet";
	private String email;
	private String cperson;
	boolean online;
	private ArrayList<Store> stores = new ArrayList<Store>();
	
	Company(String name, String address, String entered, String logo, String telephone, String email, String cperson, boolean online){
		this.name = name;
		this.address = address;
		this.entered = entered;
		this.logo = logo;
		this.telephone = telephone;
		this.email = email;
		this.cperson = cperson;
		this.online = online;
	}
	
	Company(int id, String name, String address, String entered, String telephone, String email, String cperson, boolean online){
		this.id = id;
		this.name = name;
		this.address = address;
		this.entered = entered;
		this.telephone = telephone;
		this.email = email;
		this.cperson = cperson;
		this.online = online;
	}

	public void setName(String name){
		this.name = name;
	}
	public void setId(int id){
		this.id = id;
	}
	public void setAddress(String address){
		this.address = address;
	}
	public void setPc(String pc){
		this.pc = pc;
	}
	public void setEmail(String email){
		this.email = email;
	}
	public void setCperson(String cperson){
		this.cperson = cperson;
	}
	public String getName(){
		return this.name;
	}
	public int getId(){
		return this.id;
	}
	public String getAddress(){
		return this.address;
	}
	public String getPc(){
		return this.pc;
	}
	public String getEntered(){
		return this.entered;
	}
	public String getLogo(){
		return this.logo;
	}
	public String getTelephone(){
		return this.telephone;
	}
	public boolean getStatus(){
		return this.online;
	}
	public String getEmail(){
		return this.email;
	}
	public String getCperson(){
		return this.cperson;
	}
	public void addStore(Store store){
		this.stores.add(store);
	}
}
