package edu.Brandeis.cs131.Common.JerryWang;

import edu.Brandeis.cs131.Common.Abstract.Industry;
import edu.Brandeis.cs131.Common.Abstract.Log.Log;

public class SharedClient extends MyClient {
	public SharedClient(String name, Industry industry){
		super(name, industry);
	}
	@Override
    public String toString() {
        return String.format("%s SHARED %s", getIndustry(), getName());
    }
}
