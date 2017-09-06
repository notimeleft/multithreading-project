package edu.Brandeis.cs131.Common.JerryWang;

import edu.Brandeis.cs131.Common.Abstract.Industry;


public class BasicClient extends MyClient {
	public BasicClient(String name, Industry industry){
		super(name, industry);
	}
	
	@Override
    public String toString() {
        return String.format("%s BASIC %s", getIndustry(), getName());
    }
}
