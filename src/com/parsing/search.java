package com.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.TreeMap;


class MultiThreaded extends Thread
{
	String query;
	RandomAccessFile raf;
	long start,end;
	QueryWord qw;
	boolean fullProcess;
	public MultiThreaded(String s,QueryWord q,boolean flag)
	{
		query=s;
		fullProcess=flag;
		//System.out.println(query);
		qw=q;
		//run();
	}
	
	public void run()
	{
		try
		{
			if(fullProcess)
			{
				char ch[] = new char[5];
				int c=0;
				String[] category = query.split(":");
				if(category.length>1)
				{
					for(int i=0;i<category[0].length();i++)
					{
						char a=category[0].charAt(i);
						if(a=='b' || a=='c' || a=='l' || a=='t' || a=='i'||a=='B' || a=='C' || a=='L' || a=='T' || a=='I')
							ch[c++]=a;
					}
					if(c>0)
					{
						qw.cat=new String(ch,0,c);
						qw.isCategory=true;
					}
					query=category[1];
					qw.query=query;
				}
				Stemmer stem = new Stemmer();
				query=search.isAlpha(query.toLowerCase());
				if(!parsing.isPresent(query) && query.length()>0)
				{
					stem.add(query);
					stem.stem();
					raf = new RandomAccessFile("index/"+query.charAt(0)+".txt","r");
					query=stem.toString();
				//	System.out.println(query);
					setOffsets();
				//	System.out.println(start+"   "+end);
					if(!startPostingList())
					{
						qw.isLeft=false;
					}
					raf.close();	
				//	System.out.print(search.result(args[0],stem.toString()));
				}
				else
					qw.isLeft=false;
				qw.setFlag(true);
			}
			else
			{
				getNext();
				qw.setFlag(true);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	public void getNext()
	{
		try
		{
			//System.out.println("wait");
			raf = new RandomAccessFile("index/"+query.charAt(0)+".txt", "r");
			raf.seek(qw.getEndOffset());
			byte[] key = new byte[20];
			raf.read(key,0,20);
			String s=new String(key);
			if(s.charAt(0)<'0' || s.charAt(0)>'9')
			{
				qw.isLeft=false;
				return;
			}
		//	System.out.println(s+"    "+s.indexOf('\n'));
			String[] parts = s.split(":");
		//	System.out.println(parts[0]);
			int offset=Integer.parseInt(parts[0]);
			int currLen=s.length()-(parts[0].length()+1);
			int remaining=0;
			StringBuilder sb = new StringBuilder();
			if(s.indexOf('\n')==-1)
				sb.append(s,parts[0].length()+1,s.length());
			else
			{
				sb.append(s,parts[0].length()+1,s.indexOf('\n'));
				qw.isLeft=false;
			}
			if(currLen<offset)
			{
				remaining=offset-currLen;
			}
			getPostingList(remaining,sb.toString());
			raf.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	
	public void setOffsets()
	{
		String ref = search.words.floorKey(query);
		try
		{
			if(ref==null || ref.charAt(0)!=query.charAt(0))
			{
				start=0;
			}
			else
				start = search.words.get(ref);
			ref=search.words.ceilingKey(query);
			if(ref==null || ref.charAt(0)!=query.charAt(0))
			{
				end=raf.length();
			}
			else 
				end = search.words.get(ref);
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	 public void getPostingList(int len,String stored)
	{
		try
		{
			if(len>0)
			{
				byte[] b2 = new byte[len];
				raf.read(b2,0, len);
				stored=stored+new String(b2);
				if(stored.indexOf('\n')!=-1)
				{
					qw.isLeft=false;
				}
				stored=stored.substring(0,stored.length()-1);
			}
			int count=0,catcounter=0,docIDLen=0;
			MapObject mo;
	//		System.out.println(query+"   "+stored);
			char[] cat = new char[5];
			String[] parts = stored.split(":");
		//	System.out.println(parts.length);
			for(int i=0;i<parts.length;i+=2)
			{
				mo=new MapObject();
				catcounter=0;
				docIDLen=0;
				for(int j=0;j<parts[i].length();j++)
				{
					char a=parts[i].charAt(j);
					if(a>='0' && a<='9')
						docIDLen++;
					else if(a!=':')
						cat[catcounter++]=parts[i].charAt(j);
				}
				mo.setWt(Integer.parseInt(parts[i+1]));
				mo.setCat(new String(cat,0,catcounter));
				mo.setDocid(parts[i].substring(0,docIDLen));
				qw.arr.add(mo);
				count++;
			}
			qw.setSizeArr(count);
			qw.setEndOffset(raf.getFilePointer());
		//	return stored;
		}
		catch (Exception e) {
			e.printStackTrace();
			//return null;
			// TODO: handle exception
		}
		
	}
	 
	 
	public boolean startPostingList()//throws Exception
		{
			try
			{
				long mid;
				int comparison;
				String stringKey;
				byte key[]=new byte[150];
				String arr[];
				int lengthTillOffset;
				int length;
				while(start<=end)
				{
					mid=(start+end)/2;
					raf.seek(mid);
					raf.readLine();
					if((raf.read(key, 0, 150))!=-1)
					{
						stringKey=new String(key);
						arr=stringKey.split(":");
						comparison=arr[0].compareTo(query);
						if(comparison==0)
						{
							
							if(arr.length>2)//means we got offset too
							{
								lengthTillOffset=arr[0].length()+2+arr[1].length();
								//System.out.println(stringKey.length());
								//arr[1]=arr[1].substring(0,arr[1].length()-1);
								int t=stringKey.length()-lengthTillOffset;
								StringBuilder ref=new StringBuilder();
								if(stringKey.indexOf('\n')==-1)
								{
									ref.append(stringKey,lengthTillOffset,stringKey.length());
								}
								else
								{
									ref.append(stringKey,lengthTillOffset,stringKey.indexOf('\n'));
									qw.isLeft=false;
								}
								//System.out.println(arr[1]+"          "+t);
								if(t<Integer.parseInt(arr[1]))
									length = Integer.parseInt(arr[1])-t;
								else
								{
									length=-1;
								}
								getPostingList(length,ref.toString());
								return true;
							}
							else//offest dint come
							{
								System.err.println("******invalid case offsets******");
								return false;
							}
							
						}
						else if(comparison<0)
						{
							start=mid+1;//indexReader.getFilePointer();
						}
						else
						{
							end=mid-1;
						}
					}
					else
					{
						end=mid-1;
					}
				}
				return false;
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
			
		}
 
	
}












public class search {
	
	 static int count=0,end;
	// ArrayList<String> directWords = new ArrayList<String>();
	// ArrayList<Long> offsets = new ArrayList<Long>();
	 static TreeMap <String,Long> words = new TreeMap<String,Long>();
	 static TreeMap<Integer,Long> title = new TreeMap<Integer, Long>();
	 static RandomAccessFile[] raf = new RandomAccessFile[26];
	public static  void load_off(File f)
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			String[] parts;
			while((line=br.readLine())!=null)
			{
				parts=line.split(":");
				words.put(parts[0],Long.parseLong(parts[1]));
				//directWords.add(parts[0]);
				//offsets.add(Long.parseLong(parts[1]));
				end++;
				//offs[count++]=Long.parseLong(line);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 public static void docIDTitle(File f)
	 {
		 try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;
				String[] parts;
				while((line=br.readLine())!=null)
				{
					parts=line.split(":");
					title.put(Integer.parseInt(parts[0]),Long.parseLong(parts[1]));
					//directWords.add(parts[0]);
					//offsets.add(Long.parseLong(parts[1]));
					end++;
					//offs[count++]=Long.parseLong(line);
				}
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	
	 public static String getTitle(RandomAccessFile raf,String docId)
	 {
		 long start,end;
		 int doc= Integer.parseInt(docId);
		 Integer s;
		 try
		 {
			 s=title.floorKey(doc);
			 if(s!=null)
				 start=title.get(s);
			 else
				 start=0;
			 s=title.ceilingKey(doc);
			 if(s==null)
				 end=raf.length();
			 else
				end=title.get(s);
		//	 raf.seek(start);
		//	 System.out.println(raf.readLine());
		//	 raf.seek(end);
		//	 System.out.println(raf.readLine());
			 String res=null;
			 long mid;
			 while(start<=end)
				{
				  	mid=(start+end)/2;
			//	  	System.out.println(start+"       "+end+"          "+mid);
				  	raf.seek(mid);
					raf.readLine();
					if((res=raf.readLine())!=null)
					{
				//		System.out.println(res);
						String[] arr=res.split(":");
						int comparison=Integer.parseInt(arr[0]);//.compareTo(docId);
						if(comparison==doc)
						{
							return res.substring(arr[0].length()+1,res.length());
						}
						else if(comparison<doc)
						{
							start=mid+1;//indexReader.getFilePointer();
						}
						else
						{
							end=mid-1;
						}
					}
					else
					{
						end=mid-1;
					}
				}
		 }
		 catch (Exception e) {
			// TODO: handle exception
		}
		return ""; 
	 }
/*	static  void getAddress(RandomAccessFile raf,String query)
	{
		String ref = words.floorKey(query);
		long start,end;
		try
		{
			if(ref==null || ref.charAt(0)!=ref.charAt(0))
			{
				start=0;
			}
			else
				start = words.get(ref);
			ref=words.ceilingKey(query);
			if(ref==null || ref.charAt(0)!=ref.charAt(0))
			{
				end=raf.length();
			}
			else 
				end = words.get(ref);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		/*try
		{
			while(first<last)
			{
				mid=(first+last)/2;
				r=directWords.get(mid).compareTo(query);
				if(r==0)
				{
					setFirst(offsets.get(r));
					setLast(offsets.get(r));
					flag=true;
					break;
					//return r;
				}
				else if(r>0)
				{
					last=mid-1;
				}
				else
				{
					first=mid+1;
				}
			}
			if(!flag)
			{
				if((first==0 && directWords.get(0).compareTo(query)>0)||(directWords.get(first-1).charAt(0)!=query.charAt(0)))
				{
					setFirst(0);
				}
				else 
				{
					setFirst(offsets.get(first-1));
				}
				if((directWords.get(last).compareTo(query)<0) ||(directWords.get(last).charAt(0)!=query.charAt(0)))
				{
					setLast(raf.length());
				}
				else
					setLast(offsets.get(last));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	public  String result(String folder,String query)
	{
		String res=null;
		try {
				String[] parts;
				int ind=query.charAt(0)-'a';
				getAddress(raf[ind],query);
				long start=getFirst(),limit=getLast(),curr;
				while(start<limit)
				{
					curr=(start+limit)/2;
					//System.out.println(start+"   "+limit+"   "+curr);
					raf[ind].seek(curr);
					raf[ind].readLine();
					if((res=raf[ind].readLine())!=null)
					{
						parts = res.split(":");
						int ref=parts[0].compareTo(query);
						if(ref==0)
						{
							return parts[1];
						}
						else if(ref>0)
							limit=curr-1;
						else
							start=curr+1;
					}
					else 
						limit=curr-1;
				}
				return "";
	//		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			////e.printStackTrace();
			return "";
		}
		
		
	}*/
	
		
	

	 
	public static String isAlpha(String name) {
			int len=name.length(),counter=0;
			char[] ch=new char[len];
			char c;
			for(int i=0;i<len;i++)
			{
				c=name.charAt(i);
				if(c>='a' && c<='z')
					ch[counter++]=c;
			}
		    return new String(ch,0,counter);
		}
}
