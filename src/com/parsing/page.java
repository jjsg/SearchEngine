package com.parsing;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.TreeMap;

public class page {
	static String title=null;
	static String id=null;
	static HashMap<Character,Integer> size = new HashMap<Character, Integer>();
	static HashMap<Character,Integer> reference = new HashMap<Character, Integer>();
	static
	{
		size.put('t',7);
		size.put('b',2);
		size.put('c',1);
		size.put('i',1);
		size.put('l',1);
	}
	
	public static void process(PrintWriter pw,TreeMap<String,HashMap<Character,Integer>> index )
	{
		for(String s: index.keySet())
		{
			//me = (Map.Entry<String,HashMap<Character,Integer>>)i.next();
			int total=0;
			reference = index.get(s);//.getValue();
			//System.out.print(s+":"+id+"-");
			pw.write(s+":"+id);
			for(char c: reference.keySet())
			{
				total+=reference.get(c)*size.get(c);
				pw.write(c);
			}
			pw.write(":"+(double)total/index.size());
			//System.out.println();
			pw.write("\n");
			//mapped_details.append(me.getKey()+":"+me.getValue()+"\n");
		}
		
	}
	
}
