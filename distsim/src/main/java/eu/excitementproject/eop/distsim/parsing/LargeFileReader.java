package eu.excitementproject.eop.distsim.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
 
public class LargeFileReader implements Iterable<String>{
    private BufferedReader reader;
 
    public LargeFileReader(File currFile, String encoding) throws IOException {
	reader = new BufferedReader(new InputStreamReader(new FileInputStream(currFile),encoding));
    }
 
    public void Close(){
	try	{
	    reader.close();
		}
	catch (Exception ex) {}
    }
 
    @Override
	public Iterator<String> iterator() {
	return new FileIterator();
    }
 
    private class FileIterator implements Iterator<String> {
	private String currentLine;
 
	@Override
	public boolean hasNext(){
	    try	{
	    	currentLine = reader.readLine();
	    }
	    catch (Exception ex) {
	    	currentLine = null;
	    	ex.printStackTrace();
	    }
 
	    return currentLine != null;
	}
 
	@Override
	public String next(){
		
	    return currentLine;
	}
 
	@Override
	public void remove(){}
    }

}
