package eu.excitementproject.eop.distsim.parsing;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public class UKWacReader implements CorpusReader {
	
	private Iterator<String> linesIter;//iterates through the lines in currFile
	private StringBuilder currDocContent; // a string representation of the content
	private Integer SentenceNum;//starts from 1 (not zero)
	private LargeFileReader fileReader;
	public static final String POS_SEP = "#_@";//hack for queries
	public static final String TOKEN_SEP = "%%%";
	public static final String VERB_SEP = "__";
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * @param dir folder containing files to read
	 * @throws IOException
	 */
	public UKWacReader(File  file) throws IOException {
		this(file, null,DEFAULT_ENCODING);
	}
	
	/**
	 * @param dir folder containing files to read
	 * @param stopWordsFile
	 * @throws IOException
	 */
	public UKWacReader(File file, File stopWordsFile,String encoding) throws IOException{
		if (file == null || !file.exists())
			throw new IOException("the directory is null or nonexistant");
		//if (stopWordsFile == null || !stopWordsFile.exists())
			//throw new IOException("the stop words file is null or nonexistant");
		if (file.isFile()) {
			fileReader = new LargeFileReader(file,encoding);
			linesIter = fileReader.iterator();
			currDocContent = new StringBuilder(); // a string representation of the content
		} else
			throw new IOException("given " + file.getPath() + " is not a file");
	}

	/**
	 * Progresses the reader's head to the next sentence to read.
	 * @return false iff reached the end of corpus. 
	 * @throws IOException 
	 */
	@Override
	public String nextSentence() throws Exception {
		// if there is no additional lines (what if we have additional files
		if(!linesIter.hasNext())
			return null;
					
		//Doc is the indexed unit,the indexed unit in this case is a sentence
		String line = linesIter.next();
		
		// if the line is starting new document (document includes some sentences)
		if (isDocStart(line)){
			//zero out the sentences counter
			SentenceNum = 0;
			line = linesIter.next();	
			// while there is next line and the line does not start sentence
			while (linesIter.hasNext()){
				line = linesIter.next();
				if (isSentenceStart(line)) 
					//stop since the line start sentence
					break;
				}
		}
		String[] words;		
		currDocContent = new StringBuilder();		
		if (isSentenceStart(line)){				
			// while there is next line and the line does not start/end sentence or end document
			while(linesIter.hasNext() ){				
				line = linesIter.next();
				if (isSentenceEnd(line) || isSentenceStart(line) || isDocEnd(line)  ) break; 
				words = line.split("\t");
				if(words.length<6)
				{
					/*String strDebug = "";
					for (int i = 0; i < words.length; i++) {
						strDebug += " : "+words[i];
					}
					System.out.println(strDebug);*/
				}
				else
					currDocContent.append(words[1]+POS_SEP+words[2]+POS_SEP+words[3]+POS_SEP+words[4]+POS_SEP+words[5]+TOKEN_SEP);
			}
			SentenceNum +=1;
			
		}		
		
		return currDocContent.toString();	
	}

	public void close() {
		fileReader.Close();
	}
	

//------------- Private -----------------------------	
	

	private boolean isSentenceEnd(String line) {
		return line.startsWith("</s>");
	}
	
	private boolean isSentenceStart(String line) {
		return line.startsWith("<s>");
	}
	
	private boolean isDocEnd(String line){
		return line.startsWith("</text");
	}

	private boolean isDocStart(String line) {
		//return line.startsWith("</text><text id=");
		return line.startsWith("<text id=");
	}
}
