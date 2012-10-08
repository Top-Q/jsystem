/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.util.LinkedHashSet;

public class GroupsManager {
	private static GroupsManager manager;
	public static GroupsManager getInstance(){
		if(manager == null){
			manager = new GroupsManager();
		}
		return manager;
	}

	private LinkedHashSet<String> groups = new LinkedHashSet<String>();
	private GroupsManager(){
	}
	
	public void addGroup(String groupName){
		if(!groups.contains(groupName)){
			groups.add(groupName);
		}
	}
	
	public void reset(){
		groups.clear();
	}
	
	public String[] getGroups(){
		String[] array = groups.toArray(new String[groups.size()]);
		String[] groups = new String[array.length + 1];
		groups[0] = "";
		System.arraycopy(array, 0, groups, 1, array.length);
		return groups;
	}
	
	
	
	
}
