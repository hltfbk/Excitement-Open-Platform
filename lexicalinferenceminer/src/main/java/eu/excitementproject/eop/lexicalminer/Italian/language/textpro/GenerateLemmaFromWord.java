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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class GenerateLemmaFromWord {

    public static ArrayList<String> get(String word, String type, String command) {
        //String command = "/home/aprosio/TANA/textpro1.5/MorphoPro/bin/fstan /home/aprosio/TANA/textpro1.5/MorphoPro/models/italian-utf8.fsa";
        // String word = "andare";
        // String type = "v";
        
        ArrayList<String> requests = new ArrayList<String>();
        requests.add(word);

        HashSet<String> words = new HashSet<String>();
        
        if (type == null) {
            type = "";
        }
        else {
            if (type.equals("a")) {
                type = "adj";
            }
            if (type.equals("r")) {
                type = "adv";
            }
            if (type.equals("NOUN")) {
                type = "n";
            }
            if (type.equals("VERB")) {
                type = "v";
            }
            if (type.equals("ADJECTIVE")) {
                type = "adj";
            }
            if (type.equals("ADVERB")) {
                type = "adv";
            }
            if (type.equals("PREPOSITION")) {
                type = "prep";
            }
            if (type.equals("DETERMINER")) {
                type = "art";
            }
            if (type.equals("PRONOUN")) {
                type = "pron";
            }
            if (type.equals("PUNCTUATION")) {
                type = "punc";
            }
            if (type.equals("OTHER")) {
                type = "";
            }
        }

        try {
            Process process = Runtime.getRuntime().exec(command);
            OutputStream out = process.getOutputStream();

            Iterator<String> it = requests.iterator();

            while (it.hasNext()) {
                out.write((it.next() + "\n").getBytes());
            }

            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("[ /]");
                for (int i = 1; i < parts.length; i++) {
                    if (parts[i].length() > 0) {
                        String[] subparts = parts[i].trim().split("\\+");
                        if (type.length() > 0) {
                            try {
                                if (subparts[1].equals(type)) {
                                    String[] subsubparts = subparts[0].trim().split("~");
                                    words.add(subsubparts[subsubparts.length - 1]);
                                }
                            }
                            catch (Exception e) {
                                words.add(subparts[0]);
                                // System.out.println(Arrays.toString(subparts));
                            }
                        }
                        else {
                            for (String part : subparts) {		
                            	String[] subsubparts = subparts[0].trim().split("~"); //TODO: shouldn't this be part instead of subparts?
                                words.add(subsubparts[subsubparts.length - 1]);
                            }
                        }
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        ArrayList<String> ret = new ArrayList<String>();
        ret.addAll(words);
        if (ret.size() < 1) {
            ret.add(word);
        }
        return ret;
    }

    public static void main(String[] args) {
    	String command = "/home/aprosio/TANA/textpro1.5/MorphoPro/bin/fstan /home/aprosio/TANA/textpro1.5/MorphoPro/models/italian-utf8.fsa";
        System.out.println(GenerateLemmaFromWord.get("spariamo", "v", command));
    }
}
