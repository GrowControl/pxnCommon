package com.poixson.commonjava.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;


public final class utilsString {
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	private utilsString() {}


	// string equals
	public static boolean strEquals(String a, String b) {
		if(a == null || a.isEmpty()) return false;
		if(b == null || b.isEmpty()) return false;
		return a.equals(b);
	}
	public static boolean strEqualsIgnoreCase(String a, String b) {
		if(a == null || a.isEmpty()) return false;
		if(b == null || b.isEmpty()) return false;
		return a.equalsIgnoreCase(b);
	}


	// trim from string
	public static String trim(String str, String data) {
		if(str  == null || str.isEmpty())  return null;
		if(data == null || data.isEmpty()) return null;
		int size = str.length();
		while(data.startsWith(str))
			data = data.substring(size);
		while(data.endsWith(str))
			data = data.substring(0, 0-size);
		return data;
	}


	// repeat string with deliminator
	public static String repeat(String delim, String str, int repeat) {
		if(delim == null || delim.isEmpty()) {
			StringBuilder out = new StringBuilder();
			for(int i=0; i<repeat; i++)
				out.append(str);
			return out.toString();
		}
		if(str == null || str.isEmpty()) return null;
		if(repeat < 1) return null;
		StringBuilder out = new StringBuilder();
		for(int i=0; i<repeat; i++) {
			if(out.length() > 0)
				out.append(delim);
			out.append(str);
		}
		return out.toString();
	}


	// exception to string
	public static String ExceptionToString(Throwable e) {
		if(e == null) return null;
		StringWriter writer = new StringWriter(256);
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString().trim();
	}


	// md5
	public static String MD5(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(str.getBytes());
		byte[] byteData = md.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xFF & byteData[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}


	// generate a random string
	public static String RandomString(int length) {
		if(length == 0) return "";
		if(length <  0) return null;
		String str = "";
		while(str.length() < length) {
			String s = UUID.randomUUID().toString();
			if(s == null) throw new NullPointerException();
			str += s;
		}
		return str.substring( 0, utilsMath.MinMax(length, 0, str.length()) );
	}


	// add strings with delimiter
//	public static String add(String baseString, String addThis, String delim) {
//		if(addThis.isEmpty())    return baseString;
//		if(baseString.isEmpty()) return addThis;
//		return baseString + delim + addThis;
//	}
	public static String add(String delim, String...addThis) {
		return addArray(null, addThis, delim);
	}
	public static String addList(String baseString, List<String> addThis, String delim) {
		return addArray(baseString, (String[]) addThis.toArray(new String[0]), delim);
	}
	public static String addArray(String baseString, String[] addThis, String delim) {
		if(baseString == null) baseString = "";
		if(delim == null || delim.isEmpty()) delim = null;
		StringBuilder string = new StringBuilder(baseString);
		for(String line : addThis) {
			if(line == null || line.isEmpty()) continue;
			if(string.length() != 0 && delim != null)
				string.append(delim);
			string.append(line);
		}
		return string.toString();
	}




}
