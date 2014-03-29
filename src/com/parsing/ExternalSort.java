package com.parsing;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

public class ExternalSort {

	static int MAX = 400;
		public static void sort(File input, String output,int no) throws IOException {
                ExternalSort.mergeSortedFiles(ExternalSort.sortInBatch(input),output,no,true);
        }

		 static String tempdirectory;
	     static
	     {
	        	tempdirectory="temporary";
	     } 


        
       


        public static List<File> sortInBatch(File file)
                throws IOException {
                List<File> files = new ArrayList<File>();
                BufferedReader fbr = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file)));
                long blocksize = Runtime.getRuntime().freeMemory()/2;// in
                                                                             // bytes

                try {
                        List<String> tmplist = new ArrayList<String>();
                        String line = "";
                        try {
                                //int counter = 0;
                                while (line != null) {
                                        long currentblocksize = 0;
                                        while ((currentblocksize < blocksize)
                                                && ((line = fbr.readLine()) != null)) {
                                                
                                                tmplist.add(line);
                                                currentblocksize += 2*line.length();
                                        }
                                        files.add(sortAndSave(tmplist));
                                        tmplist.clear();
                                }
                        } catch (EOFException oef) {
                                if (tmplist.size() > 0) {
                                        files.add(sortAndSave(tmplist));
                                        tmplist.clear();
                                }
                               tmplist=null; 
                        }
                } finally {
                	
                        fbr.close();
                }
                return files;
        }

        
        public static File sortAndSave(List<String> tmplist) throws IOException {
                Collections.sort(tmplist);
                File newtmpfile = File.createTempFile("sortInBatch",
                        "flatfile");
                newtmpfile.deleteOnExit();
                OutputStream out = new FileOutputStream(newtmpfile);
                BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
                        out));
                try {
                	    for (String r : tmplist) {
                                        fbw.write(r);
                                        fbw.newLine();
                                      
                         }
                } finally {
                        fbw.close();
                }
                return newtmpfile;
        }
        

        public static int mergeSortedFiles(List<File> files, String outputfile,int no,boolean flag) throws IOException {
                ArrayList<compare> bfbs = new ArrayList<compare>();
                for (File f : files) {
                        InputStream in = new FileInputStream(f);
                        BufferedReader br= new BufferedReader(new InputStreamReader(in));
                        compare bfb = new compare(br);
                        bfbs.add(bfb);
                }
                //BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile)));
                int rowcounter;
                if(flag)
                	rowcounter= merge(outputfile, bfbs,no);
                else
                	rowcounter=mergeFinal(outputfile, bfbs);
                for (File f : files) f.delete();
                return rowcounter;
        }
        
        public static int merge(String f, List<compare> buffers,int no) throws IOException {
            File fil=new File(f+"/sorted_index"+no+".txt");
   // 		if(fil.exists())
     //       	fil.delete();
			BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fil)));
            PriorityQueue<compare> pq = new PriorityQueue<compare>(
                    11, new Comparator<compare>() {
                            public int compare(compare i,
                                    compare j) {
                                    return i.peek().compareTo(j.peek());
                            }
                    });
            for (compare bfb: buffers)
                    if(!bfb.empty())
                            pq.add(bfb);
            int rowcounter = 0;
            try {
            		//TreeMap<Integer,Integer> doc_details = new TreeMap<Integer,Integer>();
            	    while (pq.size() > 0) {
                    		compare bfb = pq.poll();
                    		fbw.write(bfb.pop());
                    		fbw.newLine();
                            ++rowcounter;
                            if (bfb.empty()) {
                                    bfb.fbr.close();
                            } else {
                                    pq.add(bfb); // add it back
                            }
                    }
            } finally {
            	   
            		fbw.close();
            }
            return rowcounter;

    }

        public static void completeSort(String path,int no) throws IOException {
            ExternalSort.mergeSortedFiles(ExternalSort.split(path,no),path,no,false);
    }

        public static List<File> split(String path,int count)
                throws IOException {
        	List<File> files = new ArrayList<File>();
            for(int i=1;i<=count;i++)
        	{
        		File fd = new File(path+"/sorted_index"+i+".txt");
        		files.add(fd);
        	}
        	return files;
        }
   
        
        public static int mergeFinal(String f, List<compare> buffers) throws IOException {
                File fil=new File(f+"/a.txt");
        		if(fil.exists())
                	fil.delete();
        		RandomAccessFile fbw = new RandomAccessFile(fil, "rw");
                PriorityQueue<compare> pq = new PriorityQueue<compare>(
                        11, new Comparator<compare>() {
                                public int compare(compare i,
                                        compare j) {
                                        return i.peek().compareTo(j.peek());
                                }
                        });
                for (compare bfb: buffers)
                        if(!bfb.empty())
                                pq.add(bfb);
                int rowcounter = 0;
                StringBuilder level2=new StringBuilder();
//                StringBuilder level1=new StringBuilder();
                fil=new File(f+"/level2.txt");
                if(fil.exists())
                	fil.delete();
                RandomAccessFile pwL2 = new RandomAccessFile(fil,"rw");
        		String lastkey ="";
//        		int base=0;
                StringBuilder final_string= new StringBuilder();
                StringBuilder main_string= new StringBuilder();
                char c='a';
                try {
                			//TreeMap<Integer,Integer> doc_details = new TreeMap<Integer,Integer>();
                	//Map<String,Float> doc_details = new HashMap<String, Float>(); 
                	TreeMap<Float,TreeSet<String>> doc_details = new TreeMap<Float,TreeSet<String>>(Collections.reverseOrder());
                	TreeSet<String> internal;
                	int counter=0,alphaCount=0;
                	//long initialOffset;
                	int totalDoc=XMLmethods.noDocuments;
                        while (pq.size() > 0) {
                        		compare bfb = pq.poll();
                                if (!bfb.getKey().equals(lastkey)) {
                                	    if(counter>0)
                                		{
                                	    	if(lastkey.charAt(0)>c)
                                	    	{
                                	    		//offs.append(c);
                                	    		//for(int i=(int)lastkey.charAt(0);i<c;i++)
                                	    			//offs.append(-1);
                                	    		c++;
                                	    		fbw.close();
                                	    		fil = new File(f+"/"+c+".txt");
                                	    		if(fil.exists())
                                                	fil.delete();
                                        		fbw = new RandomAccessFile(fil, "rw");
  //                              	    		level1.append(pwL2.getFilePointer());
   //                             	    		level1.append("\n");
                                	    		alphaCount=0;
                                	    	}
                                	    	if(totalDoc!=counter)
                                	    	{	
	                                	    	//initialOffset=fbw.getFilePointer()+lastkey.length()+1;
	                                	    	final_string.setLength(0);
	                                	    	int size=doc_details.size()/10;
	                                	    	float log=(float)(1000*Math.log10(totalDoc/counter));
	                                	    	if(size<100)
	                                	    		size=doc_details.size();
	                                	    	if(size>200)
	                                	    		size=100;
	                                	    	main_string.append(lastkey);
	                                	    	int i=0;
	                                	    	//doc_details = sortWeights(doc_details);
	                                	    	for(Float s: doc_details.keySet())
	                            				{
	                                	    		/*if(counter>size && doc_details.get(s).intValue()==0)
	                                	    		{
	                                	    		}
	                                	    		else*/
	                                	    		internal = doc_details.get(s);
	                                	    		for(String s1 : internal)
	                                	    		{
	                                	    			if(i==size)
		                                	    		{
		                                	    			long newOffset = (long)final_string.length();
		                                	    			main_string.append(":"+newOffset);
		                                	    			main_string.append(final_string);
		                                	    			fbw.writeBytes(main_string.toString());
		                                        	    	main_string.setLength(0);
		                                        	    	final_string.setLength(0);
		                                	    	//		initialOffset=newOffset;
		                                	    			i=0;
		                                	    		}
		                                	    		
	                                	    			final_string.append(":"+s1+":"+(int)(s*log));
	                                	    			i++;
		                            					counter--;
	                                	    		}
	                            					
	                            				}
	                                	    	if(final_string.length()>0)
	                                	    	{
	                                	    		long newOffset = final_string.length();
	                                	    		//newOffset+=newOffset.toString().length()+2;
	                            	    			main_string.append(":"+newOffset);
	                            	    			main_string.append(final_string);
	                            	    			fbw.writeBytes(main_string.toString()+"\n");
	                                    	    	main_string.setLength(0);
	                                    	    	final_string.setLength(0);
	                            	    		//	initialOffset=newOffset;
	                            	    		}
	                                	    	alphaCount++;
	                                	    	if(alphaCount==MAX)
	                                	    	{
	                                	    		level2.append(lastkey+":");
	                                	    		level2.append(fbw.getFilePointer());
	                                	    		level2.append("\n");
	                                	    		pwL2.writeBytes(level2.toString());
	                                	    		level2.setLength(0);
	                                	    		alphaCount=0;
	                                	    	}
	                                	    	//fbw.newLine();
                                	    	}
                                        }
                                	    	
                                	    lastkey=null;
                                        lastkey=bfb.getKey();
                                        doc_details.clear();
                                        counter=0;
                                        //doc_details.put(bfb.doc_id,bfb.count);
                                      //  base=bfb.doc_id;
                                        if(doc_details.containsKey(bfb.tf))
                                    	{
                                    		internal = doc_details.get(bfb.tf);
                                    		internal.add(bfb.doc_id);
                                    	}
                                    	else
                                    	{
                                    		internal = new TreeSet<String>();
                                    		internal.add(bfb.doc_id);
                                    	}
                                    	doc_details.put(bfb.tf,internal);
                                    	counter++;
                                        bfb.pop();
                                        //df=1;
                                }
                                else
                                {
                                	//df++;
                                	//bfb.doc_id=base-bfb.doc_id;
                                	if(doc_details.containsKey(bfb.tf))
                                	{
                                		internal = doc_details.get(bfb.tf);
                                		internal.add(bfb.doc_id);
                                	}
                                	else
                                	{
                                		internal = new TreeSet<String>();
                                		internal.add(bfb.doc_id);
                                	}
                                	doc_details.put(bfb.tf,internal);
                                	counter++;
                                	//doc_details.put(bfb.doc_id,bfb.count);
                                	bfb.pop();
                                }
                                ++rowcounter;
                                if (bfb.empty()) {
                                        bfb.fbr.close();
                                } else {
                                        pq.add(bfb); // add it back
                                }
                                bfb=null;
                        }
                        if(counter>0 && totalDoc!=counter)
                        {
                        	final_string.setLength(0);
                        	int size=doc_details.size()/10;
                        	float log=(float)(1000*Math.log10(totalDoc/counter));
                	    	if(size<10)
                	    		size=doc_details.size();
                	    	main_string.append(lastkey);
                	    	int i=0;
 //               	    	initialOffset=fbw.getFilePointer()+lastkey.length()+1;
                	    //	doc_details = sortWeights(doc_details);
                	    	for(Float s: doc_details.keySet())
            				{
                	    		/*if(counter>size && doc_details.get(s).intValue()==0)
                	    		{
                	    		}
                	    		else*/
                	    		internal = doc_details.get(s);
                	    		for(String s1 : internal)
                	    		{
                	    			if(i==size)
                    	    		{
                    	    			long newOffset =final_string.length();
                    	    		//	newOffset+=newOffset.toString().length()+2;
                    	    			main_string.append(":"+newOffset);
                    	    			main_string.append(final_string);
                    	    			fbw.writeBytes(main_string.toString());
                            	    	main_string.setLength(0);
                            	    	final_string.setLength(0);
                    	    			//initialOffset=newOffset;
                    	    			i=0;
                    	    		}
                    	    		
                	    			final_string.append(":"+s1+":"+(int)(s*log));
                	    			i++;
                					counter--;
                	    		}
            					
            				}
                	    	if(final_string.length()>0)
                	    	{
                	    		long newOffset =final_string.length();
                	    	//	newOffset+=newOffset.toString().length()+2;
            	    			main_string.append(":"+newOffset);
            	    			main_string.append(final_string);
            	    			final_string.setLength(0);
 //           	    			initialOffset=newOffset;
            	    		}
                	    	fbw.writeBytes(main_string.toString()+"\n");
                			main_string.setLength(0);
                        }
   //                     level1.append(pwL2.getFilePointer());
    //             	    level1.append("\n");	
                } finally {
                	   pwL2.close();
                		fbw.close();
                        for (compare bfb : pq)
                                bfb.close();
 //                       PrintWriter pw = new PrintWriter(f+"/level1.txt");
  //                      pw.write(level1.toString());
    //                    pw.close();
                }
                return rowcounter;

        }


/*		private static Map<String, Float> sortWeights(
				Map<String, Float> doc_details) {
			List<Entry<String, Float>> list = new LinkedList<Entry<String, Float>>(doc_details.entrySet()); 
			// Sorting the list based on values 
			Collections.sort(list, new Comparator<Entry<String, Float>>() 
			{ public int compare(Entry<String, Float> o1, Entry<String,Float> o2)
			 { return o2.getValue().compareTo(o1.getValue()); } }); 
			 // Maintaining insertion order with the help of LinkedList 
			  Map<String,Float> sortedMap = new LinkedHashMap<String,Float>(); 
			  for (Entry<String,Float> entry : list) 
			  {
				  sortedMap.put(entry.getKey(), entry.getValue()); 
				  entry.setValue(null);
			  } 
			  return sortedMap;
		}*/
}
