package org.geotools.tutorials;

import java.util.ArrayList;

public class SalesCategory {
	private int ID;
	private int parent_id;
	private String name;
	ArrayList<SalesCategory> child_list = new ArrayList<SalesCategory>();
	
	SalesCategory(int ID, int parent_id, String name){
		this.ID = ID;
		this.parent_id = parent_id;
		this.name = name;
	}
	
	SalesCategory(int parent_id, String name){
		this.parent_id = parent_id;
		this.name = name;
	}
	
	public void addChild(SalesCategory sales_category){
		this.child_list.add(sales_category);
	}
	
	void setID(int ID){
		this.ID = ID;
	}
	
	int getID(){
		return this.ID;
	}
	
	int getParentId(){
		return this.parent_id;
	}
	
	String getName(){
		return this.name;
	}
}
