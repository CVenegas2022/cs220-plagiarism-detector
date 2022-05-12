package plagdetect;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class PlagiarismDetector implements IPlagiarismDetector {
	int numWords;
	LinkedList<String> fileNames;
	Map<String, LinkedList<String>> nGrams;
	//Map<String, LinkedList<String>> susNGrams;
	Map<String, Map<String, Integer>> numSusNGrams;
	
	public PlagiarismDetector(int n) {
		numWords=n;
		fileNames=new LinkedList<String>();
		nGrams=new HashMap<String, LinkedList<String>>();
		//susNGrams=new HashMap<String, LinkedList<String>>();
		numSusNGrams=new HashMap<String, Map<String, Integer>>();
	}
	
	@Override
	public int getN() {
		
		return numWords;
	}

	@Override
	public Collection<String> getFilenames() {
		return fileNames;
	}

	@Override
	public Collection<String> getNgramsInFile(String filename) {
		return nGrams.get(filename);
	}

	@Override
	public int getNumNgramsInFile(String filename) {
		return nGrams.get(filename).size();
	}

	@Override
	public Map<String, Map<String, Integer>> getResults() {
		return numSusNGrams;
	}

	@Override
	public void readFile(File file) throws IOException {
		//create new scanner to check file
		Scanner scan=new Scanner(file);
		
		//other variables used later
		String[] sentenceWords;
		String currNGram="";
		LinkedList<String> nGramList=new LinkedList<String>();
		
		//add file name to list of checked files
		String currFileName=file.getName();
		fileNames.add(currFileName);
		
		//initialize sentence
		String sentence="";
		
		//int testCount=0;	//test code
		

		
		
		//go through each line
		while(scan.hasNextLine())
		{
			//change temp variable sentence to current line
			sentence=scan.nextLine();
			
			//split sentence around spaces
			sentenceWords=sentence.split(" ");
			//testCount++;	//more test code
			
			
			
			//go through each set of n grams
			for(int i=0; i+numWords-1<sentenceWords.length; i++)
			{
				currNGram="";
				//store strings from sentenceWords at i, i+1, i+2,... as an n gram
				for(int j=0; j<numWords; j++)
				{
					currNGram += sentenceWords[i+j];
					if(j!=numWords-1)
					{
						currNGram+=" ";
					}
				}
				//System.out.println(currNGram);
				nGramList.add(currNGram);
			}
			
			nGrams.put(currFileName, nGramList);
			
		}
		
		//System.out.println(sentence+", "+testCount);	//even more test code
		
		//check common n grams
		for(int i=0; i<fileNames.size(); i++)
		{
			if(!fileNames.get(i).equals(currFileName))
			{
				getNumNGramsInCommon(currFileName, fileNames.get(i));
			}
		}
		
	}

	@Override
	public int getNumNGramsInCommon(String file1, String file2) {
		LinkedList<String> list1=nGrams.get(file1);
		LinkedList<String> list2=nGrams.get(file2);
		int numCommonNGramsTotal=0;
		int numCommonCurr=0;
		Map<String, Integer> tempMap=null;
		
		for(int i=0; i<list1.size(); i++)
		{
			for(int j=0; j<list2.size(); j++)
			{
				numCommonCurr=0;
				if(list1.get(i).equals(list2.get(j)))
				{
					numCommonNGramsTotal++;
					numCommonCurr++;
				}
				if(!numSusNGrams.containsKey(file1))
				{
					tempMap=new HashMap<String, Integer>();
					tempMap.put(file2, numCommonCurr);
					numSusNGrams.put(file1, tempMap);
				}
				else 
				{
					numSusNGrams.get(file1).put(file2, numCommonCurr);
				}
			}
		}
		return numCommonNGramsTotal;
	}

	@Override
	public Collection<String> getSuspiciousPairs(int minNgrams) {
		
		LinkedList<String> susPairs=new LinkedList<String>();
		int currNumCommon=0;
		for(int i=0; i<fileNames.size(); i++)
		{
			for(int j=0; j<fileNames.size(); j++)
			{
				currNumCommon=getNumNGramsInCommon(fileNames.get(i), fileNames.get(j));
				if(currNumCommon>=minNgrams && !fileNames.get(j).equals(fileNames.get(i)))
				{
					if(fileNames.get(i).compareTo(fileNames.get(j))<=0)
					{
						susPairs.add(fileNames.get(i)+" "+fileNames.get(j)+" "+currNumCommon);
					}
					else
					{
						susPairs.add(fileNames.get(j)+" "+fileNames.get(i)+" "+currNumCommon);
					}
				}
			}
		}
		
		return susPairs;
	}

	@Override
	public void readFilesInDirectory(File dir) throws IOException {
		// delegation!
		// just go through each file in the directory, and delegate
		// to the method for reading a file
		for (File f : dir.listFiles()) {
			readFile(f);
		}
	}
}
