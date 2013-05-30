package com.vaadin.demo.parking.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class Translations {
	
	private static Locale[] locales = new Locale[] {
		Locale.ENGLISH, new Locale("fi"), new Locale("sv")
	};

	public static Locale[] getAvailableLocales() {
		return locales;
	}
	
	public static ResourceBundle get(Locale locale) {
		if(locale == null) {
			locale = Locale.ENGLISH;
		}
		return ResourceBundle.getBundle("NamesBundle", locale);
	}

}
