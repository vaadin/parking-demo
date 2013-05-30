package com.vaadin.demo.parking.model;

import java.util.Collection;
import java.util.LinkedList;

public class ClassificationGroup extends ClassificationItem {
	private Collection<ClassificationItem> children = new LinkedList<ClassificationItem>();

	public ClassificationGroup(String latinName) {
		setName(latinName);
	}

	public Collection<ClassificationItem> getChildren() {
		return children ;
	}
	
	public void setChildren(Collection<ClassificationItem> children) {
		this.children = children;
	}
	
	public static ClassificationGroup AVES;
	
}
