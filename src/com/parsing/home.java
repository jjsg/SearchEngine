package com.parsing;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
public class home {

	public static void main(String args[]) throws SAXException, IOException
	{
		
		//XMLMETHODS CHECK LINE NUMBER 32
		//StringBuilder text = new StringBuilder("aáeéiíoóöőuúüű AÁEÉIÍOÓÖŐUÚÜŰ");
		long start = System.currentTimeMillis();;
	    XMLReader p = XMLReaderFactory.createXMLReader();
		p.setContentHandler(new XMLmethods(args[1]));
		p.parse(args[0]);
		long start1 = System.currentTimeMillis();;
		
		
		//System.out.println(XMLmethods.noDocuments);
		//XMLmethods.noDocuments=12975;
		System.out.println((start1 - start) + " ms");
	//	XMLmethods.noOfFileCount=1;
		for(int i=1;i<=XMLmethods.noOfFileCount;i++)
		{
			File f=new File(args[1]+"/index"+i+".txt");
		    ExternalSort.sort(f,args[1],i);
		}
		ExternalSort.completeSort(args[1],XMLmethods.noOfFileCount);
		//f.delete();
	    long end = System.currentTimeMillis();;
	    System.out.println((end-start)+" ms");
	}
	
}

/*
Start

end
12975
18047 ms
20575 ms
 */

/*
Start

end
10865 ms
10206 ms

*/


/*
du -sh ?.txt
du -c ?.txt
*/
