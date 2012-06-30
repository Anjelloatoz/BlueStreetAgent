package org.geotools.tutorials;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.Icon;

class SalesCategoryNode extends DefaultMutableTreeNode {

	protected Icon icon;

	protected String iconName;

	public SalesCategoryNode() {
		this(null);
	}

	public SalesCategoryNode(Object userObject) {
		this(userObject, true, null);
	}

	public SalesCategoryNode(Object userObject, boolean allowsChildren, Icon icon) {
		super(userObject, allowsChildren);
		this.icon = icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getIconName(){
		if (iconName != null){
			return iconName;
		}
		else{
			String str = userObject.toString();
			int index = str.lastIndexOf(".");
			if (index != -1) {
				return str.substring(++index);
			}
			else{
				return null;
				}
			}
		}
	public void setIconName(String name) {
		iconName = name;
	}
}