package org.geotools.tutorials;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.ImageIcon;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

class SalesTreeCellRenderer extends DefaultTreeCellRenderer {
	ImageIcon projectIcon;
	ImageIcon patternIcon;
	ImageIcon elementIcon;
	ImageIcon frontIcon;
	ImageIcon rearIcon;
	ImageIcon containerIcon;
	ImageIcon saleIcon;
	
	public SalesTreeCellRenderer() {
		projectIcon = new ImageIcon("project_icon.gif");
		patternIcon = new ImageIcon("pattern_object_icon.gif");
		elementIcon = new ImageIcon("element_icon.gif");
		frontIcon = new ImageIcon("front_view_icon.gif");
		rearIcon = new ImageIcon("rear_view_icon.gif");
		containerIcon = new ImageIcon("container_icon.gif");
		saleIcon = new ImageIcon("icons/sale_icon.png");
		}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,boolean expanded,boolean leaf, int row, boolean hasFocus){
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		Object nodeObj = ((SalesCategoryNode)value).getUserObject();
 // check whatever you need to on the node user object

		SalesCategory sales_category = null;
		sales_category = (SalesCategory)nodeObj;
		setIcon(saleIcon);
		setText(sales_category.getName());
		
		return this;
	}
}