package edu.Brandeis.cs131.Common.JerryWang;

import java.util.HashSet;
import java.util.Set;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Industry;
import edu.Brandeis.cs131.Common.Abstract.Server;

public class BasicServer extends Server {
	protected int shared;
	protected int basic;
	protected Set<Industry> industries;
	
	public BasicServer(String name) {
		super(name);
		this.shared=0;
		this.basic=0;
		this.industries= new HashSet<Industry>();
	}

	@Override
	public synchronized boolean connectInner(Client client) {
		boolean isShared =client.getClass()==SharedClient.class;
		boolean checkBasic= this.basic==1;
		boolean checkShared = this.shared==2;		
		boolean checkBasicShared = (this.basic==1 && isShared) || (this.shared==1 && !isShared); 
		boolean checkIndustry = (this.industries.contains(client.getIndustry()));				
		boolean mustWait = checkBasic || checkShared || checkBasicShared || checkIndustry;
				
		if(mustWait){
			return false;
		}				
		if(isShared){		
				this.shared+=1;			
		}
		else{			
			this.basic=1;
		}		
		this.industries.add(client.getIndustry());	
		return true;
	}

	@Override
	public synchronized void disconnectInner(Client client) {
		if(client.getClass()==SharedClient.class){
			this.shared-=1;
		}
		else{
			this.basic=0;
		}
		this.industries.remove(client.getIndustry());
	}
}
