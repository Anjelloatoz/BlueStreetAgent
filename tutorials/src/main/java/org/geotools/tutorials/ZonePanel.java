package org.geotools.tutorials;

import javax.swing.*;

import java.awt.event.*;

public class ZonePanel extends JPanel implements ActionListener{
	
	App app;
	
	JLabel name_label;
	JTextField name_tf;
	
	JLabel address_label;
	JTextField address_tf;
	
	JButton add_zone_button;

	ZonePanel(App app){
		this.app = app;
		this.name_label = new JLabel("Name: ");
		this.name_tf = new JTextField(20);
		name_tf.setName("Zone name");
		
		this.address_label = new JLabel("Address: ");
		this.address_tf = new JTextField(20);
		address_tf.setName("Zone address");
		address_tf.setText("Address not entered");
		address_tf.setEditable(false);

		this.add_zone_button = new JButton("Add");
		
		Box base_box = new Box(BoxLayout.Y_AXIS);
		Box b1 = new Box(BoxLayout.X_AXIS);
		b1.add(name_label);
		b1.add(name_tf);
		
		Box b2 = new Box(BoxLayout.X_AXIS);
		b2.add(address_label);
		b2.add(address_tf);
		
		Box b3 = new Box(BoxLayout.X_AXIS);
		b3.add(add_zone_button);

		base_box.add(b1);
		base_box.add(b2);
		base_box.add(b3);

		add_zone_button.setActionCommand("addzone");
		add_zone_button.addActionListener(this);
		
		this.add(base_box);
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equals("addzone")){
			if(verifyText(name_tf)){
				if(verifyText(address_tf)){
					app.postNewZone(name_tf.getText(), address_tf.getText());
					name_tf.setText("");
				}
			}
		}
	}
	
	private boolean verifyText(JTextField tf){
		if(tf.getText().isEmpty()){
			JOptionPane.showMessageDialog(null, tf.getName()+" cannot be empty to proceed", "Empty"+tf.getName(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
}
