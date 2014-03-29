package com.parsing;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;


public class compare{

	public BufferedReader fbr;
    private String cache;
    private boolean empty;
    public String key="";
    //int doc_id=0;
    float tf;
    //StringBuilder type=new StringBuilder();
    String doc_id="";	
    
    int count=-1;
    public compare(BufferedReader r)
            throws IOException {
            this.fbr = r;
            reload();
            
    }

    public boolean empty() {
            return this.empty;
    }

    private void reload() throws IOException {
            try {
                    if ((this.cache = this.fbr.readLine()) == null) {
                            this.empty = true;
                            this.cache = null;
                    } else {
                            this.empty = false;
                    }
                    setVar();
            } catch (EOFException oef) {
                    this.empty = true;
                    this.cache = null;
            }
    }

    public void close() throws IOException {
            this.fbr.close();
    }

    public String peek() {
            if (empty())
                    return null;
            return this.cache.toString();
    }

    public String pop() throws IOException {
            String answer = peek();
            reload();
            return answer;
    }
   	public void setVar()
    {
    	if(this.cache!=null)
    	{
    		String[] parts = this.cache.split(":");
    		this.key=parts[0];
    		//String[] ref=parts[1].split("-");
    		this.doc_id=parts[1];
/*    		for(int i=0;i<parts[1].length();i++)
    		{
    			if(parts[1].charAt(i)>='0' && parts[1].charAt(i)<='9')
    				this.doc_id=this.doc_id*10+parts[1].charAt(i)-'0';
    			else
    				this.type.append(parts[1].charAt(i));
    		}*/
    		this.tf=Float.parseFloat(parts[2]);
    		//this.count=Integer.parseInt(ref[1]);
    	}
    }
    
    public String getDoc_id()
    {
    	return this.doc_id;
    }
    
    public String getKey()
    {
    	return this.key;
    }
    public int getCount()
    {
    	return this.count;
    }

}
