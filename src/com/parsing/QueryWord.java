package com.parsing;

import java.util.ArrayList;

public class QueryWord {

	String query;
	long endOffset;
	String cat;//=new char[5];
	ArrayList<MapObject> arr;
	int sizeArr;
	int start=0;
	MultiThreaded mt;
	boolean flag=false;
	boolean isCategory=false;
	boolean isLeft=true;
	public QueryWord(String name) {
		query=name;
		arr=new ArrayList<MapObject>();
		mt=new MultiThreaded(query,this,true);
		mt.start();
		// TODO Auto-generated constructor stub
	}
	
	public void generate()
	{
		arr.clear();
		flag=false;
		sizeArr=0;
		start=0;
		mt=new MultiThreaded(query,this,false);
		mt.start();
	}
	public int getStart() {
		return start;
	}
	public void setStart(int Start) {
		this.start = Start;
	}
	public int getSizeArr() {
		//System.out.println(sizeArr);
		return sizeArr;
	}
	public void setSizeArr(int sizeArr) {
		this.sizeArr = sizeArr;
	}
	public long getEndOffset() {
		return endOffset;
	}
	public void setEndOffset(long endOffset) {
		this.endOffset = endOffset;
	}
	public boolean isFlag() {
	//	System.out.println("kitni baar");
		return flag;
	}
	public void setFlag(boolean flag) {
	//	System.out.println("h");
		this.flag = flag;
	}
	
}
