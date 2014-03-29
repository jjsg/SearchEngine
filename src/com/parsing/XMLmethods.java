package com.parsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLmethods extends DefaultHandler {
	PrintWriter pw;
	File f;
	String srcPath;
	PrintWriter log;
	BufferedWriter bw2;
	RandomAccessFile bw;
	int maxDoc=500;
	int count=0;
	long sizeTitle=0;
	long maxAllowed = 10737418240L;//22548578304L;
	static int noDocuments=0;
	HashMap<Character,Integer> subIndex;
	TreeMap<String,HashMap<Character,Integer>> index = new TreeMap<String,HashMap<Character,Integer>>();
	Stemmer stem= new Stemmer();
	StringBuilder text= new StringBuilder();
	String type="";
	static int noOfFileCount=1;
	//boolean infoBox=false,category=false,links=false;
	//String inBody="";
	int max=0;
	public XMLmethods(String path)
	{
		try {
			srcPath=path;
			f = new File(path+"/index1.txt");
			pw = new PrintWriter(f);
			bw = new RandomAccessFile("index/titles.txt","rw");
			bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("index/reftitles.txt"))));
			log = new PrintWriter("log.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(log);
			//e.printStackTrace();
		}
	}
	public void startDocument(){
		System.out.println("Start");
	}
	public void endDocument(){
		try
		{
			//System.out.println("abc");
			pw.close();
			bw.close();
			bw2.close();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("\nend");
	}
	public void startElement(String uri, String localName, String qName,Attributes attributes){
		try
		{
			type=qName;
		}
		catch (Exception e) {
			e.printStackTrace(log);
		}
		//System.out.println("<"+qName+">");
	}
	public void endElement(String uri, String localName, String qName){
		//if(p!=null)
			//p.add("</"+qName+">");
		try
		{
			if(type.equals("text"))
			{
				divideText();
				text.setLength(0);
			}
			type="";
			if(qName.equals("page"))
			{
				//System.out.println("aaya kya"+index.size());
				page.process(pw,index);
				page.title=null;
				page.id=null;
				index.clear();
				noDocuments++;
				if(f.length()>maxAllowed)
				{
					pw.close();
					f = new File(srcPath+"/index"+(++noOfFileCount)+".txt");
					pw = new PrintWriter(f);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace(log);
		}
		//System.out.print("</"+qName+">");
	}
	
	public void parseAndSave(String s,char cat)
	{
		String stemmedString;
		if(!parsing.isPresent(s))
		{
			stem.add(s);
			stem.stem();
			stemmedString=stem.toString();
			//System.out.println(stemmedString+"   "+cat);
			if(index.containsKey(stemmedString))
			{
				subIndex = index.get(stemmedString);
				int count=1;
				if(subIndex.containsKey(cat))
				{
					count=subIndex.get(cat);
					count++;
				}
				subIndex.put(cat,count);
				
				/*String old_value=index.get(stemmed_String);
				String[] parts=old_value.split("-");
				int count=Integer.parseInt(parts[1])+1;
				//value=value+(Integer.parseInt(parts[1])+1)
				index.put(stemmed_String,parts[0]+"-"+count);*/
			}
			else
			{
				subIndex= new HashMap<Character, Integer>();
				subIndex.put(cat,1);
				//index.put(stemmed_String,page.id+"-"+1);
			}
			index.put(stemmedString,subIndex);
		}
	}
	
	
	public void divideText()
	{
		//V V V V IMP
		//http://www.drillio.com/en/software-development/java/removing-accents-diacritics-in-any-language/
		if(!Normalizer.isNormalized(text,Form.NFD))
		{
			text = new StringBuilder(Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));
		}
		
		
		//comments|==...==|<dd>|Image
		//text=new StringBuilder(Pattern.compile("<(\\s)*!(\\s)*-(\\s)*-(\\s)*(.)*?-(\\s)*-(\\s)*>|=(\\s)*=(\\s)*(.)*?=(\\s)*=|<(.)*?>|File:(.)*?\\|").matcher(text).replaceAll(""));
		//text=new StringBuilder(Pattern.compile("==(.)*==").matcher(text).replaceAll(""));
		int start=0,length=text.length();
		char temp[] =  new char[length];
		int counter=0;
		char c;
		StringBuilder s1=new StringBuilder();
		char subType[] = new char[length];
		int j,refCounter=0;
		for(int i=start;i<length;i++)
		{
			char ch=Character.toLowerCase(text.charAt(i));
			if(ch=='f')
			{
				j=i;
				refCounter=0;
				//boolean flag;
				for(j=i;j<length;j++)
				{
					c=Character.toLowerCase(text.charAt(j));
					if(c==':' || (c>='a' && c<='z'))
					{
						subType[refCounter++]=c;
					}
					if(refCounter==5)
						break;
				}
				s1.append(subType, 0,refCounter);
				if(s1.toString().equals("file:"))
				{
					refCounter=0;
					//flag=false;
					for(i=j;i<length;i++)
					{
						c=Character.toLowerCase(text.charAt(i));
						if(c=='|')
							break;
					}
				}
				else
					temp[counter++]=ch;
				s1.setLength(0);
			}
			else if(ch=='<')
			{
				// Bigger One
				j=i;
				refCounter=0;
				//boolean flag;
				subType[refCounter++]=ch;
				for(j=i+1;j<length;j++)
				{
					c=Character.toLowerCase(text.charAt(j));
					if(c>='a' && c<='z')
					{
						subType[refCounter++]=c;
					}
					if(c=='>')
					{
						subType[refCounter++]=c;
						break;
					}
				}
				s1.append(subType, 0,refCounter);
				i=j;
				//String s1 = new String(subType,0,refCounter);
				if(s1.toString().equals("<ref>") || s1.toString().equals("<gallery>"))
				{
					refCounter=0;
					for(i=j;i<length;i++)
					{
						c=text.charAt(i);
						if(c=='<')
							break;
					}
				}
				s1.setLength(0);
			}
			else if(ch=='=')
			{
				j=i;
				refCounter=0;
				subType[refCounter++]=ch;
				//boolean flag;
				for(j=i+1;j<length;j++)
				{
					c=text.charAt(j);
					if(c=='=')
					{
						subType[refCounter++]=c;
					}
					else if(c!=' ')
						break;
				}
				int count;
				if(refCounter>1)
				{
					refCounter=0;
					count=0;
					for(i=j;i<length;i++)
					{
						c=text.charAt(i);
						if(c=='=')
							count++;
						if(count==refCounter)
								break;
					}
				}
				//s1.setLength(0);
			}
			else if(ch=='!')
			{
				j=i;
				refCounter=0;
				subType[refCounter++]=ch;
				//boolean flag;
				for(j=i;j<length;j++)
				{
					c=text.charAt(j);
					if(c=='-')
					{
						subType[refCounter++]=c;
					}
					else if(c!=' ')
						break;	
				}
				int count;
				s1.append(subType, 0,refCounter);
				//String s1 = new String(subType,0,refCounter);
				if(s1.equals("!--"))
				{
					refCounter=0;
					//flag=false;
					count=0;
					for(i=j;i<length;i++)
					{
						c=text.charAt(i);
						if(c=='-')
							count++;
						if(count==2)
								break;
					}
				}
				s1.setLength(0);
			}
			else if(ch=='{')
			{
				j=i;
				refCounter=0;
				//boolean flag;
				for(j=i;j<length;j++)
				{
					c=Character.toLowerCase(text.charAt(j));
					if(c=='{' || (c>='a' && c<='z'))
					{
						subType[refCounter++]=c;
					}
					if(refCounter==9)
						break;
				}
				int count=2;
				s1.append(subType, 0,refCounter);
				//String s1 = new String(subType,0,refCounter);
				if(s1.toString().equals("{{infobox"))
				{
					refCounter=0;
					//flag=false;
					for(i=j;i<(start+length);i++)
					{
						c=Character.toLowerCase(text.charAt(i));
						if(c=='{')
							count++;
						if(c=='}')
						{
							//System.out.println(count);
							count--;
							if(count==0)
								break;
						}
						if(c>='a' && c<='z')
							subType[refCounter++]=c;
						else
						{
							String s = new String(subType,0,refCounter);
							if(refCounter>1)
								 parseAndSave(s, 'i');
							refCounter=0;
						}
						//if(flag && c!=' ' && c!='}')
							//flag=false;
					}
					//infoBox=true;
					//i=infoSpecific(ch,j, length-j);
				}
				s1.setLength(0);
			}
			else if(ch=='[')
			{
				j=i;
				refCounter=0;
				for(j=i;j<(start+length);j++)
				{
					c=Character.toLowerCase(text.charAt(j));
					if(c==':' || c=='[' || (c>='a' && c<='z'))
					{
						subType[refCounter++]=c;
					}
					if(refCounter==11)
						break;
				}
				int count;
				s1.append(subType, 0,refCounter);
				//System.out.println(s1+"  ");
				//String s1=new String(subType,0,refCounter);
				if(s1.toString().equals("[[category:"))
				{
					//flag=false;
					count=2;
					refCounter=0;
					for(i=j;i<(start+length);i++)
					{
						c=Character.toLowerCase(text.charAt(i));
						if(c=='[')
							count++;
						if(c==']')
						{
							count--;
							if(count==0)
								break;
							/*if(flag)
								break;
							else
								flag=true;*/
						}
						if(c>='a' && c<='z')
							subType[refCounter++]=c;
						else
						{
							String s = new String(subType,0,refCounter);
							if(refCounter>1)
								parseAndSave(s, 'c');
							refCounter=0;
						}
						//if(flag && c!=' ' && c!=']')
							//flag=false;
						/*if(c==']' ||(c>='a' && c<='z') || c==' ')
							page.category.append(c);
						if(page.category.charAt(i)==']' && page.category.charAt(i-1)==']')
							break;*/
					}
					//category=true;
					//i=categorySpecific(ch,j, length-j);
				}
				else if(s1.length()>5 && s1.substring(0,6).equals("[http:"))
				{
					count=1;
					//links=true;
					refCounter=0;
					for(i=j-5;i<(start+length);i++)
					{
						c=Character.toLowerCase(text.charAt(i));
						if(c=='[')
							count++;
						if(c==']')
						{
							count--;
							if(count==0)
								break;
						}
						if(c>='a' && c<='z')
							subType[refCounter++]=c;
						else
						{
							String s = new String(subType,0,refCounter);
							if(refCounter>1)
								parseAndSave(s, 'l');
							refCounter=0;
						}
					/*	if(c==']' || (c>='a' && c<='z') || c==' ')
							page.externalLinks.append(c);
						if(page.externalLinks.charAt(i)==']')
							break;*/
					}
					
				}
				s1.setLength(0);
				
			}
			else if(ch>='a' && ch<='z')
			{
				temp[counter++]=ch;
			}
			if(counter>0 && (ch<'a' || ch>'z'))
			{
				String s = new String(temp,0,counter);
				if(counter>2)
					parseAndSave(s, 'b');
				counter=0;
			}
			
		//System.out.println(ch[i]);
		}
		if(counter!=0)
		{
			String s = new String(temp,0,counter);
			if(counter>2)
			{
				parseAndSave(s,'b');
				//System.out.println(s);
				/*stem.add(s);
				stem.stem();
				stemmed_String=stem.toString();
				if(index.containsKey(stemmed_String))
				{
					String old_value=index.get(stemmed_String);
					String[] parts=old_value.split("-");
					int count=Integer.parseInt(parts[1])+1;
					//value=value+(Integer.parseInt(parts[1])+1)
					index.put(stemmed_String,parts[0]+"-"+count);
				}
				else
				{
					//index.put(stemmed_String,page.id+"-"+1);
					index.put(stemmed_String,page.id);
				}
				*/
			}
		}
	}
	
	
	
/*	public int infoSpecific(char[] ch,int start,int length)
	{
		int i,counter=0;
		char temp[] =  new char[length];
		for(i=start;i<(start+length);i++)
		{
			ch[i]=Character.toLowerCase(ch[i]);
			if(ch[i]=='}' && ch[i+1]=='}')
			{
				infoBox=false;
				String s = new String(temp,0,counter);
				if(counter>1)
					parseAndSave(s, 'i');
				counter=0;
				return i+2;
			}
			if(ch[i]>='a' && ch[i]<='z')
			{
				temp[counter++]=ch[i];
			}
			else if(counter>0)
			{
				String s = new String(temp,0,counter);
				if(counter>1)
					parseAndSave(s, 'i');
				counter=0;
			}
		}
		return i;
	}
	
	
	public int categorySpecific(char[] ch,int start,int length)
	{
		int i,counter=0;
		char temp[] =  new char[length];
		for(i=start;i<(start+length);i++)
		{
			ch[i]=Character.toLowerCase(ch[i]);
			if(ch[i]==']' && ch[i+1]==']')
			{
				category=false;
				String s = new String(temp,0,counter);
				if(counter>1)
					parseAndSave(s, 'c');
				counter=0;
				return i+2;
			}
			if(ch[i]>='a' && ch[i]<='z')
			{
				temp[counter++]=ch[i];
			}
			else if(counter>0)
			{
				String s = new String(temp,0,counter);
				if(counter>1)
					parseAndSave(s, 'c');
				counter=0;
			}
		}
		return i;
	}
	

	public int linkSpecific(char[] ch,int start,int length)
	{
		int i,counter=0;
		char temp[] =  new char[length];
		for(i=start;i<(start+length);i++)
		{
			ch[i]=Character.toLowerCase(ch[i]);
			if(ch[i]==']')
			{
				links=false;
				String s = new String(temp,0,counter);
				if(counter>1)
					parseAndSave(s, 'l');
				counter=0;
				return i+2;
			}
			if(ch[i]>='a' && ch[i]<='z')
			{
				temp[counter++]=ch[i];
			}
			else if(counter>0)
			{
				String s = new String(temp,0,counter);
				if(counter>1)
					parseAndSave(s, 'l');
				counter=0;
			}
		}
		return i;
	}
	
	*/
	public void characters(char[] ch,int start,int length)
	{
		try
		{
			//System.out.println(type + "    "+page.id);
			if(type.equals("title"))
			{
				String s1 = new String(ch,start,length);
				int counter=0;
				char temp[] =  new char[length];
				for(int i=start;i<(start+length);i++)
				{
					ch[i]=Character.toLowerCase(ch[i]);
					if(ch[i]>='a' && ch[i]<='z')
					{
						temp[counter++]=ch[i];
					}
					else if(counter>0)
					{
						String s = new String(temp,0,counter);
						if(counter>1)
							parseAndSave(s, 't');
						counter=0;
					}
				}
				if(counter!=0)
				{
					String s = new String(temp,0,counter);
					if(counter>1)
						parseAndSave(s, 't');
				}
				page.title=s1;
			}
			else if(type.equals("text"))
			{
				
			text.append(ch, start,length);
			}
			else if(type.equals("id") && page.id==null)
			{
				String s = new String(ch, start, length);
				page.id=s;
				if(count==maxDoc)
				{
					bw2.write(s+":"+bw.getFilePointer()+"\n");
					count=0;
				}
				//System.out.println(page.id.length()+"  "+page.title.length()+"   ");
				if(!Normalizer.isNormalized(page.title,Form.NFD))
				{
					page.title = new StringBuilder(Normalizer.normalize(page.title, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")).toString();
				}
				
				String s1=page.id+":"+page.title+"\n";
				bw.writeBytes(s1);
				//sizeTitle+=s1.length();
				//System.out.println(sizeTitle);
				count++;
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace(log);
			//System.exit(1);
		}
	}
	
}
