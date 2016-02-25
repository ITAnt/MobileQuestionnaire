package com.itant.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Validator {
	
	/**
	 * 判断是否手机号码
	 * @param input
	 * @return
	 */
	public static boolean isMobile(String input){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");   
		Matcher m = p.matcher(input);
		return m.matches();
	}
	
	/**
	 * 判断是否为电子邮件地址
	 * @param input
	 * @return
	 */
	public static boolean isEmail(String input){
		String str="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);     
        Matcher m = p.matcher(input);
        return m.matches();     
	}

}
