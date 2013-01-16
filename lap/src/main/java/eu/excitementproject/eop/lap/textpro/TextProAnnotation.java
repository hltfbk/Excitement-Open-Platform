package eu.excitementproject.eop.lap.textpro;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProAnnotation {

	// the index of each annotation type (sentence, token, lemma, ...)
	HashMap<String,Integer> annType = null;
	
	// the annotations, indexed on the annotation type
	HashMap<String,HashMap<Integer[],String>> annotations = null;
	
	// the result of the TextPro annotation, in CoNLL (string) format
	String[] textProResult;
	
	String token = "token";
	String sentence = "sentence";
	String lemma = "lemma";
	String pos = "pos";
	String ne = "entity";
	String tokenstart = "tokenstart";
	String morpho = "comp_morpho";
	String stem = "stem";
		
	String originalText = "";
	
	public TextProAnnotation(String originalText, String textProResult){
		
		this.textProResult = textProResult.split("\n");
		this.originalText = originalText;

		setAnnotationTypes();
		loadAnnotations();
	}
	
	
	public Set<String> getAnnotationTypes(){
		return annType.keySet();
	}
	
	public void setAnnotationTypes(){
		Boolean found = false;
		int i = 0;
		Pattern atp = Pattern.compile("\\#\\s*FIELDS\\s*:\\s*(.*)");
		Matcher m;
		annType = new HashMap<String,Integer>();
		
		while ( !found || i >= textProResult.length) {
			m = atp.matcher(textProResult[i]);
			if (m.matches()) {
				found = true;
				String[] atps = m.group(1).split("\\t");
				for (int j=0; j < atps.length; j++) {
					annType.put(atps[j], j);
				}
			}
			i++;
		}
	}
	
	public HashMap<Integer[],String> getAnnotation(String type) {
		if (annotations.containsKey(type)) 
			return annotations.get(type);
		
		return null;
	}

	
	public String getNEtype(String value){
		
		if (value.matches(".*?\\t.*")) 
			return value.split("\\t")[0];
		
		return "UNKNOWN";
	}
	
	public String getNEtype(Integer[] index){
		if (annotations.get(ne).containsKey(index))
			return getNEtype(annotations.get(ne).get(index));
		
		return "UNKNOWN";
	}
	
	
	public String getToken(Integer[] index) {
		if (annotations.get(token).containsKey(index)) 
			return annotations.get(token).get(index);
		
		return null;
	}
	
	
	public Integer getTokenLength(Integer index) {
		if (annotations.get(token).containsKey(index)) {
			return annotations.get(token).get(index).length();
		}
		return 0;
	}
	
	
	public String getString(Integer[] index, String type) {

		if (type.matches(token) || type.matches(lemma) || type.matches(pos)) 
			return getToken(index);
		
		String str = annotations.get(type).get(index);
/*		if (type.matches(ne)) {
			return str.substring(str.indexOf("\t") + 1);
		}
*/		
		return str;
	}
	
	
	public Boolean hasAnnotation(String type) {
		return annotations.containsKey(type);
	}

	public void loadAnnotations(){
		annotations = new HashMap<String,HashMap<Integer[],String>>();
		for (String at: annType.keySet()) {
			loadAnnotations(at);
		}
	}
	
	public void loadAnnotations(String type) {
		HashMap<Integer[],String> ann = new HashMap<Integer[],String>();
		String neType = "";
		int start = 0, end = 0, comp_start = -1, prev_end = 0; 
		int index = annType.get(type);
		String[] info;
		
		Pattern neBeginPatt = Pattern.compile("B-(.*)");
		Matcher m;
		
		for(int i=0; i < textProResult.length; i++) {			
			if (! textProResult[i].matches("^\\#.*")) {
				
				info = textProResult[i].split("\\t");
				start = Integer.parseInt(info[annType.get(tokenstart)]);
				
				prev_end = end;
				end = start + info[annType.get(token)].length();
				
				if ((comp_start == -1) && type.matches(sentence)) 
					comp_start = start;
				
				if (type.matches("(token|lemma|pos)")) {
					ann.put(new Integer[]{start,end},info[index]);
				} else {

					if (type.matches(sentence)) {
						if (info[index].matches("\\<eos\\>")) {
							
							ann.put(new Integer[]{comp_start, end}, originalText.substring(comp_start, end));
							comp_start = -1;
						}
					} else {

						if (type.matches(ne)) {
							m = neBeginPatt.matcher(info[index]);
							if (m.matches()) {
								if ( comp_start > -1) {
									ann.put(new Integer[]{comp_start,prev_end}, neType+"\t"+originalText.substring(comp_start, prev_end));
								}
								neType = m.group(1);
								comp_start = start;
							} else {
								if (info[index].matches("(O|0)") && comp_start > -1) {
										ann.put(new Integer[]{comp_start,prev_end}, neType+"\t"+originalText.substring(comp_start, prev_end));
										comp_start = -1;
									}
								}
							}
						}
					}
				}
			}
		annotations.put(type, ann);
	}
	
		
}
