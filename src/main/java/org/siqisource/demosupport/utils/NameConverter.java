package org.siqisource.demosupport.utils;

public class NameConverter {

	public static String propertyToColumn(String property) {
		if (null == property) {
			return "";
		}
		char[] chars = property.toCharArray();
		StringBuffer field = new StringBuffer();
		for (char c : chars) {
			if (Character.isUpperCase(c)) {
				field.append("_");
			}
			field.append(Character.toLowerCase(c));
		}
		return field.toString().toUpperCase();
	}

	public static String columnToProperty(String column) {
		String[] words = column.toLowerCase().split("_");
		StringBuffer property = new StringBuffer();

		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (i == 0) {
				property.append(word);
			} else {
				property.append(Character.toUpperCase(word.charAt(0)));
				property.append(word.substring(1));
			}
		}
		return property.toString();
	}

}
