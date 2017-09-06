package edu.Brandeis.cs131.Common.JerryWang;

import java.util.Random;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Industry;


public abstract class MyClient extends Client {

    public MyClient(String name, Industry industry) {  	      		
    	super(name, industry, randomSpeed(), 3);
    }
    
    public static int randomSpeed(){
    	Random rn = new Random();
		return rn.nextInt(10);
    }
   
    
}
