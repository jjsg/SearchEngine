Search Engine


XML Document : : http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2
Size : 43GB

Basic and Simple Logic:

Index Generation:
1. Create bag of words by removing stop-words and applying stemming.
2. Find the tf-idf of each word.
3. Each word is distinguished on its location (Title,Infobox,Category,External Links,References) 
4. Store all the documents containing the word in the sorted order of the weight.
5. Since the size of index is huge, we have used external merger sorting algorithm

Size of Index: 12GB

Searching:
1. For each word in search term retrieve top 100 document list.
2. Find the intersection of each of document list, and get the top ranked documents.

Search Time (average): 2s


Usage:
1. To compile the code run : install.sh in bin
1. To generate the index run : index.sh in bin
2. To start the search engine need to run : query.sh in bin


Future Scope:
1. Apply compression
2. Searching on basis of various parameters (As in infobox)
3. Decrease search time
4. For more precise results: proper weighting measures, phrase identification,named entity, etc.
