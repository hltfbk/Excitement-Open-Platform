package eu.excitementproject.eop.lap.textpro;


import java.io.IOException;


/** La classe TextProHandler fornisce dei metodi di utilita`
 * per la gestione di TextPro.
 * 
 * @author Roberto Zanoli (FBK)
 * @author Vivi Nastase (FBK)
 * @version 0.1, 2005
 */
public class TextProHandler {

    private static final boolean DEBUG = false;
    
    private static Loader LOADER = new Loader();
    
    private static TextProHandler _instance;
    
    private static Process PROC = null;
    
	protected String TEXTPRO;
	protected String YAMCHA_HOME;
    
    //file temporaneo per la memorizzazione del testo da analizzare
    private static final String TEMP_FILENAME = "esempio";
    
    //file temporaneo per la memorizzazzione del testo analizzato; è l'output di TextPro
    private static final String TEMP_FILENAME_OUT = TEMP_FILENAME + ".txp";
    
    //.log file
    //    private static final String TEMP_FILENAME_LOG = TEMP_FILENAME + ".log";
    
    //directory dei file temporanei
    private static final String TEMP_PATH = "/tmp/";
    
    //Variabili Globali: TEXTPRO, YAMCHA_HOME e PATH
    private static String[] CONFIG = {"TEXTPRO=" + ""};

    
    
    /** new TextProHandler object
     */
    public TextProHandler() throws IOException{

    	initializePaths();
    	if (DEBUG)
            System.out.println("TextProHandler: #TextProHandler()");
    }
    
    
	/**
	 * set the values for TEXTPRO and YAMCHA_HOME from the environment variables (which the user must set to be able to run TextPro)
	 */
	private void initializePaths() throws IOException{
			initializePaths("TEXTPRO");
//			initializePaths("YAMCHA_HOME"); // this was for TextPro1.4.3
	}
	
	private void initializePaths(String name) throws IOException{
		if (System.getenv().containsKey(name)) {
			if (name.matches("TEXTPRO")) {
				TEXTPRO = System.getenv(name);
				CONFIG[0] = "TEXTPRO=" + TEXTPRO;
			} else if(name.matches("YAMCHA_HOME")) {
				YAMCHA_HOME = System.getenv(name);
				CONFIG[1] = "YAMCHA_HOME=" + YAMCHA_HOME;
			}
		} else {
			throw new IOException("Environment variable " + name + " not defined!");
		}
	}
    
    /** Pattern Singleton
     */
    protected static synchronized TextProHandler getInstance() throws IOException{

        if ( _instance==null ) {
            _instance = new TextProHandler();
        }
        
        return _instance;
    }
    
    /**
     *  Kills the subprocess.
     */
    protected void destroy() {
    
    	PROC.destroy();
    	
    }

    /** Performs the TextPro analysis on the specified data set.
     * @param text The data set
     * @param parameters The TextPro parameters
     * @return The TextPro analysis
     * @exception IOException
     */
    protected synchronized String getAnalysis(String text, String parameters) throws Exception {
    	
        String result = null;
        
//        System.out.println("Processing text: \n*" + text + "*");
        
        //verifica dei parametri di TextPro
        if (checkParameters(parameters) == false) 
            throw new IOException("Wrong parameters!");
        
        //Chiamata di TextPro
        String[] textpro = {"/bin/tcsh", "-c", TEXTPRO + "/textpro.pl" + " " + parameters + " -o " + TEMP_PATH + " " + TEMP_PATH + "/" + TEMP_FILENAME };
        
        //rimuove eventuali analisi precedenti
        remove(TEMP_PATH +  TEMP_FILENAME_OUT);

        //salva il testo da analizzare su file
        save(TEMP_PATH + TEMP_FILENAME, text);

        //analizza il testo chiamando TextPro
        run(textpro, CONFIG);
        
        //legge il testo analizzato da TextPro
        result = load(TEMP_PATH + TEMP_FILENAME_OUT);

        return result;
        
    }
    
    /**
     * Analyses the file passed as input according to the given parameters, and writes the output to the default output path
     */
    public void makeAnalysis(String FileName, String parameters) throws Exception {
    	
        if (checkParameters(parameters) == false) 
            throw new IOException("Wrong parameters!");
        
        String[] textpro = {"/bin/tcsh", "-c", TEXTPRO + "textpro.pl" + " " + parameters + " -o " + TEMP_PATH + " " + FileName };
        
        //rimuove eventuali analisi precedenti
        remove(TEMP_PATH +  TEMP_FILENAME_OUT);

        //analizza il testo chiamando TextPro
        run(textpro, CONFIG);
    }
    
    
    /** Checks the TexPro parameters
     * @param parameters The TextPro parameters list (e.g. -t ym -c token+tokenid+tokenstart+tokenend)
     * @return true if the specified parameters are valid, false otherwise
     */
    private boolean checkParameters(String parameters) {
    	
    	boolean check = false;
    	
    	String[] command = {"-a", "-c", "-d", "-eos", "-h", "-html", "-l", "-n", "-nk", "-no_abstract_lemma", "-o", "-r", "-s", "-tmp", "-u", "-v", "-y", "ym", "tnt", "ita", "eng", "token", "tokenid", "tokenstart", "tokenend", "sentence", "pos", "morpho", "lemma", "chunk", "entity", "wnpos", "comp_morpho", "full_morpho"};
    	
    	String[] parameterList = (parameters.replaceAll("\\s", "\\+")).split("\\+");
    	for (int j = 0; j < parameterList.length; j++) {
    		check = false;
    		for (int i = 0; i < command.length; i++) {
    			if (parameterList[j].equals(command[i])) {
    				check = true;
    				break;
    			}
    		}
    		if (check == false) {
                        System.err.println("error:" + parameterList[j]); 
			return check;
		}
    	}
    	
    	return check;
    	
    }
    
    /** Checks the TextPro tool 
     * @param
     * @return 1 if it run, 0 otherwise
     * @exception IOException
     */
    protected synchronized int check() throws IOException {

    	return 1;
    }
    
    public String getResultFile() {
    	return TEMP_PATH + TEMP_FILENAME_OUT;
    }
    
    
    /** Saves the text into the file: fileName
     * @param fileName The file name
     * @param text The text
     * @exception IOException
     */
    
    private void save(String fileName, String text) throws Exception {

        LOADER.save(fileName, text, false);

    }
    
    
    /** Appends the text in the specified file
     * @param fileName The file name
     * @param text The text
     * @param append boolean if true, then data will be written to the end of the file 
     * rather than the beginning
     * @exception IOException
     */
    public void save(String fileName, String text, boolean append) throws Exception {

        LOADER.save(fileName, text, append);

    }

    /** Remove the file fileName
     * @param fileName The file name
     * @exception IOException
     */
    private void remove(String fileName) throws Exception {

        LOADER.remove(fileName);
 	 
    }



    /** Loads the file fileName
     * @param fileName The file name
     * @exception IOException
     */
    private String load(String fileName) throws Exception {

        return LOADER.loadText(fileName);

    }

    /** Runs the TextPro tool
     * @param command The TextPro command
     * @param config The configuration setting
     * @exception IOException
     */
    private void run(String[] command, String[] config) throws IOException {

        try {

            Runtime rt = Runtime.getRuntime();
            PROC = rt.exec(command, config);
            PROC.waitFor();

        } catch(Exception e) {
            throw new IOException("TextPro error: " + e.getMessage());
        }

    }
    
    
    public static void main (String[] args){
 	   
       try {
    	   TextProHandler txp = new TextProHandler();
    	   System.err.println(txp.getAnalysis("Harry Redknapp tells BBC Sport he is close to becoming the new manager of Queens Park Rangers.\n", "-l eng -y -c token+sentence+pos+entity"));
           System.err.println(txp.getAnalysis(LOADER.loadText("/destromath0/tcc/TextProLinux1.4.3/tmp/esempio"), "-l ita -y -c token+sentence+pos+entity"));
           
       } catch (Exception e) { 
    	   System.err.println(e.getMessage()); 
       }
       
    }
    
}
