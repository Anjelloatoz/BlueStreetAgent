package org.geotools.tutorials;

import org.opengis.referencing.crs.*;
import org.geotools.referencing.crs.*;
import org.geotools.geometry.jts.JTS;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;

import org.geotools.referencing.*;
import org.opengis.referencing.operation.*;
import org.geotools.referencing.operation.*;
import org.opengis.geometry.*;
import org.geotools.geometry.*;

import org.w3c.dom.Document;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.HttpResponse;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geom.impl.*;


public class App implements ActionListener
{
	ArrayList<Zone> zone_list = new ArrayList<Zone>();
	ArrayList<Company> company_list = new ArrayList<Company>();
//	ArrayList<Store> store_list = new ArrayList<Store>();
	ArrayList<Point> points = new ArrayList<Point>();
	
	String ss = "Status: ";

	JMenu file_menu;
	JMenu help_menu;

	JFrame main_frame;
	JTabbedPane tabbed_pane;
	JLabel testLabel;
	JLabel status_label = new JLabel(ss);
	ZonePanel zone_panel;
	CompanyPanel company_panel;
	StorePanel store_panel;
	PromotionPanel promotion_panel;

	JTextField store_name_tf = new JTextField(20);
	JTextField store_address_tf = new JTextField(20);
	JTextField store_pc_tf = new JTextField(10);
	JComboBox zones_cb = new JComboBox();
	JLabel number_of_stores_label = new JLabel();

	JButton addstore_bt = new JButton("Add Store");
	JButton clrstore_bt = new JButton("Clear Store");
	double lat;
	double lon;

	App(){
		//		this.algoTest();
		main_frame = new JFrame("Anjelloatoz@gmail.com");
		main_frame.setBounds(100, 100, 780, 565);
		main_frame.setResizable(false);
		main_frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		zones_cb.addItemListener(new MyItemListener());

		JMenuBar menuBar = new JMenuBar();
		file_menu = new JMenu("File");
		file_menu.add(make("New Company"));
		file_menu.add(make("New Store"));
		file_menu.add(make("New Category"));
		file_menu.add(make("New Promotion"));
		file_menu.addSeparator();
		file_menu.add(make("Exit"));
		menuBar.add(file_menu);
		
		help_menu = new JMenu("Help");
		help_menu.add(make("About"));
		menuBar.add(help_menu);

		this.main_frame.setJMenuBar(menuBar);

		getZones("http://www.svgonbatik.com/fcn/bluestreet_zone_server.php");
		getStores("http://www.svgonbatik.com/fcn/bluestreet_store_server_xml.php");
		getCompanies("http://www.bluestreet.co.uk/script/bluestreet_company_server_xml.php");
		

		JPanel panel = new JPanel();
		company_panel = new CompanyPanel(this, company_list);
		store_panel = new StorePanel(this, company_list, zone_list);
		zone_panel = new ZonePanel(this);
		promotion_panel = new PromotionPanel(this, company_list, zone_list);
		
		populateZones();
		tabbed_pane = new JTabbedPane();
		tabbed_pane.addTab("Stores", store_panel);
		tabbed_pane.addTab("Zones", zone_panel);
		tabbed_pane.addTab("Company", company_panel);
		tabbed_pane.addTab("Promotion", promotion_panel);
		testLabel = new JLabel();
		main_frame.add(tabbed_pane);
		panel.add(testLabel);

		main_frame.setVisible(true);
		//		this.zoneAssociationCreator();
		//		this.GRStoWGS(0, 0);
	}

	JPanel storePanel(){
		JPanel storepanel = new JPanel();
		JLabel store_name_label = new JLabel("Store Name");
		JLabel store_address_label = new JLabel("Address");
		JLabel store_pc_label = new JLabel("Post Code");

		Box b1 = new Box(BoxLayout.X_AXIS);
		b1.add(zones_cb);
		b1.add(number_of_stores_label);

		Box b2 = new Box(BoxLayout.X_AXIS);
		b2.add(store_name_label);
		b2.add(store_name_tf);

		Box b3 = new Box(BoxLayout.X_AXIS);
		b3.add(store_address_label);
		b3.add(store_address_tf);

		Box b4 = new Box(BoxLayout.X_AXIS);
		b4.add(store_pc_label);
		b4.add(store_pc_tf);

		Box b = new Box(BoxLayout.Y_AXIS);
		b.add(b1);
		b.add(b2);
		b.add(b3);
		b.add(b4);

		storepanel.add(b);
		storepanel.add(addstore_bt);
		storepanel.add(clrstore_bt);
		storepanel.add(status_label);
		addstore_bt.setActionCommand("addstore");

		addstore_bt.addActionListener(this);

		return storepanel;
	}

	void clearFields(){
		this.store_name_tf.setText("");
		this.store_address_tf.setText("");
		this.store_pc_tf.setText("");
	}

	void populateZones(){
		zones_cb.removeAllItems();
		zones_cb.addItem("Select Zone");
		for(int i = 0; i < zone_list.size(); i++){
			zones_cb.addItem(zone_list.get(i).getName());
		}
		System.out.println("Number of zones: "+zone_list.size());
	}

	void populateCompanies(JComboBox combo){
		combo.removeAllItems();
		combo.addItem("Select Company");
		for(int i = 0; i < company_list.size(); i++){
			combo.addItem(company_list.get(i).getName());
		}
		System.out.println("Number of companies: "+company_list.size());
	}

	boolean seekPostCode(String pc){
		if(pc.equals("")){
			JOptionPane.showMessageDialog(null, "You must enter the post code to proceed", "Empty Post Code entry", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		pc = pc.replaceAll("\\s+","");
		pc = pc.toUpperCase();
		store_pc_tf.setText(pc);

		String pc_id = "";

		for(int i = 0; !Character.isDigit(pc.charAt(i)); i++){
			pc_id = pc_id+pc.charAt(i);
		}

		File file = new File(pc_id+".csv");
		BufferedReader bufRdr = null;
		statusUpdate("Seeking post code");
		try{
			bufRdr  = new BufferedReader(new FileReader(file));
		}
		catch(Exception ex){
			System.out.println("File reading error: "+ex);
			JOptionPane.showMessageDialog(null, "Please make sure that the entered postcode is valid.", "Post Code Error", JOptionPane.ERROR_MESSAGE);
			statusUpdate("Post code error");
			return false;
		}
		if(bufRdr!=null){
			String line = null;
			try{
				While: while((line = bufRdr.readLine()) != null)
				{
					StringTokenizer st = new StringTokenizer(line,",");
					String[] values = new String[st.countTokens()];
					values[0] = st.nextToken();
					values[0] = values[0].replaceAll("\\s+","");
					values[0] = values[0].replaceAll("\"","");
					if(values[0].equalsIgnoreCase(pc)){
						for(int i = 1; i < st.countTokens(); i++){
							values[i] = st.nextToken();
						}
						statusUpdate("Parsing post code");
						GRStoWGS(Integer.parseInt(values[2]), Integer.parseInt(values[3]));
						break While;
					}
				}
			}
			catch(Exception ex){
				System.out.println("bufRdr.readLine() Exception: "+ex);
			}
		}
		return true;
	}

	void GRStoWGS(int easting, int northing){
		CRSAuthorityFactory crsFac =
				ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", null);
		try{
			CoordinateReferenceSystem wgs84crs = crsFac.createCoordinateReferenceSystem("4326");
			CoordinateReferenceSystem osgbCrs = crsFac.createCoordinateReferenceSystem("27700");

			CoordinateOperation op = new DefaultCoordinateOperationFactory().createOperation(osgbCrs, wgs84crs);

			//            DirectPosition eastNorth = new GeneralDirectPosition(533911, 160581);
			DirectPosition eastNorth = new GeneralDirectPosition(easting, northing);

			DirectPosition latLng = op.getMathTransform().transform(eastNorth, eastNorth);
			System.out.println("lat: " + latLng.getOrdinate(0));
			System.out.println("lng: " + latLng.getOrdinate(1));
			lat = latLng.getOrdinate(0);
			lon = latLng.getOrdinate(1);
			testLabel.setText(latLng.getOrdinate(0)+", "+latLng.getOrdinate(1));
		}
		catch(Exception ex){
			System.out.println("Exception: "+ex);
		}
	}

	private void getZones(String url_string){
		Document doc = null;
		HttpURLConnection urlConnection= null;
		try{
			URL sourceUrl = new URL(url_string);

			urlConnection=(HttpURLConnection)sourceUrl.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true); 
			urlConnection.setDoInput(true); 
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());
			doc.getDocumentElement().normalize();

			NodeList zone_node_list = doc.getElementsByTagName("shopping_zone"); 

			for(int i = 0; i < zone_node_list.getLength(); i++){
				Node node = zone_node_list.item(i);

				if(node.getNodeType()== Node.ELEMENT_NODE){
					Element element = (Element)node;
					try{
//						System.out.println("ID: "+Integer.parseInt(getTagValue("id", element)));
//						System.out.println("NAME: "+getTagValue("name", element));
//						System.out.println("ADDRESS: "+getTagValue("address", element));
//						System.out.println("LTD: "+Double.parseDouble(getTagValue("center_latitude", element)));
//						System.out.println("LND: "+Double.parseDouble(getTagValue("center_longitude", element)));
//						System.out.println("RAD: "+Integer.parseInt(getTagValue("radius", element)));

						Zone new_zone = new Zone(Integer.parseInt(getTagValue("id", element)),getTagValue("name", element), getTagValue("address", element), Double.parseDouble(getTagValue("center_latitude", element)), Double.parseDouble(getTagValue("center_longitude", element)), Integer.parseInt(getTagValue("radius", element)));
						zone_list.add(new_zone);
					}
					catch(Exception ex1){
						System.out.println("Could not enter a zone. :"+ex1);
					}
				}
			}
		}
		catch(Exception ex){
			System.out.println("Exception caught in the getZones: "+ex);
		}
		System.out.println("Zones: "+zone_list.size());
	}

	private void getStores(String url_string){
		System.out.println("Get Stores called");
		Document doc = null;
		HttpURLConnection urlConnection= null;
		try{
			URL sourceUrl = new URL(url_string);
			urlConnection=(HttpURLConnection)sourceUrl.openConnection();
			urlConnection.setRequestMethod("GET");
			
			urlConnection.setDoOutput(true); 
			urlConnection.setDoInput(true); 
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());
			doc.getDocumentElement().normalize();

			NodeList zone_node_list = doc.getElementsByTagName("store"); 

			for(int i = 0; i < zone_node_list.getLength(); i++){
				Node node = zone_node_list.item(i);

				if(node.getNodeType()== Node.ELEMENT_NODE){
					Element element = (Element)node;
					try{
						Store new_store = new Store(Integer.parseInt(getTagValue("id", element)), 0, getTagValue("name", element), getTagValue("postcode", element), getTagValue("address", element), Double.parseDouble(getTagValue("latitude", element)), Double.parseDouble(getTagValue("longitude", element)), Integer.parseInt(getTagValue("zone", element)));
						struct:{
							for(int j = 0; j < this.zone_list.size(); j++){
								if(zone_list.get(j).getId()== new_store.getZone()){
									zone_list.get(j).addStore(new_store);
									break struct;
								}
							}
							JOptionPane.showMessageDialog(null, "The store zone could not be found.", "Zone Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch(Exception ex1){
						System.out.println("Could not enter a zone. :"+ex1);
					}
				}
			}
		}
		catch(Exception ex){
			System.out.println("Exception caught in the getStores: "+ex);
		}
	}
	
	private void getCompanies(String url_string){
		System.out.println("Get Companies called");
		Document doc = null;
		HttpURLConnection urlConnection= null;
		try{
			URL sourceUrl = new URL(url_string);
			urlConnection=(HttpURLConnection)sourceUrl.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true); 
			urlConnection.setDoInput(true); 
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());
			doc.getDocumentElement().normalize();

			NodeList zone_list = doc.getElementsByTagName("company"); 

			for(int i = 0; i < zone_list.getLength(); i++){
				Node node = zone_list.item(i);

				if(node.getNodeType()== Node.ELEMENT_NODE){
					Element element = (Element)node;
					try{
						Company new_company = new Company(Integer.parseInt(getTagValue("id", element)), getTagValue("name", element), getTagValue("address", element), getTagValue("entered", element), getTagValue("telephone", element), getTagValue("email", element), getTagValue("contact_person", element), Boolean.parseBoolean(getTagValue("online", element)));
						company_list.add(new_company);
					}
					catch(Exception ex1){
						System.out.println("Company creation exception:"+ex1);
					}
				}
			}
		}
		catch(Exception ex){
			System.out.println("Exception caught in the getCompanies: "+ex);
		}
		System.out.println(company_list.size()+" companies.");
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}

	private void updateZone(Zone zone){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.svgonbatik.com/fcn/zone_update.php");
		String buffer, result = "";

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("id", zone.getId()+""));
			nameValuePairs.add(new BasicNameValuePair("zone_name", zone.getName()));
			nameValuePairs.add(new BasicNameValuePair("address", zone.getAddress()));
			nameValuePairs.add(new BasicNameValuePair("center_latitude", ""+zone.getLatitude()));
			nameValuePairs.add(new BasicNameValuePair("center_longitude", ""+zone.getLongitude()));
			nameValuePairs.add(new BasicNameValuePair("radius", ""+zone.getRadius()));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			while ((buffer = rd.readLine()) != null){
				result = result + buffer;
			}
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("RESULT: "+result);
	}

	public void zoneAssociationCreator(){
		Point[] points_array = new Point[zone_list.size()];
		Coordinate coord;
		for(int i = 0; i < zone_list.size(); i++){
			statusUpdate("Reading zone coordinate "+i);
			coord = new Coordinate(zone_list.get(i).getLatitude(), zone_list.get(i).getLongitude());
			points_array[i] = new Point(coord, new PrecisionModel(), 1);
			System.out.println("Point "+i+": "+zone_list.get(i).getLatitude()+", "+zone_list.get(i).getLongitude());
		}
		System.out.println("points_array has: "+points_array.length);
		System.out.println("Zones has: "+zone_list.size());
		String[] names = new String[zone_list.size()];
		for(int i = 0; i < zone_list.size(); i++){
			names[i] = zone_list.get(i).getName();
		}
		statusUpdate("Creating multipoint");
		MultiPoint multipoint = new MultiPoint(points_array, new GeometryFactory());
		DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		statusUpdate("Setting points to the dt builder");
		dtb.setSites(multipoint);
		statusUpdate("Getting geometry");
		Geometry triangle_collection = dtb.getTriangles(new GeometryFactory());
		System.out.println("Triangle collection has: "+triangle_collection.getNumGeometries());
		for(int x = 0; x < points_array.length; x++){
			statusUpdate("Checking point "+x);
			ArrayList<Integer> locations = new ArrayList<Integer>();
			for(int i = 0; i < triangle_collection.getNumGeometries(); i++){
				Geometry triangle = triangle_collection.getGeometryN(i);
				if(triangle.touches(points_array[x])){
					Coordinate[] coords = triangle.getCoordinates();
					for(int j = 0; j < points_array.length; j++){
						if(coords[0].equals2D(points_array[j].getCoordinate())){
							loop:{
							for(int h = 0; h < locations.size(); h++){
								if(locations.get(h) == zone_list.get(j).getId()){
									break loop;
								}
							}
							locations.add(zone_list.get(j).getId());									
						}
						}
						if(coords[1].equals2D(points_array[j].getCoordinate())){
							loop:{
							for(int h = 0; h < locations.size(); h++){
								if(locations.get(h) == zone_list.get(j).getId()){
									break loop;
								}
							}
							locations.add(zone_list.get(j).getId());									
						}
						}
						if(coords[2].equals2D(points_array[j].getCoordinate())){
							loop:{
							for(int h = 0; h < locations.size(); h++){
								if(locations.get(h) == zone_list.get(j).getId()){
									break loop;
								}
							}
							locations.add(zone_list.get(j).getId());									
						}
						}
					}
				}
			}
			int[] locations_array = new int[locations.size()];
			for(int g = 0; g < locations.size(); g++){
				locations_array[g] = locations.get(g);
			}
			System.out.println("\n");
			statusUpdate("Posting zone associations");
			this.postZoneAssociations(zone_list.get(x).getId(), locations_array);
		}
	}

	private void postZoneAssociations(int id, int[] associations){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.svgonbatik.com/fcn/bluestreet_zone_associations.php");
		String buffer, result = "";

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("id", id+""));
			for(int i = 0; i < associations.length; i++){
				nameValuePairs.add(new BasicNameValuePair("associations["+i+"]", associations[i]+""));
			}
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			statusUpdate("Waiting for server response..");
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			while ((buffer = rd.readLine()) != null){
				result = result + buffer;
			}
			System.out.println("Zone Associations result is: "+result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		statusUpdate("Server updating completed.");
	}

	private String postNewStore(String name, String address, String postcode, double lat, double lon, int zone, int company){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.svgonbatik.com/fcn/bluestreet_store_agent.php");
		String buffer, result = "";
		System.out.println("Posting zone id: "+zone);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("address", address));
			nameValuePairs.add(new BasicNameValuePair("postcode", postcode));
			nameValuePairs.add(new BasicNameValuePair("latitude", ""+lat));
			nameValuePairs.add(new BasicNameValuePair("longitude", ""+lon));
			nameValuePairs.add(new BasicNameValuePair("zone", ""+zone));
			nameValuePairs.add(new BasicNameValuePair("company", ""+company));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			while ((buffer = rd.readLine()) != null){
				result = result + buffer;
			}
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void postNewZone(String name, String address){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.svgonbatik.com/fcn/bluestreet_zone_agent.php");
		String buffer, result = "";
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("address", address));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			while ((buffer = rd.readLine()) != null){
				result = result + buffer;
			}
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		result = result.replaceAll("\\s+","");
		int zone_id = Integer.parseInt(result);
		Zone new_zone = new Zone(zone_id, name, address, 0, 0, 0);
		zone_list.add(new_zone);

		this.populateZones();
	}
	
	public void postNewCompany(Company company){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.bluestreet.co.uk/script/bluestreet_company_agent.php");
		String buffer, result = "";
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("name", company.getName()));
			nameValuePairs.add(new BasicNameValuePair("address", company.getAddress()));
			nameValuePairs.add(new BasicNameValuePair("entered", company.getEntered()));
			nameValuePairs.add(new BasicNameValuePair("logo", company.getLogo()));
			nameValuePairs.add(new BasicNameValuePair("telephone", company.getTelephone()));
			nameValuePairs.add(new BasicNameValuePair("email", company.getEmail()));
			nameValuePairs.add(new BasicNameValuePair("contact", company.getCperson()));
			nameValuePairs.add(new BasicNameValuePair("online", company.getStatus()+""));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			while ((buffer = rd.readLine()) != null){
				result = result + buffer;
			}
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		result = result.replaceAll("\\s+","");
		String logo_name = "company_"+result+"_logo.png";
		FTPClient ftp = new FTPClient(company.getLogo(), logo_name, "public_html/images/logos");
		ftp.putFile();
		
		int company_id = Integer.parseInt(result);
		company.setId(company_id);
/*		result = result.replaceAll("\\s+","");
		int zone_id = Integer.parseInt(result);
		Zone new_zone = new Zone(zone_id, name, address, 0, 0, 0);
		zones.add(new_zone);

		this.populateZones();*/
	}
	
	private void statusUpdate(String s){
		status_label.setText(ss+s);
		System.out.println(ss+s);
	}
	
	public void addStore(String company_name, String zone_name,String address, String post_code){

		statusUpdate("Verifying post code");
		boolean postcode_correct = seekPostCode(post_code);
		if(!postcode_correct){
			return;
		}

		Zone zone = null;
		statusUpdate("Seeking the zone");
		int company_id = 1000;
		for(int i = 0; i < company_list.size(); i++){
			if(company_list.get(i).getName()==company_name){
				company_id = company_list.get(i).getId();
			}
		}

		struct:{
			for(int i = 0; i < this.zone_list.size(); i++){
				statusUpdate("Checking zone "+i);
				if(zone_list.get(i).getName().equals(zone_name)){
					zone = zone_list.get(i);
					Store store = new Store(0, company_id, "new_store", post_code, address, lat, lon, zone.getId());
					statusUpdate("Adding store to the zone");
					zone.addNewStore(store);
					statusUpdate("Posting new store");
					postNewStore(store.getName(), store.getAddress(), store.getPostcode(), lat, lon, zone.getId(), company_id);
					statusUpdate("Updating zone");
					updateZone(zone);
					statusUpdate("Creating zone associations");
					this.zoneAssociationCreator();
					clearFields();
					break struct;
				}
			}
			JOptionPane.showMessageDialog(null, "The store is not entered.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void actionPerformed(ActionEvent ae){
		System.out.println("Action Coamnd: "+ae.getActionCommand());
		if(ae.getActionCommand().equals("addstore")){
			statusUpdate("Verifying post code");
			boolean postcode_correct = seekPostCode(store_pc_tf.getText());
			if(!postcode_correct){
				return;
			}
			
			String zone_name = zones_cb.getSelectedItem().toString();

			Zone zone = null;
			statusUpdate("Seeking the zone");
			struct:{
				for(int i = 0; i < this.zone_list.size(); i++){
					statusUpdate("Checking zone "+i);
					if(zone_list.get(i).getName().equals(zone_name)){
						zone = zone_list.get(i);
						Store store = new Store(0, 0, store_name_tf.getText(), store_pc_tf.getText(), store_address_tf.getText(), lat, lon, zone.getId());
						statusUpdate("Adding store to the zone");
						zone.addNewStore(store);
						statusUpdate("Posting new store");
						postNewStore(store_name_tf.getText(), store_address_tf.getText(), store_pc_tf.getText(), lat, lon, zone.getId(), 100);
						statusUpdate("Updating zone");
						updateZone(zone);
						statusUpdate("Creating zone associations");
						this.zoneAssociationCreator();
						clearFields();
						break struct;
					}
				}
				JOptionPane.showMessageDialog(null, "The store is not entered.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(ae.getActionCommand().equals("New Company")){
			displayCompanyEntry();
		}
		else if(ae.getActionCommand().equals("New Store")){
			displayStoreEntry();
		}
		else if(ae.getActionCommand().equals("New Promotion")){
			displayPromotionEntry();
		}
		else if(ae.getActionCommand().equals("New Category")){
			displayCategoryEntry();
		}
		else if(ae.getActionCommand().equals("Exit")){
			System.exit(0);
		}
		else if(ae.getActionCommand().equals("About")){
			JOptionPane.showMessageDialog(null, "This program is developed for the data entry of the BlueStreet application.\n Anjelloatoz@gmail.com", "BlueStreet", JOptionPane.INFORMATION_MESSAGE);
		}
		statusUpdate("Ready");
	}

	private void displayCompanyEntry(){
		companyEntryForm();
	}

	private void displayStoreEntry(){

	}

	private void displayPromotionEntry(){
		//promotionEntryForm();
	}

	public void companyEntryForm(){

		String name_label = "Company Name";
		String address_label = "Address";
		String pc_label = "Post Code";
		String phone_label = "Phone";
		String email_label = "E-Mail";
		String cperson_label = "Contact Person";

		JTextField name_tf = new JTextField();
		JTextArea address_ta = new JTextArea(2,3);
		JTextField pc_tf = new JTextField();
		JTextField phone_tf = new JTextField();
		JTextField email_tf = new JTextField();
		JTextField cperson_tf = new JTextField();

		int ans = JOptionPane.showOptionDialog(null, new Object[] {name_label, name_tf, address_label, address_ta, pc_label, pc_tf, phone_label, phone_tf, email_label, email_tf, cperson_label, cperson_tf}, "New Company",JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if(ans == 0){
			if(name_tf.getText().equals("")){
				JOptionPane.showMessageDialog(null, "The name field cant be left empty.\n Please enter the new child to proceed,\nor press NO button to cancell data entry.", "Required fields left empty", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void displayCategoryEntry(){

		JComboBox category_combo = new JComboBox();
		//    this.populateCategories(category_combo);

		JComboBox stores_combo = new JComboBox();

		String category_name_label = "Top Level";

		JTextField name = new JTextField();

		int ans = JOptionPane.showOptionDialog(null, new Object[] {category_combo, stores_combo, category_name_label, name}, "New Product Category",JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if(ans == 0){
			if(name.getText().equals("")){
				JOptionPane.showMessageDialog(null, "The name field cant be left empty.\n Please enter the new child to proceed,\nor press NO button to cancell data entry.", "Required fields left empty", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void promotionEntryForm(){
		JComboBox company_combo = new JComboBox();
		this.populateCompanies(company_combo);
		company_combo.addItem("Select Coompany");

		JComboBox stores_combo = new JComboBox();

		String name_lab = "Child name";
		String year_lab = "Year";
		String month_lab = "Month";
		String date_lab = "Date";

		JTextField name = new JTextField();

		int ans = JOptionPane.showOptionDialog(null, new Object[] {company_combo, stores_combo, name_lab, name, year_lab, month_lab, date_lab}, "New Child",JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if(ans == 0){
			if(name.getText().equals("")){
				JOptionPane.showMessageDialog(null, "The name field cant be left empty.\n Please enter the new child to proceed,\nor press NO button to cancell data entry.", "Required fields left empty", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private JMenuItem make(String name) {
		JMenuItem m = new JMenuItem(name);
		m.addActionListener(this);
		return m;
	}

	public static void main( String[] args )throws Exception{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		App app = new App();
		
	}
	
	/*	private void algoTest(){
	File file = new File("rm.csv");
	BufferedReader bufRdr = null;
	try{
		bufRdr  = new BufferedReader(new FileReader(file));
	}
	catch(Exception ex){
		JOptionPane.showMessageDialog(null, "Please make sure that the entered postcode is valid.", "Post Code Error", JOptionPane.ERROR_MESSAGE);
	}
	if(bufRdr!=null){
		String line = null;
		try{
			Coordinate coord = null;
			/*				while((line = bufRdr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line,",");
				String[] values = new String[st.countTokens()];
				values[0] = st.nextToken();
				values[0] = values[0].replaceAll("\\s+","");
				values[0] = values[0].replaceAll("\"","");
				for(int i = 1; i < st.countTokens(); i++){
					values[i] = st.nextToken();
				}
				GRStoWGS(Integer.parseInt(values[2]), Integer.parseInt(values[3]));
				coord = new Coordinate(lat, lon);
				points.add(new Point(coord, new PrecisionModel(), 1));
			}

			String[] names = {"Oval", "Vauxhall", "Kennington", "Elephant & castle", "Lambeth North", "Borough", "London Bridge", "Southwark", "Waterloo", "Towerhill", "Monument", "Aldgate", "Liverpool Street", "Mansion House", "St. Pauls", "Old Street", "Angel", "Kings Cross", "Holborn", "Chancery Lane", "Barbican", "Temple", "Embankment", "Charring Cross", "West Minster", "St. James Park", "Victoria", "Pimlico", "Hyde Park Corner", "Knights Bridge", "Sloan Square", "South Kensington", "Gloucester Road", "Earls Court", "Fulham Broadway", "West Kensington", "Barons Court", "Hammersmith", "Kensington", "Shephards Bush", "Goldhawk road", "Shephards Bush Market", "Holand Park", "Notinghill Gate", "Highstreet Kensington", "Queens way", "Lancaster Gate", "Bays Water", "Paddington", "Marble Arch", "Bond Street", "Oxford Circus", "Picadily Circus", "Covent Garden", "Tottenham Court Road", "Goodge Street", "Warren Street", "Great Portland Street", "Baker Street", "London Marylebone", "Edgeware Road", "Warwick Avenue", "Royal Oak", "Westbourne Park", "Ladbroke Grove", "Latimer Road", "Wood Lane"};

			coord = new Coordinate(51.48212320967214, -0.11196398721949663);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.4857310644921, -0.12419486032740679);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.48834992102972, -0.10595583902613726);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.49449562623386, -0.10063433634059038);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.4988238430679, -0.11235022531764116);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.501201515668996, -0.09329581247584429);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50571608066674, -0.08887553201930132);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.5039797624436, -0.1049258707644185);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50333864360594, -0.11513972269312944);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50993639370766, -0.07651591287867632);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.510684256508334, -0.08595728861109819);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51423643716165, -0.07561469064967241);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.5173877640517, -0.08355402933375444);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51209982034088, -0.09398245798365679);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.5149308160511, -0.09758734689967241);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.52585253723592, -0.087759733069106);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.53177961199715, -0.10535502420680132);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.530818517156334, -0.12295031534449663);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.517547995187385, -0.12046122537867632);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51824232359505, -0.1115777491213521);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.520298541788215, -0.09797358499781694);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51097805639266, -0.11423850046412554);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50702495374419, -0.12269282327906694);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50737219947446, -0.12728476511256304);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.501361803740785, -0.124838590490981);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.49957188827128, -0.13359332071559038);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.496365893811515, -0.14307761179225054);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.48909813812747, -0.1337649820925435);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50301808080464, -0.15247607218043413);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.501709092628964, -0.16050124155299272);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.492304643758544, -0.156424283850356);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.494094844626304, -0.1740624903322896);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.494308595255504, -0.18264555917994585);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.492037443569664, -0.19337439523951616);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.48043944640345, -0.19539141641871538);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.49046093057683, -0.2065923212649068);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.49051437258793, -0.21375918375269976);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.4923313636913, -0.22358679758326616);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.49791548595784, -0.2095534800173482);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50440716999093, -0.21882319437281694);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50200295039028, -0.2267196177126607);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50595648874456, -0.22633337961451616);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50718522133316, -0.2056481836916646);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.5091885186304, -0.19607806192652788);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.50104122703348, -0.19273066507594194);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51033703602048, -0.18715167032496538);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.511725902093936, -0.1754357813479146);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51250044362831, -0.1882674692751607);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.516613305616055, -0.1756932734133443);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.513408510036236, -0.1589562891604146);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.514316558342955, -0.14968657480494585);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51522458854861, -0.14187598215357866);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51006994153675, -0.1337649820925435);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51311472582624, -0.12445235239283647);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51642636540005, -0.130331754553481);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.52059227966605, -0.13432288156764116);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.52497141970672, -0.13831400858180132);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.523903375599936, -0.14423632608668413);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.52288871049459, -0.1571109293581685);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.52254158303134, -0.16324782358424272);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.520325245309934, -0.17011427866236772);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.523289238894925, -0.18380427347437944);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.519096867104736, -0.1886966227175435);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.52112634368102, -0.2010562418581685);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.51746787969001, -0.21032595621363726);
			points.add(new Point(coord, new PrecisionModel(), 1));
			coord = new Coordinate(51.513408510036236, -0.2177503107668599);
			points.add(new Point(coord, new PrecisionModel(), 1));

			Point[] points_array = new Point[points.size()];
			for(int i = 0; i < points.size(); i++){
				points_array[i] = points.get(i);
			}
			System.out.println("Points Array size: "+points_array.length);
			MultiPoint multipoint = new MultiPoint(points_array, new GeometryFactory());
			DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
			dtb.setSites(multipoint);
			Geometry triangle_collection = dtb.getTriangles(new GeometryFactory());
			for(int x = 0; x < points_array.length; x++){
				System.out.println(names[x]+" is covered by: ");
				ArrayList<Integer> locations = new ArrayList<Integer>();
				for(int i = 0; i < triangle_collection.getNumGeometries(); i++){
					Geometry triangle = triangle_collection.getGeometryN(i);
					if(triangle.touches(points_array[x])){
						Coordinate[] coords = triangle.getCoordinates();
						for(int j = 0; j < points_array.length; j++){
							if(coords[0].equals2D(points_array[j].getCoordinate())){

								loop:{
								for(int h = 0; h < locations.size(); h++){
									if(locations.get(h) == j){
										break loop;
									}
								}
								locations.add(j);
							}
							}
							if(coords[1].equals2D(points_array[j].getCoordinate())){
								loop:{
								for(int h = 0; h < locations.size(); h++){
									if(locations.get(h) == j){
										break loop;
									}
								}
								locations.add(j);
							}
							}
							if(coords[2].equals2D(points_array[j].getCoordinate())){
								loop:{
								for(int h = 0; h < locations.size(); h++){
									if(locations.get(h) == j){
										break loop;
									}
								}
								locations.add(j);
							}
							}
						}
					}
				}
				for(int g = 0; g < locations.size(); g++){
					System.out.print(names[locations.get(g)]+", ");						
				}
				System.out.println("\n");
			}
			Geometry aTriangle = triangle_collection.getGeometryN(0);
			for(int i = 0; i < aTriangle.getNumPoints(); i++){
				System.out.println("An X of the triangle 0: "+aTriangle.getCoordinates()[i].x);
			}
			//				System.out.println("Triangles: "+dtb.getTriangles(new GeometryFactory()).getNumGeometries());
			System.out.println("Triangles: "+triangle_collection.getNumGeometries());
		}
		catch(Exception ex){
			System.out.println("bufRdr.readLine() Exception: "+ex);
		}
	}
}*/
	
	class MyItemListener implements ItemListener {
	    public void itemStateChanged(ItemEvent evt) {
	        JComboBox cb = (JComboBox)evt.getSource();

	        int number = cb.getSelectedIndex();
	        
	        if(number!=0){
		        number_of_stores_label.setText(zone_list.get(number-1).getNumberOfStores()+"");
	        }
	    }
	}

}
