package com.AudioToolkit.Utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import android.util.Log;

public class Mathf {

	private Mathf() {} //to avoid instantiation (ie. static class)

	//locale independant
	public static double str2dbl(String s) {

		NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
		
		try {			
			return nf.parse(s).doubleValue();
		} catch (Exception e) {
			Log.d("str2dlb","Could not parse '" + "'");
			return Double.MIN_VALUE;
		}
	}
	
	public static boolean testStr2Dbl(String s) {
		
		double val = str2dbl(s);
		
		if (val == Double.MIN_VALUE) return false;
		else return true;
		
	}
	
	public static String dbl2str(double d) {
		
		NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
		
		return nf.format(d);
		
	}
	
	public static double round(double d, int decimalPlaces) {
		
		//BigDecimal bd = new BigDecimal(d);
	   // BigDecimal rounded = bd.setScale(decimalPlaces, RoundingMode.HALF_UP );
	   // return rounded.doubleValue();
		
		
		
		
		
		return (double) Math.round(d * Math.pow(10,decimalPlaces)) / Math.pow(10,decimalPlaces);
		
	}
	
	public static double round(String s, int decimalPlaces) {
		
		return round(str2dbl(s), decimalPlaces);
	
	}
	
	public static String truncate(String s) {
		
		if (s.length() < 2) return s;
		
		if (s.endsWith(".0")) s = s.substring(0, s.length()-2);
	
		return s;
		
	}
		
	public static String truncate(double d) {
		return truncate(dbl2str(d)+"");
	}
	
	public static String truncate(double d, int decimals) {
		
		return truncate(round(d, decimals));
		
	}
	
	public static String truncate(String s, int decimals) {
		
		return truncate(round(s, decimals));
		
	}	
	
}
