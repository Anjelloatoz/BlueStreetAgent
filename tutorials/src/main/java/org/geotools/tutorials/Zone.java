package org.geotools.tutorials;

import java.util.ArrayList;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.algorithm.*;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class Zone {
	private int id;
	private String name = "Unnamed Zone";
	private String address = "Address not entered";
	private double center_latitude;
	private double center_longitude;
	private int radius;
	private ArrayList<Store> stores = new ArrayList<Store>();
	
	Zone(int id, String name, String address, double center_latitude, double center_longitude, int radius){
		this.id = id;
		this.name = name;
		this.address = address;
		this.center_latitude = center_latitude;
		this.center_longitude = center_longitude;
	}

	public int getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}
	public double getLatitude(){
		return this.center_latitude;
	}
	
	public double getLongitude(){
		return this.center_longitude;
	}

	public double getRadius(){
		return this.radius;
	}
	public String getAddress(){
		return this.address;
	}
	
	public void addNewStore(Store store){
		this.stores.add(store);
		this.calculateParameter();
	}
	
	public void addStore(Store store){
		this.stores.add(store);
	}
	
	public int getNumberOfStores(){
		return this.stores.size();
	}
	
	private void calculateParameter(){
		System.out.println("At calculateParameter number of stores: "+stores.size());
		Point[] points = new Point[stores.size()];
//		Point[] points = new Point[3];
		/***************************
		Coordinate coordp = new Coordinate(51.575953, 0.183118);
		points[0] = new Point(coordp, new PrecisionModel(), 1);
		Coordinate coordq = new Coordinate(51.578086, 0.180131);
		points[1] = new Point(coordq, new PrecisionModel(), 1);
		Coordinate coordr = new Coordinate(51.599016, 0.160990);
		points[2] = new Point(coordr, new PrecisionModel(), 1);
		***************************/
		for(int i = 0; i < stores.size(); i++){
			Coordinate coord = new Coordinate(stores.get(i).getLatitude(), stores.get(i).getLongitude());
			points[i] = new Point(coord, new PrecisionModel(), 1);
			System.out.println("Coord "+i+": "+stores.get(i).getLatitude()+", "+stores.get(i).getLongitude());
		}
		MultiPoint multipoint = new MultiPoint(points, new GeometryFactory());
		MinimumBoundingCircle circle = new MinimumBoundingCircle(multipoint);
		if(circle == null){
			System.out.println("circle is null");
		}
		else{
//			System.out.println("Circle x: "+circle.getCentre().x);
		}
		try{
			this.center_latitude = circle.getCentre().x;
			this.center_longitude = circle.getCentre().y;
		}
		catch(Exception ex){
			System.out.println("Exception: "+ex);
		}
		this.radius = 0;
		CoordinateReferenceSystem sourceCRS = null;
		try{
			sourceCRS = CRS.decode("EPSG:4326");
		}
		catch(Exception ex){
			System.out.println("Exception: "+ex);
		}
		for(int i = 0; i < stores.size(); i++){
			System.out.println("Stores: "+stores.size());
			System.out.println("Stores: "+stores.get(i).getPostcode());
			Coordinate coord = new Coordinate(stores.get(i).getLatitude(), stores.get(i).getLongitude());
			try{
				double tmp = JTS.orthodromicDistance(circle.getCentre(), coord, sourceCRS);
				System.out.println("Distance: "+tmp);
				if(tmp> radius){
					radius = (int)tmp;
				}
			}
			catch(Exception ex){
				System.out.println("Exception: "+ex);
			}
		}
	}
}