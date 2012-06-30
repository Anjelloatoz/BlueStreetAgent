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

public class StorePanel extends JPanel implements ActionListener{
	App app;
	ArrayList<Company> company_list = new ArrayList<Company>();
	ArrayList<Zone> zone_list = new ArrayList<Zone>();
	
	JPanel north_panel = new JPanel();
	JPanel south_panel = new JPanel();
	
	JLabel select_company_label = new JLabel("Company");
	JLabel select_zone_label = new JLabel("Zone");
	JComboBox company_combo = new JComboBox();
	JComboBox zone_combo = new JComboBox();
	
	JLabel address_label = new JLabel("Address");
	JLabel postcode_label = new JLabel("Postcode");
	JLabel contactperson_label = new JLabel("Contact Person");
	JTextArea address_ta = new JTextArea();
	JTextField postcode_tf = new JTextField();
	JTextField contactperson_tf = new JTextField();

	JLabel logo_label = new JLabel("Logo");
	ImageIcon browse_icon = new ImageIcon("icons/browse1.png");
	JButton browse_button = new JButton("Browse", browse_icon);
	ImageIcon icon;
	JLabel icon_label = new JLabel();
	JPanel image_panel = new JPanel();
	JPanel bluestreet_logo_panel = new JPanel();
	
	ImageIcon add_icon = new ImageIcon("icons/add1.png");
	JButton add_button = new JButton("Add", add_icon);
	
	boolean online = false;
	String logo_file = "";
	
	StorePanel(App app, ArrayList<Company> company_list, ArrayList<Zone> zone_list){
		this.app = app;
		this.company_list = company_list;
		this.zone_list = zone_list;
		this.setLayout(new BorderLayout());
		Box north_panel_top_box = new Box(BoxLayout.X_AXIS);
		north_panel_top_box.add(select_company_label);
		
		populateCompanies();
		populateZones();
		company_combo.addItemListener(new companyComboListener());
		zone_combo.addItemListener(new zoneComboListener());
		
		north_panel_top_box.add(company_combo);

		north_panel_top_box.add(select_zone_label);
		north_panel_top_box.add(zone_combo);

		north_panel.add(north_panel_top_box);
		
		this.add(north_panel, BorderLayout.NORTH);
		
		Box west_box = new Box(BoxLayout.Y_AXIS);
		TitledBorder store_info_border = new TitledBorder("Store Info");
		west_box.setBorder(store_info_border);

		Box name_box = new Box(BoxLayout.X_AXIS);
		
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
		
		Box postcode_box = new Box(BoxLayout.X_AXIS);
		postcode_box.add(postcode_label);
		postcode_box.add(Box.createHorizontalGlue());
		postcode_tf.setMaximumSize(new Dimension(200, 23));
		postcode_tf.setPreferredSize(new Dimension(200, 23));
		postcode_box.add(postcode_tf);
		west_box.add(postcode_box);
		west_box.add(Box.createRigidArea(new Dimension(10,10)));
		
		Box email_box = new Box(BoxLayout.X_AXIS);
		email_box.add(Box.createHorizontalGlue());
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
		state_box.add(Box.createHorizontalGlue());
		
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
		
		south_panel.add(add_button);
		this.add(south_panel, BorderLayout.SOUTH);
		
		add_button.setActionCommand("add");
		add_button.addActionListener(this);
		
		browse_button.setActionCommand("browse");
		browse_button.addActionListener(this);
		browse_button.setEnabled(false);
	}
	
	void populateCompanies(){
		company_combo.removeAllItems();
		company_combo.addItem("Select Company");
		for(int i = 0; i < company_list.size(); i++){
			company_combo.addItem(company_list.get(i).getName());
		}
//		System.out.println("Number of zones: "+zones.size());
	}
	
	void populateZones(){
		zone_combo.removeAllItems();
		zone_combo.addItem("Select Zone");
		for(int i = 0; i < zone_list.size(); i++){
			zone_combo.addItem(zone_list.get(i).getName());
		}
		System.out.println("Number of zones: "+zone_list.size());
	}
	
	private void setCompanyLogo(File f){
		icon = new ImageIcon(f.getAbsolutePath());
		logo_file = f.getAbsolutePath();
		icon_label.setIcon(icon);
	}

	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equals("add")){
			if(company_combo.getSelectedIndex() == 0 || zone_combo.getSelectedIndex() == 0){
				JOptionPane.showMessageDialog(this,"You must enter a Company and a Zone \nfor the new store.",
				         "Company or Zone not selected",JOptionPane.ERROR_MESSAGE);
			}
			else{
				app.addStore(company_combo.getSelectedItem().toString(), zone_combo.getSelectedItem().toString(), address_ta.getText(), postcode_tf.getText());
			}
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
		address_ta.setText("");
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
	
	class companyComboListener implements ItemListener {
	    public void itemStateChanged(ItemEvent evt) {
	        JComboBox cb = (JComboBox)evt.getSource();

	        int number = cb.getSelectedIndex();
	        
	        if(number>0){
	        }
	    }
	}
	
	class zoneComboListener implements ItemListener {
	    public void itemStateChanged(ItemEvent evt) {
	        JComboBox cb = (JComboBox)evt.getSource();

	        int number = cb.getSelectedIndex();
	        
	        if(number>0){
	        }
	    }
	}
}
