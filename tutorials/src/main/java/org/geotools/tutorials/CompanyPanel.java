package org.geotools.tutorials;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.border.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.geotools.tutorials.App.MyItemListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CompanyPanel extends JPanel implements ActionListener{
	App app;
	ArrayList<Company> company_list = new ArrayList<Company>();
	
	JPanel north_panel = new JPanel();
	JPanel east_panel = new JPanel();
	JPanel west_panel = new JPanel();
	JPanel south_panel = new JPanel();
	
	JLabel select_company_label = new JLabel("Select Company");
	JLabel status_label = new JLabel("");
	JComboBox company_combo = new JComboBox();
	ImageIcon edit_icon = new ImageIcon("icons/edit1.png");
	JButton edit_button = new JButton("Edit", edit_icon);
	ImageIcon new_icon = new ImageIcon("icons/new1.png");
	JButton new_button = new JButton("New", new_icon);
	
	JLabel name_label = new JLabel("Name");
	JLabel address_label = new JLabel("Address");
	JLabel telephone_label = new JLabel("Telephone");
	JLabel email_label = new JLabel("E Mail");
	JLabel contactperson_label = new JLabel("Contact Person");
	JLabel state_label = new JLabel("State");
	JComboBox state_combo = new JComboBox();
	JTextField name_tf = new JTextField();
	JTextArea address_ta = new JTextArea();
	JTextField telephone_tf = new JTextField();
	JTextField email_tf = new JTextField();
	JTextField contactperson_tf = new JTextField();

	JLabel logo_label = new JLabel("Logo");
	ImageIcon browse_icon = new ImageIcon("icons/browse1.png");
	JButton browse_button = new JButton("Browse", browse_icon);
	ImageIcon icon;
	JLabel icon_label = new JLabel();
	JPanel image_panel = new JPanel();
	JPanel bluestreet_logo_panel = new JPanel();
	
	ImageIcon clear_icon = new ImageIcon("icons/clear1.png");
	JButton clearfields_button = new JButton("Clear Fields", clear_icon);
	ImageIcon update_icon = new ImageIcon("icons/update1.png");
	JButton update_button = new JButton("Update", update_icon);
	ImageIcon add_icon = new ImageIcon("icons/add1.png");
	JButton add_button = new JButton("Add", add_icon);
	
	boolean online = false;
	String logo_file = "";
	
	CompanyPanel(App app, ArrayList<Company> company_list){
		this.app = app;
		this.company_list = company_list;
		this.setLayout(new BorderLayout());
		Box north_panel_top_box = new Box(BoxLayout.X_AXIS);
		north_panel_top_box.add(select_company_label);
		
		populateCompanies();
		company_combo.addItemListener(new MyItemListener());
		
		north_panel_top_box.add(company_combo);
		north_panel_top_box.add(edit_button);
		north_panel_top_box.add(new_button);
		new_button.setActionCommand("new");
		new_button.addActionListener(this);
		
		north_panel.add(north_panel_top_box);
		north_panel.add(status_label);
		
		this.add(north_panel, BorderLayout.NORTH);
		
		Box west_box = new Box(BoxLayout.Y_AXIS);
		TitledBorder company_info_border = new TitledBorder("Company Info");
		west_box.setBorder(company_info_border);

		Box name_box = new Box(BoxLayout.X_AXIS);
		name_tf.setPreferredSize(new Dimension(200, 25));
		name_tf.setMaximumSize(new Dimension(200, 23));
		name_tf.setMinimumSize(new Dimension(200, 23));
		
		name_box.add(name_label);
		name_box.add(Box.createHorizontalGlue());
		name_box.add(name_tf);
		
		west_box.add(name_box);
		west_box.add(Box.createRigidArea(new Dimension(20,10)));
//		west_box.setMinimumSize(new Dimension(400, 400));
		
		Box address_box = new Box(BoxLayout.X_AXIS);
		address_box.add(address_label);
		address_box.add(Box.createHorizontalGlue());
		address_ta.setMaximumSize(new Dimension(200, 69));
		address_ta.setMinimumSize(new Dimension(200, 69));
		address_ta.setPreferredSize(new Dimension(200, 69));
		address_ta.setLineWrap(true);
		address_ta.setWrapStyleWord(true);
		address_box.add(address_ta);
		west_box.add(address_box);
		west_box.add(Box.createRigidArea(new Dimension(20,10)));
		
		Box telephone_box = new Box(BoxLayout.X_AXIS);
		telephone_box.add(telephone_label);
		telephone_box.add(Box.createHorizontalGlue());
		telephone_tf.setMinimumSize(new Dimension(200, 23));
		telephone_tf.setMaximumSize(new Dimension(200, 23));
		telephone_tf.setPreferredSize(new Dimension(200, 25));
		telephone_box.add(telephone_tf);
		west_box.add(telephone_box);
		west_box.add(Box.createRigidArea(new Dimension(10,10)));
		
		Box email_box = new Box(BoxLayout.X_AXIS);
		email_box.add(email_label);
		email_box.add(Box.createHorizontalGlue());
		email_tf.setMaximumSize(new Dimension(200, 23));
		email_tf.setPreferredSize(new Dimension(200, 23));
		email_box.add(email_tf);
		west_box.add(email_box);
		west_box.add(Box.createRigidArea(new Dimension(10,10)));
		
		Box contactperson_box = new Box(BoxLayout.X_AXIS);
		contactperson_box.add(contactperson_label);
		contactperson_box.add(Box.createHorizontalGlue());
		contactperson_tf.setMaximumSize(new Dimension(200, 23));
		contactperson_tf.setPreferredSize(new Dimension(200, 23));
		contactperson_box.add(contactperson_tf);
		west_box.add(contactperson_box);
		west_box.add(Box.createRigidArea(new Dimension(10,10)));
		
		Box state_box = new Box(BoxLayout.X_AXIS);
		state_label.setMaximumSize(new Dimension(100, 23));
		state_box.add(state_label);
		state_box.add(Box.createHorizontalGlue());
		state_box.add(state_combo);
		
		state_combo.addItem("Online");
		state_combo.addItem("Offline");
		
		state_combo.setMaximumSize(new Dimension(100, 25));
		state_combo.setMinimumSize(new Dimension(100, 25));
		state_combo.setPreferredSize(new Dimension(100, 25));
		
		
		west_box.add(state_box);
		west_box.add(Box.createRigidArea(new Dimension(5,5)));
		
		this.add(west_box, BorderLayout.WEST);
		Box east_box = new Box(BoxLayout.Y_AXIS);
		
		
		image_panel.setPreferredSize(new Dimension(100,100));
		image_panel.add(icon_label);
		Box image_box = new Box(BoxLayout.Y_AXIS);
		TitledBorder company_logo_border = new TitledBorder("Company Logo");
		image_box.setBorder(company_logo_border);
		image_box.add(image_panel);
		image_box.add(browse_button);
		
		east_box.add(image_box);
		ImageIcon icon = new ImageIcon("BlueStreetlogo.png");
		JLabel label = new JLabel();
		label.setIcon(icon);
		bluestreet_logo_panel.setMinimumSize(new Dimension(300, 150));
		bluestreet_logo_panel.add(label);
		bluestreet_logo_panel.setPreferredSize(new Dimension(100,70));
		east_box.add(bluestreet_logo_panel);
		this.add(east_box, BorderLayout.CENTER);
		
		south_panel.add(clearfields_button);
		clearfields_button.setActionCommand("clear");
		clearfields_button.addActionListener(this);
		south_panel.add(update_button);
		south_panel.add(add_button);
		this.add(south_panel, BorderLayout.SOUTH);
		
		add_button.setActionCommand("add");
		add_button.addActionListener(this);
		
		browse_button.setActionCommand("browse");
		browse_button.addActionListener(this);
	}
	
	void populateCompanies(){
		company_combo.removeAllItems();
		company_combo.addItem("Select Company");
		for(int i = 0; i < company_list.size(); i++){
			company_combo.addItem(company_list.get(i).getName());
		}
//		System.out.println("Number of zones: "+zones.size());
	}
	
	private void setCompanyLogo(File f){
		icon = new ImageIcon(f.getAbsolutePath());
		logo_file = f.getAbsolutePath();
		icon_label.setIcon(icon);
	}

	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equals("add")){
			if(state_combo.getSelectedIndex()==0){
				online = true;
			}
			else{
				online = false;
			}
			if(logo_file.equals("")){
				JOptionPane.showMessageDialog(null, "A company must have a logo file \nPlease enter an image as the logo.", "No Logo entered", JOptionPane.ERROR_MESSAGE);
				return;
			}
			Company company = new Company(name_tf.getText(), address_ta.getText(), "Anjello", logo_file, telephone_tf.getText(), email_tf.getText(), contactperson_tf.getText(), online);
			app.postNewCompany(company);
			name_tf.setText("");
			address_ta.setText("");
			telephone_tf.setText("");
			email_tf.setText("");
			contactperson_tf.setText("");
			logo_file = "";
			this.icon_label.setIcon(null);
			company_list.add(company);
			this.populateCompanies();
//			getCompanies("http://www.bluestreet.co.uk/script/bluestreet_company_server_xml.php");
		}
		else if(ae.getActionCommand().equals("browse")){
			File f = openFile();
			if(f != null){
				setCompanyLogo(f);
			}
		}
		else if(ae.getActionCommand().equals("new")){
			allClear();
		}
		else if(ae.getActionCommand().equals("clear")){
			allClear();
		}
	}
	
	private void allClear(){
		name_tf.setText("");
		address_ta.setText("");
		telephone_tf.setText("");
		email_tf.setText("");
		contactperson_tf.setText("");
		logo_file = "";
		this.icon_label.setIcon(null);
		add_button.setEnabled(true);
	}
	
	private File openFile() {
	    FileFilter filter1 = new FileNameExtensionFilter("Portable Network Graphic (png)", "png");
	    FileFilter filter2 = new FileNameExtensionFilter("JPEG", "jpg");
	      JFileChooser jfc = new JFileChooser();
	      jfc.addChoosableFileFilter(filter1);
	      jfc.addChoosableFileFilter(filter2);

	      int result = jfc.showOpenDialog(this);
	      if(result == JFileChooser.CANCEL_OPTION) return null;
	      try {
	          return(jfc.getSelectedFile());
	      }

	      catch (Exception e) {
	         JOptionPane.showMessageDialog(this,e.getMessage(),
	         "File error",JOptionPane.ERROR_MESSAGE);
	      return null;
	      }
	}
	
	class MyItemListener implements ItemListener {
	    public void itemStateChanged(ItemEvent evt) {
	        JComboBox cb = (JComboBox)evt.getSource();

	        int number = cb.getSelectedIndex();
	        
	        if(number>0){
	        	add_button.setEnabled(false);
		        name_tf.setText(company_list.get(number-1).getName()+"");
		        address_ta.setText(company_list.get(number-1).getAddress()+"");
		        telephone_tf.setText(company_list.get(number-1).getTelephone()+"");
		        email_tf.setText(company_list.get(number-1).getEmail()+"");
		        contactperson_tf.setText(company_list.get(number-1).getCperson()+"");
		        setCompanyLogo("http://bluestreet.co.uk/images/logos/company_"+company_list.get(number-1).getId()+"_logo.png");
		        logo_file = "";
		        if(company_list.get(number-1).online){
		        	state_combo.setSelectedIndex(0);
		        }
		        else{
		        	state_combo.setSelectedIndex(1);
		        }
	        }
	    }
	}
	private void setCompanyLogo(String url){
		System.out.println(url);
		URL image_url = null;
		try{
			image_url = new URL(url);
		}
		catch(Exception ex){
			System.out.println("Image url exception: "+ex);
		}
		
		icon = new ImageIcon(image_url);
		icon_label.setIcon(icon);
	}
	
	private void getCompanies(String url_string){
		company_list.clear();
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
		populateCompanies();
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}
}
