package se.cag.test.soapui;

import java.net.URL;
import java.net.URLClassLoader;

public class tester {

	public static void main(String[] args) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
        	System.out.println(url.getFile());
        }
	}
	
	

}
