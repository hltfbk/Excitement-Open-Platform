package eu.excitementproject.eop.lap.textpro;


import java.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;

/** La classe Loader fornisce i metodi di lettura da
 * sorgenti estrene.
 *
 * @author Roberto Zanoli
 * @author zanoli@itc.it
 * @version 1.0
 */
public class Loader {

    private static final boolean DEBUG = false;

    /** Costruisce e inizializza un nuovo oggetto Loader
     *  per la lettura di dati da una sorgente esterna
     */
    public Loader() {

    	if (DEBUG)
    		System.out.println("Loader: +Loader()");

    }

    public List<String> load(String fileName) throws Exception {
    	
    	List<String> text = null;
    	
    	//creazione del flusso di lettura  con buffer
    	BufferedReader reader = null; 
    	
    	try {
    		
    		text = new ArrayList<String>();
//    		reader = new BufferedReader(new FileReader(fileName));
			reader = new BufferedReader( new InputStreamReader(this.getClass().getResourceAsStream(fileName)));

    		String line = reader.readLine();
    		//lettura delle linee del file
    		while (line != null) {
    			text.add(line);
    			line = reader.readLine();
    		}
    	
    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	} finally { 
    		if (reader != null)
    			reader.close();
    	}

    	return text;

    }
    
    public String loadText(String fileName) throws Exception {
    	
    	StringBuffer text = null;
    	BufferedReader reader = null;
    	
    	try {
    		text = new StringBuffer();
    		reader = new BufferedReader(new FileReader(fileName));
//			reader = new BufferedReader( new InputStreamReader(this.getClass().getResourceAsStream(fileName)));

    		String line = reader.readLine();
    		//lettura delle linee del file
    		while (line != null) {
    			text.append(line);
    			text.append("\n");
    			line = reader.readLine();
    		}
    		
    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	} finally {
    		if (reader != null)
    			reader.close();
    	}

    	return text.toString();

    }
    
    
    public Properties loadProperties(String fileName) throws Exception {
    
    	Properties properties = new Properties();
    	FileInputStream fis = null;
    	
		try {
			
			fis = new FileInputStream(fileName);
			properties.loadFromXML(fis);
			//PROPERTIES.list(System.out);
			
    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	} finally {
    		if (fis != null)
    			fis.close();
    	}
		
		return properties;

	}
    

    /** Salva i dati in un file
     * @param fileName nome del file
     * @param text il testo da salvare
     * @exception IOException
     */
    public void save(String fileName, String text, boolean append) throws Exception {
    	
    	BufferedWriter writer = null;
    	
    	try {
    		
    		//creo un oggetto FileWriter...
	    	// ... che incapsulo in un BufferedWriter...
	    	writer = new BufferedWriter(new FileWriter(fileName, append));
	    	// ... che incapsulo in un PrintWriter
	    	PrintWriter printout = new PrintWriter(writer);
	    	printout.print(text);
	    	printout.close();
	    	
    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	} finally {
    		if (writer != null)
    			writer.close();
    	}

    }
    
    public void save(String fileName, List<String> list, boolean append) throws Exception {
    	
    	BufferedWriter writer = null;
    	
    	try {
    		
	    	writer = new BufferedWriter(new FileWriter(fileName));
	    	// ... che incapsulo in un PrintWriter
	    	PrintWriter printout = new PrintWriter(writer, append);
    	
	    	Iterator<String> iterator = list.iterator();
	    	while(iterator.hasNext()) {
	    		printout.println(iterator.next());
	    	}
	    	printout.close();
	    	
    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	} finally {
    		if (writer != null)
    			writer.close();
    	}

    }

    
    /** Rimuove un file
     * @param fileName nome del file da rimuovere
     * @exception IOException
     */
    public boolean remove(String fileName) throws Exception {

    	boolean result = false;
    	
    	try {
    		
    		File file = new File(fileName);
    		result = file.delete();
    		
    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	}
    	
    	return result;
    	
    }

    /** Verifica l'esistenza di un file
     * @param fileName nome del file
     * @return true se il file esiste
     * @exception IOException
     */
    public boolean exists(String fileName) throws Exception {
    	
    	boolean result = false;
    	
    	try {
    		
    		File file = new File(fileName);
    		result = file.exists();	
    		
    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	}
    	
    	return result;

    }
    
    /** Restituisce la lista dei file presenti nella directory
     * @param path la directory
     * @return la lista dei patents
     */
    public File[] list(String dirName) throws Exception {

    	File[] list = null;
    	
    	try {
   
    		File file = new File(dirName);
    		list = file.listFiles();

    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	}

        return list;

    }
    
    
    /** Restituisce la lista dei file presenti nella directory
     * @param path la directory
     * @return la lista dei patents
     */
    public int size(String dirName) throws Exception {

    	int size = 0;
    	
    	try {
   
    		File file = new File(dirName);
    		size = file.listFiles().length;

    	} catch (Exception e) {
    		System.err.println(e.getMessage());
    		throw new Exception(e.getMessage());
    	}

        return size;

    }
    

  }
