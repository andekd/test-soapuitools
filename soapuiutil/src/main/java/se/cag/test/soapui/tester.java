package se.cag.test.soapui;

import java.net.URL;
import java.net.URLClassLoader;
import se.cag.test.soapui.DataDrivenTestsUtil;

public class tester {

	public static void main(String[] args) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
        	System.out.println(url.getFile());
        }
    	DataDrivenTestsUtil t1 = new DataDrivenTestsUtil();
    	System.out.println(t1.getExecDir());
    	System.out.println(t1.getCompleteFileName());
    	t1.printScript();  
    	//System.out.println(t1.getScript());
    	
    	
	}	
}
