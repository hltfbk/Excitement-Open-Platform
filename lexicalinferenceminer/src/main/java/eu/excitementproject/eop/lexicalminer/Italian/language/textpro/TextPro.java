package eu.excitementproject.eop.lexicalminer.Italian.language.textpro;

/*
 * Copyright 2012 Fondazione Bruno Kessler (FBK)

 * 
 * FBK reserves all rights in the Program as delivered.
 * The Program or any portion thereof may not be reproduced
 * in any form whatsoever except as provided by license
 * without the written consent of FBK.  A license under FBK's
 * rights in the Program may be available directly from FBK.
 */



import java.io.BufferedReader;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import main.java.org.fbk.cit.hlt.moschitti.utils.MyUtils;

public class TextPro {
    
    public static String convertStreamToStr(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            }
            finally {
                is.close();
            }
            return writer.toString();
        }
        else {
            return "";
        }
    }
        
    private String tpPath;
    private String tpCommand;
    private String encoding;
    private String parserPath = "";
    private String parser = "";
    public boolean debug = false;
    
    public String maltJar = "malt.jar";
    public String maltModel = "tut-fulltrain";
    public String desrExecutable = "src/desr";
    public String desrModel = "tut.model-";
    
    public static HashMap<String, String> maltConversionMap = new HashMap<String, String>();
	static {
		maltConversionMap.put("S", "NOUN");
		maltConversionMap.put("V", "VERB");
		maltConversionMap.put("X", "PUNCT");
		maltConversionMap.put("E", "PREP");
		maltConversionMap.put("R", "ART");
		maltConversionMap.put("N", "NUM");
		maltConversionMap.put("C", "CONJ");
		maltConversionMap.put("A", "ADJ");
		maltConversionMap.put("D", "ADJ");
		maltConversionMap.put("B", "ADV");
		maltConversionMap.put("P", "PRON");
		maltConversionMap.put("Q", "PRON");
	}
    
    public TextPro(String path, String parser, String parserpath, String encoding) {
        this.encoding = encoding;
        this.parser = parser;
        this.tpPath = path;
        if (!this.tpPath.endsWith("/")) {
            this.tpPath += "/";
        }
        this.tpCommand = this.tpPath + "textpro.pl";
        this.parserPath = parserpath;
        if (!this.parserPath.endsWith("/")) {
            this.parserPath += "/";
        }
    }
    
    public TextPro(String path, String parser, String parserpath) {
        this(path, parser, parserpath, "UTF-8");
    }
    
    public TextPro(String path) {
        this(path, "", "");
    }
    
    public ArrayList<HashMap<String, String>> run(String sentence, String language, String[] settings) {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            Map<String, String> env = builder.environment();
            env.put("TEXTPRO", tpPath);

            File tempIN = File.createTempFile("sentence", ".textpro");
            // //System.out.println(tempIN);
            if (!debug) {
                tempIN.deleteOnExit();
            }
            else {
                System.out.println(tempIN);
            }
            
            String tempINName = tempIN.getPath();
            
            String tempOUTName = tempINName + ".txp";
            
            // Elaborate settings
            Boolean toBeParsed = false;
            Boolean alreadyTokenized = false;
            
            String tpSettings = "token+tokenid";
            for (String s : settings) {
                if (s.equals("alreadytokenized")) {
                    alreadyTokenized = true;
                    continue;
                }
                if (s.equals("parse")) {
                    toBeParsed = true;
                    if (parserPath.equals("/")) {
                        //System.out.println("No Malt Parser folder. Exiting.");
                        System.exit(1);
                    }
                    continue;
                }
                if (s.equals("token") || s.equals("tokenid")) {
                    continue;
                }
                tpSettings += "+" + s;
            }
            
            if (alreadyTokenized) {
                String[] sentenceParts = sentence.split("\\s+");
                String tSentence = "";
                for (String part : sentenceParts) {
                    tSentence += (part + "\n");
                }
                sentence = tSentence;
            }
            
            String newSentence;
            if (!encoding.equals("UTF-8")) {
                newSentence = "";
                for (int i = 0; i < sentence.length(); i++){
                    char c = sentence.charAt(i);
                    if ((int) c < 256) {
                        newSentence += c;
                    }
                }
            }
            else {
                newSentence = sentence;
            }
            writeFile(tempIN, newSentence, encoding);
            
            List<String> commands = new ArrayList<String>();
            
            commands.clear();
            commands.add("/usr/bin/perl");
            commands.add(tpCommand);
            commands.add("-l");
            commands.add(language);
            commands.add("-c");
            commands.add(tpSettings);
            commands.add("-n");
            commands.add(tempOUTName);
            if (alreadyTokenized) {
                commands.add("-d");
                commands.add("token");
            }
            commands.add(tempINName);

            builder.command(commands);
            if (debug) {
                System.out.println(commands);
            }
            
            @SuppressWarnings("unused")
			int exitValue = builder.start().waitFor();
            
            File tempOUT = new File(tempOUTName);
            if (!debug) {
                tempOUT.deleteOnExit();
            }
            else {
                System.out.println(tempOUTName);
            }
            
            if (!encoding.equals("UTF-8")) {
                commands.clear();
                commands.add("/usr/bin/iconv");
                String temp = tempOUTName;
                tempOUTName += ".iso";
                commands.add("-f");
                commands.add(encoding);
                commands.add("-t");
                commands.add("UTF-8");
                commands.add(temp);
                builder.command(commands);
                if (!debug) {
                    new File(tempOUTName).deleteOnExit();
                }
                else {
                    //System.out.println(tempOUTName);
                    //System.out.println(commands);
                }
                
                Process process = builder.start();
                InputStream shellIn = process.getInputStream();
                exitValue = process.waitFor();
                
                String response = convertStreamToStr(shellIn);
                writeFile(new File(tempOUTName), response, encoding);
                
                // //System.out.println(commands);
            }
            
            Pattern optionLine = Pattern.compile("^\\s*#\\s*([A-Za-z]+):\\s*(.*)$");
            
            ArrayList<String> fields = new ArrayList<String>();
            ArrayList<HashMap<String, String>> tokens = new ArrayList<HashMap<String, String>>();
            
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(tempOUTName), "UTF-8"));
            String line = "";
    		while ((line = inputStream.readLine()) != null) {
    		    line = line.trim();
    		    Matcher m = optionLine.matcher(line);
                if (m.find()) {
                    String value = m.group(1);
                    if (value.toLowerCase().equals("fields")) {
                        String fieldsText = m.group(2);
                        fieldsText = fieldsText.trim();
                        fields.addAll(Arrays.asList(fieldsText.split("\\t")));
                    }
                    continue;
                }
                
                String[] pieces = line.split("\\t");
                if (pieces.length < fields.size() || fields.size() == 0) {
                    continue;
                }
                
                HashMap<String, String> thisToken = new HashMap<String, String>();
                for (int i = 0; i < fields.size(); i++) {
                    thisToken.put(fields.get(i), pieces[i]);
                }
                
                String cpos = thisToken.get("pos");
                if (maltConversionMap.containsKey(thisToken.get("pos").substring(0, 1))) {
                    cpos = maltConversionMap.get(thisToken.get("pos").substring(0, 1));
                }
                
                thisToken.put("cpos", cpos);
                
                if (thisToken.get("lemma").equals("full_stop")) {
                    thisToken.put("lemma", ".");
                }
                tokens.add(thisToken);
    	    }
    	    inputStream.close();
    	    
    	    if (toBeParsed) {
    	        StringBuffer parseText = new StringBuffer();
    	        
    	        int i = 0;
    	        for (HashMap<String, String> token : tokens) {
    	            parseText.append(++i);
    	            token.put("parseid", Integer.toString(i));
    	            parseText.append("\t");
    	            parseText.append(token.get("token"));
    	            parseText.append("\t");
    	            if (token.get("pos").substring(0, 1).equals("X")) {
    	                parseText.append("#\\" + token.get("lemma"));
	                }
	                else {
    	                parseText.append(token.get("lemma"));
	                }
    	            parseText.append("\t");
    	            parseText.append(token.get("pos"));
    	            parseText.append("\t");
    	            parseText.append(token.get("cpos"));
    	            parseText.append("\t");
    	            parseText.append("_");
    	            parseText.append("\t");
    	            parseText.append("_");
    	            parseText.append("\t");
    	            parseText.append("_");
    	            parseText.append("\n");
    	            if (!token.get("sentence").equals("-")) {
    	                i = 0;
        	            parseText.append("\n");
	                }
	            }
    	        // //System.out.println(parseText.toString());
    	        
                File tempParse = File.createTempFile("parse", ".textpro");
                if (!debug) {
                    tempParse.deleteOnExit();
                }
                else {
                    System.out.println(tempParse);
                }
                
                String tempParseName = tempParse.getPath();
                writeFile(tempParse, parseText.toString(),encoding);
                
                String tempOUTParseName = tempParseName + ".out";
                
                // ProcessBuilder parser = new ProcessBuilder();
                
                commands.clear();
                if (this.parser.equals("malt")) {
//                    commands.add("/usr/bin/java");
                	commands.add("/opt/share/jdk1.7.0/jre/bin/java");
                    commands.add("-mx3800m");
                    commands.add("-jar");
                    commands.add(parserPath + maltJar);
                    commands.add("-c");
                    commands.add(maltModel);
                    commands.add("-w");
                    commands.add(parserPath);
                    commands.add("-m");
                    commands.add("parse");
                    commands.add("-i");
                    commands.add(tempParseName);
                    commands.add("-o");
                    commands.add(tempOUTParseName);
                }
                else {
                    commands.add(parserPath + desrExecutable);
                    commands.add("-m");
                    commands.add(desrModel);
                    commands.add(tempParseName);
                }

                builder.command(commands);
                if (debug) {
                    System.out.println(commands);
                }
                
                Process process = builder.start();
                exitValue = process.waitFor();
                
                if (this.parser.equals("malt")) {
                    File tempOUTParse = new File(tempOUTParseName);
                    if (!debug) {
                        tempOUTParse.deleteOnExit();
                    }
                    else {
                        System.out.println(tempOUTParseName);
                    }
                
                    inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(tempOUTParseName), "UTF-8"));
                }
                else {
                    inputStream = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
                }
                line = "";
                i = 0;
        		while ((line = inputStream.readLine()) != null) {
        		    line = line.trim();
                    String[] pieces = line.split("\\t");
                    if (pieces.length < 10) {
                        continue;
                    }
                    HashMap<String, String> token = tokens.get(i);
                    token.put("parseparent", pieces[6]);
                    token.put("parselabel", pieces[7]);
        		    i++;
                }
                
    	        /*
    			MaltParserService service =  new MaltParserService();
    			// Inititalize the parser model 'model0' and sets the working directory to '.' and sets the logging file to 'parser.log'
    			service.initializeParserModel("-c model0 -m parse -w . -lfi parser.log");

    			// Creates an array of tokens, which contains the Swedish sentence 'Grundavdraget upphör alltså vid en taxerad inkomst på 52500 kr.'
    			// in the CoNLL data format.
    			String[] tokens = new String[11];
    			tokens[0] = "1\tGrundavdraget\t_\tN\tNN\tDD|SS";
    			tokens[1] = "2\tupphör\t_\tV\tVV\tPS|SM";
    			tokens[2] = "3\talltså\t_\tAB\tAB\tKS";
    			tokens[3] = "4\tvid\t_\tPR\tPR\t_";
    			tokens[4] = "5\ten\t_\tN\tEN\t_";
    			tokens[5] = "6\ttaxerad\t_\tP\tTP\tPA";
    			tokens[6] = "7\tinkomst\t_\tN\tNN\t_";
    			tokens[7] = "8\tpå\t_\tPR\tPR\t_";
    			tokens[8] = "9\t52500\t_\tR\tRO\t_";
    			tokens[9] = "10\tkr\t_\tN\tNN\t_";
    			tokens[10] = "11\t.\t_\tP\tIP\t_";
    			// Parses the Swedish sentence above
    			String[] outputTokens = service.parseTokens(tokens);
    			// Outputs the with the head index and dependency type information
    			for (int i = 0; i < outputTokens.length; i++) {
    				//System.out.println(outputTokens[i]);
    			}
    			// Terminates the parser model
    			service.terminateParserModel();
    			*/
	        }
    	    
            return tokens;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static void writeFile(File wikiChunk, String content, String encoding) {
        try {
/*            if (!encoding.equals("UTF-8")) {
                Charset charset = Charset.forName(encoding);
                @SuppressWarnings("unused")
				CharsetDecoder decoder = charset.newDecoder();
                CharsetEncoder encoder = charset.newEncoder();
 
                ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(content));
                byte[] myBytes = bbuf.array();
                FileOutputStream fos = new FileOutputStream(wikiChunk);
                fos.write(myBytes);
                fos.close();
            }
            else {
 */               BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wikiChunk), encoding));
                writer.write("" + content);
                writer.close();
 //           }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    
    public static void main(String[] args) {
        
        String[] settings = {"token", "sentence", "pos", "lemma", "entity", "parse"};
        String language = "ita";
        // String sentence = "L'operazione di Polizia giudiziaria e Procura di Milano riguarda immobili e quote di societa' italiane ed estere. Il ramo di indagine e' quello sul caso Maugeri. Coinvolte le cinque persone arrestate in aprile tra cui anche il faccendiere amico di Formigoni.";
        String sentence = "L'operazione di Polizia giudiziaria e Procura di Milano riguarda immobili e quote di societa' italiane ed estere. Il ramo di indagine e' quello sul caso Maugeri. Coinvolte le cinque persone arrestate in aprile tra cui anche il faccendiere amico di Formigoni.";
        
        // TextPro tp = new TextPro("/home/aprosio/textpro/", "malt", "/home/aprosio/TANA/italian-parser-1.7.1/", "ISO-8859-1");
        TextPro tp = new TextPro("/home/aprosio/textpro/", "malt", "/home/aprosio/TANA/italian-parser-1.7.1/", "ISO-8859-1");
        tp.debug = false;
        for (HashMap<String, String> token: tp.run(sentence, language, settings)) {
            System.out.format("%3s %20s %6s %5s %20s %3s %3s %20s", token.get("tokenid"), token.get("token"), token.get("pos"), token.get("cpos"), token.get("lemma"), token.get("parseid"), token.get("parseparent"), token.get("parselabel"));
            /*
            System.out.print(token.get("parseid"));
            System.out.print("\t");
            System.out.print(token.get("token"));
            System.out.print("\t");
            System.out.print(token.get("pos"));
            System.out.print("\t");
            System.out.print(token.get("cpos"));
            System.out.print("\t");
            System.out.print(token.get("lemma"));
            System.out.print("\t");
            System.out.print(token.get("parselabel"));
            System.out.print("\t");
            System.out.print(token.get("parseparent"));
            */
            //System.out.println("");
        }
        // //System.out.println(tp.run(sentence, language, settings));
    }
}
