package org.geotools.tutorials;

import javax.swing.*;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.michaelbaranov.microba.calendar.*;
import com.michaelbaranov.microba.calendar.resource.*;
import com.michaelbaranov.microba.calendar.ui.*;

import java.text.SimpleDateFormat;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

public class PromotionPanel extends JPanel implements ActionListener, MouseListener{
	App app;
	ArrayList<Company> company_list = new ArrayList<Company>();
	ArrayList<Zone> zone_list = new ArrayList<Zone>();
	ArrayList<Store> store_list = new ArrayList<Store>();
	ArrayList<SalesCategory> sales_category_list = new ArrayList<SalesCategory>();

	JPanel north_panel = new JPanel();
	JPanel south_panel = new JPanel();

	JLabel select_company_label = new JLabel("Company");
	JComboBox company_combo = new JComboBox();

	JLabel main_title_label = new JLabel("Main Title");
	JLabel description_label = new JLabel("Description");
	JButton from_label = new JButton("From");
	JButton to_label = new JButton("To");
	JTextField main_title_tf = new JTextField();
	JTextField description_tf = new JTextField();
	JTextField from_tf = new JTextField();
	JTextField to_tf = new JTextField();

	JTextField calendar_target = from_tf;

	CalendarPane cp = new CalendarPane();

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
	String promotion_image_file = "";
	SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy");
	JTable store_table;
	
	JScrollPane scrollPane;
	Box store_table_box;
	Box east_box;
	Promotion promotion = new Promotion();
	
	JPopupMenu tree_popup_menu = new JPopupMenu();
	SalesCategoryNode selected_node;
	JTree tree;
	int selected_sales_category = 0;

	PromotionPanel(App app, ArrayList<Company> company_list, ArrayList<Zone> zone_list){
		this.app = app;
		this.company_list = company_list;
		this.zone_list = zone_list;
		this.setLayout(new BorderLayout());
		Box north_panel_top_box = new Box(BoxLayout.X_AXIS);
		north_panel_top_box.add(select_company_label);

		populateCompanies();
		company_combo.addItemListener(new companyComboListener());

		north_panel_top_box.add(company_combo);

		north_panel.add(north_panel_top_box);

		this.add(north_panel, BorderLayout.NORTH);

		Box west_box = new Box(BoxLayout.Y_AXIS);
		TitledBorder promotion_info_border = new TitledBorder("Promotion Info");
		west_box.setBorder(promotion_info_border);

		Box name_box = new Box(BoxLayout.X_AXIS);

		west_box.add(name_box);
		west_box.add(Box.createRigidArea(new Dimension(20,10)));

		Box main_title_box = new Box(BoxLayout.X_AXIS);
		main_title_box.add(main_title_label);
		main_title_box.add(Box.createHorizontalGlue());
		main_title_tf.setMaximumSize(new Dimension(200, 23));
		main_title_tf.setPreferredSize(new Dimension(200, 23));
		main_title_box.add(main_title_tf);
		west_box.add(main_title_box);
		west_box.add(Box.createRigidArea(new Dimension(20,10)));

		Box description_box = new Box(BoxLayout.X_AXIS);
		description_box.add(description_label);
		description_box.add(Box.createHorizontalGlue());
		description_tf.setMaximumSize(new Dimension(200, 23));
		description_tf.setPreferredSize(new Dimension(200, 23));
		description_box.add(description_tf);
		west_box.add(description_box);
		west_box.add(Box.createRigidArea(new Dimension(10,10)));

		Box from_box = new Box(BoxLayout.X_AXIS);
		from_box.add(from_label);
		from_box.add(Box.createHorizontalGlue());
		from_tf.setEditable(false);
		from_label.setActionCommand("from_text");
		from_label.addActionListener(this);
		from_tf.setMaximumSize(new Dimension(200, 23));
		from_tf.setPreferredSize(new Dimension(200, 23));
		from_box.add(from_tf);
		west_box.add(from_box);
		west_box.add(Box.createRigidArea(new Dimension(10,10)));

		Box to_box = new Box(BoxLayout.X_AXIS);
		to_box.add(to_label);
		to_box.add(Box.createHorizontalGlue());
		to_tf.setEditable(false);
		to_label.setActionCommand("to_text");
		to_label.addActionListener(this);
		to_tf.setMaximumSize(new Dimension(200, 23));
		to_tf.setPreferredSize(new Dimension(200, 23));
		to_box.add(to_tf);
		west_box.add(to_box);
		west_box.add(Box.createRigidArea(new Dimension(10,10)));

		Box calendar_box = new Box(BoxLayout.X_AXIS);

		cp.setEnabled(true);
		cp.setName("calendar");
		cp.addActionListener(this);
		calendar_box.add(cp);
		west_box.add(calendar_box);

		Box email_box = new Box(BoxLayout.X_AXIS);
		email_box.add(Box.createHorizontalGlue());
		west_box.add(email_box);
		west_box.add(Box.createRigidArea(new Dimension(10,10)));

		west_box.add(Box.createRigidArea(new Dimension(5,5)));
		
		this.add(setCategoryTree(), BorderLayout.WEST);

		west_box.setPreferredSize(new Dimension(250, 450));
		this.add(west_box, BorderLayout.CENTER);
		east_box = new Box(BoxLayout.Y_AXIS);

		image_panel.add(icon_label);
		Box image_box = new Box(BoxLayout.Y_AXIS);
		TitledBorder promotion_image_border = new TitledBorder("Promotion Image");
		image_box.setBorder(promotion_image_border);
		image_box.add(image_panel);
		image_box.add(browse_button);

		store_table_box = new Box(BoxLayout.X_AXIS);

		String columnNames[] = { "Store ID", "Post Code", "Set Promotion" };
		String dataValues[][] =
			{
				{ "", "", "" },
				{ "", "", "" },
				{ "", "", "" },
				{ "", "", "" }
			};

		store_table = new JTable(dataValues, columnNames);
//		store_table.setValueAt("Testing", 2, 2);
		
		store_table.addMouseListener(this);
		scrollPane = new JScrollPane(store_table);
		store_table_box.add(scrollPane);
		store_table_box.setPreferredSize(new Dimension(300, 200));

		east_box.add(image_box);
		east_box.add(store_table_box);
		ImageIcon icon = new ImageIcon("BlueStreetlogo.png");
		JLabel label = new JLabel();
		label.setIcon(icon);
		bluestreet_logo_panel.setMinimumSize(new Dimension(300, 150));
		bluestreet_logo_panel.add(label);
		bluestreet_logo_panel.setPreferredSize(new Dimension(100,70));
		east_box.add(bluestreet_logo_panel);
		east_box.setPreferredSize(new Dimension(300, 450));
		this.add(east_box, BorderLayout.EAST);

		south_panel.add(add_button);
		this.add(south_panel, BorderLayout.SOUTH);

		add_button.setActionCommand("add");
		add_button.addActionListener(this);

		browse_button.setActionCommand("browse");
		browse_button.addActionListener(this);
		browse_button.setEnabled(true);
		
		JMenuItem new_category_menu_item = new JMenuItem("Add new category");
		JMenuItem remove_category_menu_item = new JMenuItem("Remove category");
		remove_category_menu_item.setEnabled(false);
		new_category_menu_item.addActionListener(this);
		new_category_menu_item.setActionCommand("new_category");
		remove_category_menu_item.addActionListener(this);
		remove_category_menu_item.setActionCommand("remove_category");
		tree_popup_menu.add(new_category_menu_item);
		tree_popup_menu.addSeparator();
		tree_popup_menu.add(remove_category_menu_item);
	}
	
	private void getSalesCategories(String url_string){
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

			NodeList zone_node_list = doc.getElementsByTagName("salescategory"); 

			for(int i = 0; i < zone_node_list.getLength(); i++){
				Node node = zone_node_list.item(i);

				if(node.getNodeType()== Node.ELEMENT_NODE){
					Element element = (Element)node;
					try{
						SalesCategory new_sales_category = new SalesCategory(Integer.parseInt(getTagValue("id", element)), Integer.parseInt(getTagValue("parent_id", element)),getTagValue("name", element));
						this.sales_category_list.add(new_sales_category);
					}
					catch(Exception ex1){
						System.out.println("Could not create a sales category. :"+ex1);
					}
				}
			}
		}
		catch(Exception ex){
			System.out.println("Exception caught SalesCategory creator: "+ex);
		}
		System.out.println("Sales Categories: "+sales_category_list.size());
	}

	
	private JPanel setCategoryTree(){
		SalesCategoryNode root = new SalesCategoryNode();
		SalesCategory root_category = new SalesCategory(0, 0, "BlueStreet");
		root.setUserObject(root_category);
		root.setIconName("BlueStreet");
		this.sales_category_list.add(root_category);
		
		this.getSalesCategories("http://www.bluestreet.co.uk/script/bluestreet_salescategory_server.php");
		this.treePopulator(root);
        tree = new JTree(root);
        tree.setCellRenderer(new SalesTreeCellRenderer());
        tree.addTreeSelectionListener(new CategoryListener());

        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                try{
                    selected_node=(SalesCategoryNode)selPath.getLastPathComponent();
                    selected_sales_category = ((SalesCategory)selected_node.getUserObject()).getID();
                }
                catch(Exception ex){
//                	System.out.println("mose pressed out of the tree");
                }
                if(selRow != -1) {
                	if(e.isPopupTrigger()||SwingUtilities.isRightMouseButton(e)){
//                		System.out.println("Right clicked on tree");
                		tree_popup_menu.show(e.getComponent(), e.getX(), e.getY());
                	}
                    if(e.getClickCount() == 1) {
                    }
                    else if(e.getClickCount() == 2) {
                    }
                }
            }
        };
        tree.addMouseListener(ml);

        JScrollPane pane = new JScrollPane(tree);
        pane.setPreferredSize(new Dimension(170, 350));
        pane.setMaximumSize(new Dimension(170, 350));

        JPanel treePanel = new JPanel();
        treePanel.setPreferredSize(new Dimension(200,70));
        TitledBorder promotion_image_border = new TitledBorder("Sales Categories");
		treePanel.setBorder(promotion_image_border);
		treePanel.add(pane);
        return treePanel;
	}

	void setStoreTable(){
		String storeValues[][] = new String[store_list.size()][3];
		for(int i = 0; i < store_list.size(); i++){
			storeValues[i][0] = store_list.get(i).getID()+"";
			storeValues[i][1] = store_list.get(i).getPostcode()+"";
			storeValues[i][2] = "--";
		}
		
		String columnNames[] = { "Store ID", "Post Code", "Set Promotion" };
		store_table = new JTable(storeValues, columnNames);
		
		store_table.addMouseListener(this);
		scrollPane = new JScrollPane(store_table);
		store_table_box.removeAll();
		store_table_box.add(scrollPane);
		this.repaint();
		scrollPane.repaint();
		store_table_box.repaint();
		store_table.repaint();
		east_box.setVisible(true);
		
		this.setVisible(true);
	}
	
	void treePopulator(SalesCategoryNode node){
		System.out.println("Sent node: "+node.getIconName());
		SalesCategory current_sales_category = (SalesCategory)node.getUserObject();
		for(int i = 1; i < this.sales_category_list.size(); i++){
			if(sales_category_list.get(i).getParentId()==current_sales_category.getID()){
				SalesCategoryNode new_node = new SalesCategoryNode(sales_category_list.get(i));
				new_node.setIconName(sales_category_list.get(i).getName());
				node.add(new_node);
//				sales_category_list.get(i).setPath(new_node.getPath()+"");
				
//				sales_category_list.remove(i);
			}
		}
		for(int i = 0; i < node.getChildCount(); i++){
			treePopulator((SalesCategoryNode)node.getChildAt(i));
//			System.out.println(node.getChildAt(i));
		}
	}

	void populateCompanies(){
		company_combo.removeAllItems();
		company_combo.addItem("Select Company");
		for(int i = 0; i < company_list.size(); i++){
			company_combo.addItem(company_list.get(i).getName());
		}
	}
	
	private void setCompanyLogo(File f){
		icon = new ImageIcon(f.getAbsolutePath());
		promotion_image_file = f.getAbsolutePath();
		icon_label.setIcon(icon);
	}
	

	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equals("new_category")){
			JTextField category_name_field = new JTextField();
	        String f_nameString = "Enter the new category name";

	        int ans = JOptionPane.showOptionDialog(
	         null,
	         new Object[] {f_nameString,category_name_field},
	         "New Category",
	         JOptionPane.OK_OPTION,
	         JOptionPane.INFORMATION_MESSAGE,
	         null,
	         null,
	         null
	      );
	        if(ans == 0){
	        	SalesCategoryNode new_node = new SalesCategoryNode();
	        	SalesCategory new_category = new SalesCategory(((SalesCategory)selected_node.getUserObject()).getID(), category_name_field.getText());
	        	new_node.setUserObject(new_category);
	        	new_node.setIconName(new_category.getName());
	        	sales_category_list.add(new_category);
				selected_node.add(new_node);
				((DefaultTreeModel)(tree.getModel())).reload();
				postSalesCategory(new_category, new_node.getLevel());
				System.out.println("New nodes path count: "+new_node.getLevel());
	        }
		}
		else if(ae.getActionCommand().equals("add")){
			if(promotion_image_file.equals("")){
				JOptionPane.showMessageDialog(null, "Promotion must have an associated Image \nPlease enter an image.", "No Logo entered", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(main_title_tf.getText().equals("")||description_tf.getText().equals("")){
				JOptionPane.showMessageDialog(null, "Main title and the description must be entered.", "Empty text values", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(this.selected_sales_category==0){
				JOptionPane.showMessageDialog(null, "Please select a Sales Category from\nthe far left panel", "Invalid Sales Category", JOptionPane.ERROR_MESSAGE);
				return;
			}
			this.postPromotion();
			main_title_tf.setText("");
			description_tf.setText("");
			from_tf.setText("");
			to_tf.setText("");
			promotion.description = "";
			promotion.from_date = null;
			promotion.to_date = null;
			promotion.main_title = "";
			promotion_image_file = "";
			((DefaultTreeModel)(tree.getModel())).reload();
			selected_sales_category = 0;
			this.icon_label.setIcon(null);
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
		else if(ae.getActionCommand().equals("from_text")){
			from_tf.setText("Select the date");
			calendar_target = from_tf;
		}
		else if(ae.getActionCommand().equals("to_text")){
			to_tf.setText("Select the date");
			calendar_target = to_tf;
		}
		else{
			calendar_target.setText(formatter.format(cp.getDate()));
			if(calendar_target == to_tf){
				promotion.to_date = cp.getDate();
			}
			else{
				promotion.from_date = cp.getDate();
			}
		}
	}

	private void postSalesCategory(SalesCategory new_category, int level){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.bluestreet.co.uk/script/bluestreet_salescategory_agent.php");
		String buffer, result = "";

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("name", new_category.getName()));
			nameValuePairs.add(new BasicNameValuePair("parent_id", new_category.getParentId()+""));
			nameValuePairs.add(new BasicNameValuePair("level", level+""));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			while ((buffer = rd.readLine()) != null){
				result = result + buffer;
			}
			System.out.println("postSalesCategory result is: "+result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		result = result.replaceAll("\\s+","");
		new_category.setID(Integer.parseInt(result));
	}

	
	private void postPromotion(){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.bluestreet.co.uk/script/bluestreet_promotion_agent.php");
		String buffer, result = "";
		SimpleDateFormat mysql_date_format = new SimpleDateFormat("E, yyyy-mm-dd");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("company_id", promotion.company_ID+""));
			nameValuePairs.add(new BasicNameValuePair("sales_category_id", selected_sales_category+""));
			nameValuePairs.add(new BasicNameValuePair("main_title", main_title_tf.getText()));
			nameValuePairs.add(new BasicNameValuePair("description", description_tf.getText()));
			nameValuePairs.add(new BasicNameValuePair("from_date", mysql_date_format.format(promotion.from_date)));
			nameValuePairs.add(new BasicNameValuePair("to_date", mysql_date_format.format(promotion.to_date)));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			while ((buffer = rd.readLine()) != null){
				result = result + buffer;
			}
			System.out.println("postPromotion result is: "+result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		result = result.replaceAll("\\s+","");
		postPromotionAssociations(Integer.parseInt(result));
	}
	
	private void postPromotionAssociations(int id){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.bluestreet.co.uk/script/bluestreet_promotion_associations.php");
		String buffer, result = "";
		
		ArrayList<Integer> offer_stores = new ArrayList<Integer>();
		for(int i = 0; i < store_list.size(); i++){
			if(store_table.getValueAt(i, 2).equals("ENTERED")){
				offer_stores.add(Integer.parseInt(store_table.getValueAt(i, 0).toString()));
			}
		}

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("id", id+""));
			for(int i = 0; i < offer_stores.size(); i++){
				nameValuePairs.add(new BasicNameValuePair("associations["+i+"]", offer_stores.get(i)+""));
			}
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
		String logo_name = "promotion_"+id+"_logo.png";
		FTPClient ftp = new FTPClient(promotion_image_file, logo_name, "public_html/images/promotion_thumbnails");
		ftp.putFile();
	}
	
	public void mouseClicked(final MouseEvent evt) {
	    int row = store_table.getSelectedRow();
//	    System.out.println("Selected Row is: "+row);
	    if(store_table.getValueAt(row, 2).equals("--")){
		    store_table.setValueAt("ENTERED", row, 2);
	    }
	    else{
	    	store_table.setValueAt("--", row, 2);
	    }

    }

	public void mousePressed(final MouseEvent evt) {
	         }

	public void mouseReleased(final MouseEvent evt) {
	         }

	public void mouseEntered(final MouseEvent evt) {
	         }

	public void mouseExited(final MouseEvent evt) {
	         }

	private void allClear(){
		main_title_tf.setText("");
		description_tf.setText("");
		promotion_image_file = "";
		this.icon_label.setIcon(null);
		add_button.setEnabled(true);
	}
	
	private void getStores(int company_id){
		System.out.println("getStores called");
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost("http://www.svgonbatik.com/fcn/bluestreet_store_server_on_company_xml.php");
    	String result = "";

    	try {
    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("company_id", ""+company_id));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
 
			HttpResponse response = client.execute(post);
			InputStreamReader in = new InputStreamReader(response.getEntity().getContent());

			StringBuffer xml = new StringBuffer();
			int c =0;
			while( (c = in.read()) != -1){
                xml.append((char)c);
             }
			result = xml.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	System.out.println("Result is : "+result);
		
		Document doc = null;
    	try{
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		DocumentBuilder db = dbf.newDocumentBuilder();
    		ByteArrayInputStream stream = new ByteArrayInputStream(result.getBytes());

    		doc = db.parse(stream);
    		doc.getDocumentElement().normalize();

    		NodeList store_node_list = doc.getElementsByTagName("store");
    		
    		System.out.println("Number of tags: "+store_node_list.getLength());
			store_list.clear();
    		
    		for(int i = 0; i < store_node_list.getLength(); i++){
    			Node node = store_node_list.item(i);
    			
    			if(node.getNodeType()== Node.ELEMENT_NODE){
    				Element element = (Element)node;
    				try{
    					Store new_store = new Store(Integer.parseInt(getTagValue("id", element)), company_id, "name", getTagValue("postcode", element), "", 0, 0, 0);
    					store_list.add(new_store);
    				}
    				catch(Exception ex1){
    					System.out.println("Could not get the stores :"+ex1);
    				}
    			}
    		}
    		System.out.println("Store list has: "+store_list.size());
    	}
    	catch(Exception ex){
    		System.out.println("Exception caught in the getGreeting: "+ex);
    	}
    }
	
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
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
			System.out.println("combo Listener called");

			JComboBox cb = (JComboBox)evt.getSource();

			int number = cb.getSelectedIndex();

			if(number>0){
				int company_id = company_list.get(number-1).getId();
				promotion.company_ID = company_id;
				getStores(company_id);
				setStoreTable();
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
	
	class CategoryListener implements TreeSelectionListener{
		public void valueChanged(TreeSelectionEvent ew) {
            TreePath path = ew.getPath();                
            int pathCount = path.getPathCount();
            System.out.println("Path count: "+pathCount);

/*            for (int k = 0; k < pathCount; k++) {
                System.out.print(path.getPathComponent(k).toString());
                if (k + 1 != pathCount) {
                    System.out.print("|");
                }
            }
            System.out.println(path);*/
        }
	}
	
	class NewCategoryCreateListener implements ActionListener {
		TreePath path;
		String new_node_name;
		NewCategoryCreateListener(TreePath path){
			this.path = path;
			System.out.println("Path: "+path.toString());
		}
		public void actionPerformed(ActionEvent e) {
			new_node_name = JOptionPane.showInputDialog ( "Enter new category name." ); 
			DefaultMutableTreeNode parent_node=(DefaultMutableTreeNode)path.getLastPathComponent();
			
			DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(new_node_name);
			parent_node.add(new_node);
		}
		
	}

}
