/******************************************************************************
	Copyright 2016
	Jeffrey Hamilton
	Tim Goering
	
	This file implements a basic comparator used for sorting words by their
	frequencies.
 *****************************************************************************/

import java.util.Comparator;
import java.util.Map;

public class WordComparator implements Comparator<Map.Entry<String, Integer>>{
	public int compare(Map.Entry<String, Integer> g1, Map.Entry<String, Integer> g2){
		Integer h1 = g1.getValue();
		Integer h2 = g2.getValue();
		return h2.compareTo(h1);
	}
}
