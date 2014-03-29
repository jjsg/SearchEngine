package com.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class query {

	static ArrayList<Map<String,Integer>> arrList;
	static Map<String,Integer> perCount;
	static HashMap<String,String> titleCache = new HashMap<String, String>();
	static String line;
	static PrintWriter pw;
	public static void main(String args[])
	{
		
		search.load_off(new File("index/level2.txt"));
		search.docIDTitle(new File("index/reftitles.txt"));
		try {
			pw = new PrintWriter("a.txt");
			//long start = System.currentTimeMillis();;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			RandomAccessFile raf = new RandomAccessFile("index/titles.txt","r");
			line=raf.readLine();
			int i,len,counter=1;
			boolean flag=false;
			String[] parts;
			parts=line.split(":");
			titleCache.put(parts[0],parts[1]);
			//3009744
			//29187972
			//System.out.println(search.getTitle(raf, "1327258"));
			line=br.readLine();
			while(line!=null)
			{
				long start = System.currentTimeMillis();;
				parts=line.split(" ");
				i=0;
				arrList = new ArrayList<Map<String,Integer>>();
				len=parts.length;
				QueryWord[] q = new QueryWord[len];
				for(String s:parts)
				{
					q[i] = new QueryWord(s);
					i++;
					perCount = new HashMap<String, Integer>();
					arrList.add(perCount);
				}
				for(i=0;i<len;i++)
				{
				//	while(q[i].isFlag()!=true);
					q[i].mt.join();
					if(q[i].isCategory)
						add(q[i],q[i].cat);
					else
						add(q[i],"");
					
				}
				boolean someLeft=true;
				//Scanner sc = new Scanner(System.in);
				while(true)
				{
					if(arrList.size()==len && arrList.get(len-1).size()>=10)
					{
						perCount = arrList.get(len-1);
						perCount = sortWeights(perCount);
						counter=0;
						System.out.println(line+"-");
						for(String s: perCount.keySet())
						{
							if(titleCache.containsKey(s))
								System.out.println(s+":"+titleCache.get(s));
							else
							{
								String r=search.getTitle(raf,s);
								System.out.println(s+":"+r);
								titleCache.put(s,r);
							}
							counter++;
							if(counter==10)
								break;
						}
						flag=true;
						break;
					}
					else
					{
						if(counter>10)
						{
							someLeft=false;
							//System.out.println("hii");
							for(i=0;i<len;i++)
							{
								if(q[i].isLeft)
								{
									someLeft=true;
									//System.out.println("hiii");
									q[i].generate();
								}
							}
							counter=0;
							for(i=0;i<len;i++)
								q[i].mt.join();
						}
						for(i=0;i<len;i++)
						{
							if(q[i].sizeArr!=q[i].start)
							{
								if(q[i].isCategory)
									add(q[i],q[i].cat);
								else
									add(q[i],"");
							}
							
						}
					}
					//int a = sc.nextInt();
					
					counter++;
					if(!someLeft)
						break;
				}
				if(!flag)
				{
					counter=0;
					System.out.println(line+"-");
					int temp=arrList.size();
					for(i=temp-1;i>=0;i--)
					{
						perCount = arrList.get(i);
						perCount = sortWeights(perCount);
						for(String s: perCount.keySet())
						{
							if(titleCache.containsKey(s))
								System.out.println(s+":"+titleCache.get(s));
							else
							{
								String r=search.getTitle(raf,s);
								System.out.println(s+":"+r);
								titleCache.put(s, r);
							}
							counter++;
							if(counter==10)
								break;
						}
						if(counter==10)
							break;
					}
				}
				System.out.println();
				long end = System.currentTimeMillis();;
			    System.out.println((end-start)+" ms");
				arrList=null;
			    line=br.readLine();
			}
			pw.close();
			//long end = System.currentTimeMillis();;
		    //System.out.println((end-start)+" ms");
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String[] mysplit(String s)
	{
		ArrayList<String> arr = new ArrayList<String>();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)!=' ')
				sb.append(s.charAt(i));
			else
			{
				if(sb.length()>0)
				{
					arr.add(sb.toString());
				}
				sb.setLength(0);
			}
		}
		return null;
	}
	
	public static void add(QueryWord qw,String cat)
	{
		//System.out.println("aaya ki nahi");
		ArrayList<MapObject> al=qw.arr;
		int newWt,size=qw.getSizeArr(),start=qw.getStart(),totalAdded=0;
		int flag=0,i;
//		System.out.println(cat+"mainnnnnnnnnnnn");
//		System.out.println(start+"      "+size);
		for(i=start;i<size;i++)
		{
			flag=0;
			MapObject mo = al.get(i);
			if(mo==null)
			{
//				System.out.println(start+"       "+size+"         "+i+"   "+qw.query);
//				System.exit(1);
			}
	//		System.out.print("id "+ mo.docid+":");
			for(int y=0;y<cat.length();y++)
			{
				for(int x=0;x<mo.getCat().length();x++)
				{
					if(cat.charAt(y)==mo.getCat().charAt(x))
					{
						flag++;
						break;
					}
				}
			}
			if(flag==cat.length())
			{
					newWt=-1;
					flag=0;
					//System.out.println(arrList.size());
					for(int j=0;j<arrList.size();j++)
					{
						perCount = arrList.get(j);
						if(newWt!=-1)
						{
	//						perCount.put(mo.docid, newWt);
//							arrList.add(j, perCount);
							arrList.get(j).put(mo.docid,newWt);
				//			System.out.println(j+"       "+arrList.get(j).size());
							flag=1;
							break;
						}
						if(perCount.containsKey(mo.docid))
						{
				//			System.out.println(arrList.size());
				//			System.out.println(mo.docid);
							newWt = perCount.get(mo.docid)+mo.wt;
				//			System.out.println(arrList.get(j).size());
							//perCount.remove(mo.docid);
							arrList.get(j).remove(mo.docid);
							//arrList.add(j,perCount);
				//			System.out.println(arrList.get(j).size());
				//			System.out.println(arrList.size());
						}
		//				perCount=null;
//						else
//						{
//							perCount.put(mo.docid,mo.wt);
//							arrList.add(j, perCount);
//							break;
//						}
						
					}
					if(flag!=1)
					{
						arrList.get(0).put(mo.docid,mo.wt);
					}
					totalAdded++;
					if(totalAdded==10)
						break;
			}
			
		}
		qw.setStart(i);
		/*System.out.print(qw.query+" -------------\n");
		for(i=0;i<arrList.size();i++)
		{
			perCount=arrList.get(i);
			System.out.print(i+":"+perCount.size()+"\n");
		}*/
	}
	
	public static Map<String, Integer> sortWeights(
		Map<String, Integer> doc_details) {
	List<Entry<String, Integer>> list = new LinkedList<Entry<String,Integer>>(doc_details.entrySet()); 
	// Sorting the list based on values 
	Collections.sort(list, new Comparator<Entry<String, Integer>>() 
	{ public int compare(Entry<String,Integer> o1, Entry<String,Integer> o2)
	 { return o2.getValue().compareTo(o1.getValue()); } }); 
	 // Maintaining insertion order with the help of LinkedList 
	  Map<String,Integer> sortedMap = new LinkedHashMap<String,Integer>(); 
	  for (Entry<String,Integer> entry : list) 
	  {
		  sortedMap.put(entry.getKey(), entry.getValue()); 
		  entry.setValue(null);
	  } 
	  return sortedMap;
	}

}
//sequential :4903
//Binary: 2357ms

/*
sac afjnf sdlgksdn sdflosdj sdldsj
 
W/O thread
sachin
sachin-
9949732:Sachin r tendulkar
40688553:Sachin-Jigar
35877360:Sachin Tendulkar records
23954294:Sachin the maestro
24361198:Sachin waze
9267218:Sachin Tandulkar
13738217:Nawab of Sachin
35658803:Sachin:A Hundred Hundreds
26489983:Sachin Pilgaonkar
11017313:Sachin Bhowmik

141 ms
sachin tendulkar
sachin tendulkar-
9949732:Sachin r tendulkar
37310858:Tendulkar (disambiguation)
35877360:Sachin Tendulkar records
9267218:Sachin Tandulkar
23318954:Achievements of sachin tendulkar
2320807:Sachin Ramesh Tendulkar
12410929:Test matches (19912000)
38145512:Sachin Anil Punekar
9666876:Template:India Squad 1999 Cricket World Cup
2386696:Chemplast Cricket Ground

154 ms
sachin ramesh tendulkar
sachin ramesh tendulkar-
2386696:Chemplast Cricket Ground
39010460:New Zealand cricket team in India in 19992000
14334284:200708 Commonwealth Bank Series
38055294:Pakistani cricket team in India in 199899
9308380:English cricket team in India in 199293
30278328:Template:Indian Test Cricket Captains
34525986:List of international cricket centuries at the Zohur Ahmed Chowdhury Stadium
3405820:Sri Lankan cricket team in India in 200506
32930525:Indian cricket team in West Indies in 199697
26174648:2010 in cricket

353 ms
the legent cricketer
the legent cricketer-
32056454:Bangladeshi cricket team in Australia in 2003
31762269:Australia cricket team in Australia in 194647
31929194:Day/Night cricket in England
32208268:2009-13 ICC World Cricket League
32582564:James King (cricketer, born 1942)
31743771:Frank Mitchell (cricketer, born 1878)
32237365:Western Australian grade cricket
32474253:England cricket team in Ireland in 2011
31914843:James Knott (cricketer)
3583234:Les Townsend

5526 ms
ias officer
ias officer-
31151632:Ia.
16985524:US 77 (IA)
15674266:Medeleni, Iasi
1845451:Nakajima Ki-43Ia
19667876:IA (disambiguation)
10673720:Office Olympics (The Office episode)
2926131:Flag-officer
32050722:Officers' Club
28194665:Engineering officer
33621874:Class officer

48948 ms
raj is a good boy
raj is a good boy-
11506497:Wikipedia:Featured list candidates/2007 Cricket World Cup squads
4632695:Wikipedia:Articles for deletion/The Ken Baldwin Show
14284789:Wikipedia:Administrators' noticeboard/IncidentArchive327
19114885:Pattathanam
32862467:Template:Did you know nominations/Haugtussa (song cycle)
25353787:Wikipedia:Peer review/YuYu Hakusho/archive1
35528432:Leslie H. Sabo, Jr.
4744848:Ian Taylor (footballer)
5104438:Pepito Arriola
409022:Tom Hall

52256 ms
*/