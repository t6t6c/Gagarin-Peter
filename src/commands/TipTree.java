package commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TipTree {
	private HashMap<String, HashSet<String>> branches = new HashMap<>();

	public TipTree() {
		
	}
	
	
	public void addBranch(ArgumentsCollection col, List<String> tips) {
		String path = createPath(col);
		if (!branches.containsKey(path)) branches.put(path, new HashSet<String>());
		branches.get(path).addAll(tips);
		if (col.size() > 0) {
			ArgumentsCollection newCol = col.clone();
			String tip = newCol.remove(newCol.size()-1);
			addBranch(newCol, tip);
		}
	}

	
	public void addBranch(ArgumentsCollection col, String ... tips) {
		ArrayList<String> list = new ArrayList<>();
		for (String tip : tips) {
			list.add(tip);
		}
		addBranch(col, list);
	}
	
	
	
	public String createPath(ArgumentsCollection col) {
		String path = "";
		for (int i = 0; i < col.size(); i++) {
			if (i > 0) path += ",";
			path += col.get(i);
		}
		return path;
	}
	
	
	public HashMap<String, HashSet<String>> getBranches() {
		return branches;
	}
	
	
	public HashSet<String> getTips(ArgumentsCollection collection) {
		ArgumentsCollection col = collection.clone();
		if (col.size() > 0) {
			String el = col.remove(col.size() - 1);
			try {
				HashSet<String> res = new HashSet<String>(branches.get(createPath(col)));
				for (String s : new HashSet<String>(res)) {
					if (!s.contains(el)) res.remove(s);
				}
				return res;
			} catch (NullPointerException exc) {
				return null;
			}
		}
		return branches.get("");
	}

}
