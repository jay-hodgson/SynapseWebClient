package org.sagebionetworks.web.unitclient.utils;

import java.util.ArrayList;

public class ArrayListUtil {
	public static final ArrayList EMPTY_LIST = new ArrayList();
	
	public static <T> ArrayList<T> singletonList(T o) {
		ArrayList<T> list = new ArrayList<T>();
		list.add(o);
        return list;
    }
	
	public static <T> ArrayList<T> asList(T... a) {
		ArrayList<T> list = new ArrayList<T>();
		for (T t : a) {
			list.add(t);
		}
        return list;
    }
}
