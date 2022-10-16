package commands;

import java.util.ArrayList;
import java.util.Collection;

public class ArgumentsCollection extends ArrayList<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ArgumentsCollection (String ... args) {
		super();
		for (String el : args) {
			add(el);
		}
	}
	
	public ArgumentsCollection (Collection<String> args) {
		super();
		if (args != null) {
			for (String arg : args) {
				add(arg);
			}
		}
	}
	
	
	
	@Override
	public boolean add (String element) {
		if (element != null) element = element.toLowerCase().replace(' ', '_');
		return super.add(element);
	}
	
	
	
	@Override
	public ArgumentsCollection clone () {
		ArgumentsCollection col = new ArgumentsCollection(this);
		return col;
	}
	
	
	
	@Override
	public String get (int index) {
		return get(index, null);
	}
	
	
	public String get(int index, String defaultValue) {
		try {
			return super.get(index);
		} catch (IndexOutOfBoundsException exc) {
			return defaultValue;
		}
	}
	
	
	public int get(int index, int defaultValue, int onNumberFormatError) {
		String res = get(index, String.valueOf(defaultValue));
		try {
			return Integer.valueOf(res);
		} catch (NumberFormatException exc) {
			return onNumberFormatError;
		}
	}

}
