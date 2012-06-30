package org.geotools.tutorials;

import java.util.Date;
import java.util.ArrayList;

public class Promotion {
	int ID;
	int company_ID;
	String main_title;
	String description;
	Date from_date;
	Date to_date;
	int sales_category_id;
	ArrayList<Integer> store_list = new ArrayList<Integer>();
}
