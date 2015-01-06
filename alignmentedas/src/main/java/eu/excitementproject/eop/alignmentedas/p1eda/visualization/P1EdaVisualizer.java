package eu.excitementproject.eop.alignmentedas.p1eda.visualization;

/**
 * An implementation if the {@link eu.excitementproject.eop.alignmentedas.p1eda.visualization.Visualizer} interface.
 * Visualizes POS and dependency relation annotations and alignments.
 * Provide GUI for filtering annotations and alignments. 
 * 
 * @author Meni Adler
 * @since Jan 6, 2015
 *
 */


import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.CasUtil;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

import eu.excitement.type.alignment.Link;
import eu.excitementproject.eop.alignmentedas.p1eda.TEDecisionWithAlignment;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.ValueException;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;

public class P1EdaVisualizer implements Visualizer {

		public static HashMap<String, String> hashPOS = new HashMap<String, String>();
		public static HashMap<String, HashMap<String, String>> hashRel = new HashMap<String, HashMap<String, String>>();
		public static HashMap<String, String> hashTEEntities = new HashMap<String, String>();
		
		public static String strDocEntities = "var collData = { entity_types: [ \r\n";
		
		public static String strDocText;
		public static String strDocData;
		
		
		public static String docAlignmentData = "docData['alignment_entity'] = [ \r\n";
		public static String strRelationEntities = "collData['relation_types'] = [ \r\n";
		public static HashMap<String, String> hashAlignmentData = new HashMap<String, String>();
		public static HashMap<String, String> hashAlignmentEntities = new HashMap<String, String>();
		public static String strRelationData = "docData['relations'] = [ \r\n";
		public static String strHtml = "";
		
		public String generateHTML(TEDecisionWithAlignment decision) throws VisualizerGenerationException
		{
			JCas jCas = decision.getJCasWithAlignment();
			Vector<FeatureValue> featureValues = decision.getFeatureVector();
			DecisionLabel label = decision.getDecision();
			Double confidence = decision.getConfidence();
			
			try {
				return generateHTML(jCas, label.toString(), confidence.toString(), featureValues);
			} catch (ValueException e) {
				throw new VisualizerGenerationException(e);
			}
		}
		
		public String generateHTML(JCas jCas) throws VisualizerGenerationException
		{
			try {
				return generateHTML(jCas,null, null, null);
			} catch (ValueException e) {
				throw new VisualizerGenerationException(e);
			}
		}
		protected String generateHTML(JCas jCas,String strDecisionLabel, String confidence , Vector<FeatureValue> featureValues ) throws ValueException
		{
			String alignmentEntityColor = "#88ccFf";
			String entityPOSColor = "#7fffa2";
			String relationAlignColor = "blue";
			String relationDEPColor = "green";
			
			
			HashMap<String,Boolean> entities = new HashMap<String,Boolean>();
			HashMap<String,Boolean> relationEntities = new HashMap<String,Boolean>();
			HashMap<String,Boolean> alignmentEntities = new HashMap<String,Boolean>();
			strDocEntities = "var collData = { entity_types: [ \r\n";
			strDocText = "var docData = { \r\n";
			strDocData = "docData['entities'] = [ \r\n";
			strRelationEntities = "collData['relation_types'] = [ \r\n";
			strRelationData = "docData['relations'] = [ \r\n";
			strHtml = "";
			
			int countInstances = 0;
			int countRelation = 0;
			try {
				JCas jCasText = jCas.getView("TextView");
				JCas jCasHypothesis = jCas.getView("HypothesisView");
				
				strDocText += " text     : \""+jCasText.getDocumentText()+"\\r\\n"+jCasHypothesis.getDocumentText()+"\"\r\n";
				
				int TextSize = jCasText.getDocumentText().length()+2;
				Collection<AnnotationFS> col = CasUtil.selectAll(jCasText.getCas());
				Collection<AnnotationFS> colH = CasUtil.selectAll(jCasHypothesis.getCas());

				checkAllTypes(col);
				checkAllTypes(colH);
				printAllTypes();
				
				hashPOS = new HashMap<String, String>();
				hashRel = new HashMap<String, HashMap<String, String>>();
				//check if there is Dependency
				boolean hasDependency = getIfThereIsDependency(col);
				
				if(hasDependency)
				{
					
					
					//for text sentence
					updateEntitiesAndRelations(col, 0);
					//for hypothesis sentence
					updateEntitiesAndRelations(colH, TextSize);
					
					// adding the POS collection and data
					for (String entity : hashPOS.keySet()) {
						String strVal = hashPOS.get(entity);
						if(!entities.containsKey(strVal))
						{
							if(entities.keySet().size()!=0)
								strDocEntities+=", \r\n";
							
							strDocEntities+=" { \r\n";
							strDocEntities+=" type   : '"+strVal+"', \r\n";
							strDocEntities+=" labels : ['"+strVal+"'], \r\n";
							strDocEntities+=" bgColor: '"+entityPOSColor+"', \r\n";
							strDocEntities+=" borderColor: 'darken' \r\n";
							strDocEntities+="} \r\n";
							
							entities.put(strVal, true);
						}
						int indexOfS = entity.indexOf("S");
						String  begin = entity.substring(1,indexOfS);
						String  end = entity.substring(indexOfS+1);
						//System.out.println("entity: "+entity+" begin: " + begin + " end: " + end );
						strDocData += " ['"+entity+"', '"+strVal+"', [["+begin+", "+end+"]]], \r\n";
					}
					
					// adding the relations collection and data
					for (String fromRelation : hashRel.keySet()) {
						HashMap<String, String> hashTo = hashRel.get(fromRelation);
						for (String toRelation : hashTo.keySet()) {
							String type = hashTo.get(toRelation);
							
							if(!relationEntities.containsKey(type))
							{
								if(relationEntities.keySet().size()!=0)
									strRelationEntities+=", \r\n";
								
								strRelationEntities += " { \r\n";
								strRelationEntities += "     type     : '"+type+"', \r\n";
								strRelationEntities += "     labels   : ['"+type+"'], \r\n";
								strRelationEntities += "     dashArray: '3,3', \r\n";
								strRelationEntities += "     color    : '"+relationDEPColor+"', \r\n";
								strRelationEntities += "     args     : [ \r\n";
								strRelationEntities += "            {role: 'From'},\r\n";
								strRelationEntities += "           {role: 'To'}\r\n";
								strRelationEntities += "        ] \r\n";
								strRelationEntities += " } \r\n";
								relationEntities.put(type, true);
							}
							
							strRelationData += " ['R"+(++countRelation)+"', '"+type+"', [['From', '"+fromRelation+"'], ['To', '"+toRelation+"']]], \r\n";
						}
					}
					
					
				}
				else
				{
					//for text sentence
					for (AnnotationFS annotationFS : col) {
						Type type = annotationFS.getType();
						String typeShortName = type.getShortName();
						int begin = annotationFS.getBegin();
						int end = annotationFS.getEnd();
						String strVal = "";
						/*if(typeShortName.equals("Lemma"))
						{
							strVal=((Lemma)annotationFS).getValue();
							if(strVal.contains("'"))
								strVal=strVal.replaceAll("'", "\\\\'");
		
							if(!entities.containsKey(strVal))
							{
								if(entities.keySet().size()!=0)
									strDocEntities+=", ";
								
								strDocEntities+=" { ";
								strDocEntities+=" type   : '"+strVal+"', ";
								strDocEntities+=" labels : ['"+strVal+"'], ";
								strDocEntities+=" bgColor: '#7fa2ff', ";
								strDocEntities+=" borderColor: 'darken' ";
								strDocEntities+="} ";
								entities.put(strVal, true);
							}
							strDocData += " ['T"+(++countInstances)+"', '"+strVal+"', [["+begin+", "+end+"]]], ";
						}*/
						if(type.toString().startsWith("de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos"))
						{
							strVal=((POS)annotationFS).getPosValue();
							if(!entities.containsKey(strVal))
							{
								if(entities.keySet().size()!=0)
									strDocEntities+=", \r\n";
								
								strDocEntities+=" { \r\n";
								strDocEntities+=" type   : '"+strVal+"', \r\n";
								strDocEntities+=" labels : ['"+strVal+"'], \r\n";
								strDocEntities+=" bgColor: '"+entityPOSColor+"', \r\n";
								strDocEntities+=" borderColor: 'darken' \r\n";
								strDocEntities+="} \r\n";
								
								entities.put(strVal, true);
							}
							strDocData += " ['T"+(++countInstances)+"', '"+strVal+"', [["+begin+", "+end+"]]], \r\n";
						}
					}
					
					//for hypothesis sentence
					for (AnnotationFS annotationFS : colH) {
						int begin = annotationFS.getBegin()+TextSize;
						int end = annotationFS.getEnd()+TextSize;
						String strVal = "";
						Type type = annotationFS.getType();
						String typeShortName = type.getShortName();
						if(type.toString().startsWith("de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos"))
						{
							strVal=((POS)annotationFS).getPosValue();
							if(!entities.containsKey(strVal))
							{
								if(entities.keySet().size()!=0)
									strDocEntities+=", \r\n";
								strDocEntities+=" { \r\n";
								strDocEntities+=" type   : '"+strVal+"', \r\n";
								strDocEntities+=" labels : ['"+strVal+"'], \r\n";
								strDocEntities+=" bgColor: '"+entityPOSColor+"', \r\n";
								strDocEntities+=" borderColor: 'darken' \r\n";
								strDocEntities+="} \r\n";
								entities.put(strVal, true);
							}
							strDocData += " ['T"+(++countInstances)+"', '"+strVal+"', [["+begin+", "+end+"]]], \r\n";
							
						}
						
					}
				}
				
				
				
				// get "Link" type annotations back..
				for (Link l : JCasUtil.select(jCasHypothesis, Link.class))
				{
					// you can access Link, as normal, annotation. Of course.
					int tBegin = l.getTSideTarget().getBegin();
					int hBegin = l.getHSideTarget().getBegin()+TextSize;
					int tEnd = l.getTSideTarget().getEnd();
					int hEnd = l.getHSideTarget().getEnd()+TextSize;
					String tText = l.getTSideTarget().getCoveredText();
					String hText = l.getHSideTarget().getCoveredText();

					if(tText.contains("'"))
						tText=tText.replaceAll("'", "\\\\'");
					if(hText.contains("'"))
						hText=hText.replaceAll("'", "\\\\'");
					
					if(!entities.containsKey(tText+"Sred"))
					{
						if(entities.keySet().size()!=0)
							strDocEntities+=", \r\n";
						
						strDocEntities+=" { \r\n";
						strDocEntities+=" type   : '"+tText+"Sred"+"', \r\n";
						strDocEntities+=" labels : ['"+tText+"'], \r\n";
						strDocEntities+=" bgColor: '"+alignmentEntityColor+"', \r\n";
						strDocEntities+=" borderColor: 'darken' \r\n";
						strDocEntities+="} \r\n";
						
						entities.put(tText, true);
					}
					if(!alignmentEntities.keySet().contains(tText+tBegin+"S"+tEnd))
					{
						String key = "TE"+(++countInstances);
						String value = " ['"+key+"', '"+tText+"Sred"+"', [["+tBegin+", "+tEnd+"]]], \r\n";
						if(!hashTEEntities.keySet().contains(key))
							hashTEEntities.put(key, value);
						docAlignmentData += value;
						alignmentEntities.put(tText+tBegin+"S"+tEnd, true);
					}
					
					
					if(!entities.containsKey(hText+"Sred"))
					{
						if(entities.keySet().size()!=0)
							strDocEntities+=", \r\n";
						
						strDocEntities+=" { \r\n";
						strDocEntities+=" type   : '"+hText+"Sred"+"', \r\n";
						strDocEntities+=" labels : ['"+hText+"'], \r\n";
						strDocEntities+=" bgColor: '"+alignmentEntityColor+"', \r\n";
						strDocEntities+=" borderColor: 'darken' \r\n";
						strDocEntities+="} \r\n";
						
						entities.put(hText, true);
					}
					if(!alignmentEntities.keySet().contains(hText+hBegin+"S"+hEnd))
					{
						String key = "TE"+(++countInstances);
						String value = " ['"+key+"', '"+hText+"Sred"+"', [["+hBegin+", "+hEnd+"]]], \r\n";
						if(!hashTEEntities.keySet().contains(key))
							hashTEEntities.put(key, value);
						docAlignmentData += value;
						alignmentEntities.put(hText+hBegin+"S"+hEnd, true);
					}
					
					String relation = l.getID() + " (" + l.getStrength() + ")";
					String []strSplit = l.getID().split("_");
					//System.out.println(relation);
					if(!relationEntities.containsKey(relation))
					{
						if(relationEntities.keySet().size()!=0)
							strRelationEntities+=", \r\n";
						
						strRelationEntities += " { \r\n";
						strRelationEntities += "     type     : '"+relation+"', \r\n";
						strRelationEntities += "     labels   : ['"+relation+"'], \r\n";
						strRelationEntities += "     dashArray: '3,3', \r\n";
						strRelationEntities += "     color    : '"+relationAlignColor+"', \r\n";
						strRelationEntities += "     args     : [ \r\n";
						strRelationEntities += "            {role: 'From'},\r\n";
						strRelationEntities += "           {role: 'To'}\r\n";
						strRelationEntities += "        ] \r\n";
						strRelationEntities += " } \r\n";
						relationEntities.put(relation, true);
					}
					
					if(!hashAlignmentData.keySet().contains(strSplit[0]))
						hashAlignmentData.put(strSplit[0], "docData['relations_"+strSplit[0]+"'] = [ ");
					
					if(!hashAlignmentEntities.keySet().contains(strSplit[0]))
						hashAlignmentEntities.put(strSplit[0], "docData['alignment_entity_"+strSplit[0]+"'] = [ ");
					
					
					
					
					hashAlignmentEntities.put(strSplit[0], hashAlignmentEntities.get(strSplit[0]) + hashTEEntities.get("TE"+(countInstances-1)));
					hashAlignmentEntities.put(strSplit[0], hashAlignmentEntities.get(strSplit[0]) + hashTEEntities.get("TE"+(countInstances)));
					
					//strRelationData += strRelationInstance;
					String strRelationInstance =  " ['RE"+(++countRelation)+"', '"+relation+"', [['From', 'TE"+(countInstances-1)+"'], ['To', 'TE"+countInstances+"']]], \r\n";
					hashAlignmentData.put(strSplit[0], hashAlignmentData.get(strSplit[0]) + strRelationInstance);
					
					
					//System.out.println("Its direction is: " + l.getDirection().toString());
					//System.out.println("l.getAddress(): " +l.getAddress());
					
				}
				
				
				strDocEntities += " ] \r\n";
				strDocEntities += " }; \r\n";	
				strDocData += " ]; \r\n";
				strDocText += " }; \r\n";
				
				
				
				docAlignmentData += " ]; \r\n";
				strRelationEntities += "  ]; \r\n";
				strRelationData += "  ]; \r\n";
				
				
				
				
				for (String strBlock : hashAlignmentData.keySet()) {
					hashAlignmentData.put(strBlock, hashAlignmentData.get(strBlock) + "  ]; ");
				}
				
				for (String strBlock : hashAlignmentEntities.keySet()) {
					hashAlignmentEntities.put(strBlock, hashAlignmentEntities.get(strBlock) + "  ]; ");
				}
				
				
				strHtml += "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd\">\r\n";
				strHtml += " <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n";
				strHtml += " <HTML lang=\"en-GB\" lang=\"en-GB\" xml:lang=\"en-GB\" xmlns=\"http://www.w3.org/1999/xhtml\">\r\n";
				strHtml += " <HEAD>  \r\n";
				strHtml += " <META http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\r\n";     
				strHtml += " <TITLE>EOP Visualizar</TITLE>\r\n";
				
				
				strHtml += "<STYLE>" +  GetCss() + " body { width:100%;  text-align: center;  }</STYLE>\r\n";
				//strHtml += " <LINK href=\"style-vis.css\"\r\n"; 
				//strHtml += " rel=\"stylesheet\" type=\"text/css\">         \r\n";
				strHtml += " <SCRIPT language=\"javascript\" type=\"text/javascript\">(function (a) { var b = a.documentElement, c, d, e = [], f = [], g = {}, h = {}, i = a.createElement(\"script\").async === true || \"MozAppearance\" in a.documentElement.style || window.opera; var j = window.head_conf && head_conf.head || \"head\", k = window[j] = window[j] || function () { k.ready.apply(null, arguments) }; var l = 0, m = 1, n = 2, o = 3; i ? k.js = function () { var a = arguments, b = a[a.length - 1], c = []; t(b) || (b = null), s(a, function (d, e) { d != b && (d = r(d), c.push(d), x(d, b && e == a.length - 2 ? function () { u(c) && p(b) } : null)) }); return k } : k.js = function () { var a = arguments, b = [].slice.call(a, 1), d = b[0]; if (!c) { f.push(function () { k.js.apply(null, a) }); return k } d ? (s(b, function (a) { t(a) || w(r(a)) }), x(r(a[0]), t(d) ? d : function () { k.js.apply(null, b) })) : x(r(a[0])); return k }, k.ready = function (a, b) { if (a == \"dom\") { d ? p(b) : e.push(b); return k } t(a) && (b = a, a = \"ALL\"); var c = h[a]; if (c && c.state == o || a == \"ALL\" && u() && d) { p(b); return k } var f = g[a]; f ? f.push(b) : f = g[a] = [b]; return k }, k.ready(\"dom\", function () { c && u() && s(g.ALL, function (a) { p(a) }), k.feature && k.feature(\"domloaded\", true) }); function p(a) { a._done || (a(), a._done = 1) } function q(a) { var b = a.split(\"/\"), c = b[b.length - 1], d = c.indexOf(\"?\"); return d != -1 ? c.substring(0, d) : c } function r(a) { var b; if (typeof a == \"object\") for (var c in a) a[c] && (b = { name: c, url: a[c] }); else b = { name: q(a), url: a }; var d = h[b.name]; if (d && d.url === b.url) return d; h[b.name] = b; return b } function s(a, b) { if (a) { typeof a == \"object\" && (a = [].slice.call(a)); for (var c = 0; c < a.length; c++) b.call(a, a[c], c) } } function t(a) { return Object.prototype.toString.call(a) == \"[object Function]\" } function u(a) { a = a || h; var b = false, c = 0; for (var d in a) { if (a[d].state != o) return false; b = true, c++ } return b || c === 0 } function v(a) { a.state = l, s(a.onpreload, function (a) { a.call() }) } function w(a, b) { a.state || (a.state = m, a.onpreload = [], y({ src: a.url, type: \"cache\" }, function () { v(a) })) } function x(a, b) { if (a.state == o && b) return b(); if (a.state == n) return k.ready(a.name, b); if (a.state == m) return a.onpreload.push(function () { x(a, b) }); a.state = n, y(a.url, function () { a.state = o, b && b(), s(g[a.name], function (a) { p(a) }), d && u() && s(g.ALL, function (a) { p(a) }) }) } function y(c, d) { var e = a.createElement(\"script\"); e.type = \"text/\" + (c.type || \"javascript\"), e.src = c.src || c, e.async = false, e.onreadystatechange = e.onload = function () { var a = e.readyState; !d.done && (!a || /loaded|complete/.test(a)) && (d(), d.done = true) }, b.appendChild(e) } setTimeout(function () { c = true, s(f, function (a) { a() }) }, 0); function z() { d || (d = true, s(e, function (a) { p(a) })) } window.addEventListener ? (a.addEventListener(\"DOMContentLoaded\", z, false), window.addEventListener(\"onload\", z, false)) : window.attachEvent && (a.attachEvent(\"onreadystatechange\", function () { a.readyState === \"complete\" && z() }), window.frameElement == null && b.doScroll && function () { try { b.doScroll(\"left\"), z() } catch (a) { setTimeout(arguments.callee, 1); return } } (), window.attachEvent(\"onload\", z)), !a.readyState && a.addEventListener && (a.readyState = \"loading\", a.addEventListener(\"DOMContentLoaded\", handler = function () { a.removeEventListener(\"DOMContentLoaded\", handler, false), a.readyState = \"complete\" }, false)) })(document)</SCRIPT>\r\n";  
				strHtml += " </HEAD>\r\n";
				strHtml += " <BODY>\r\n";
				
				strHtml += " <DIV id=\"content\" style=\"margin: 0px auto; width:96%;\">\r\n";
				
				strHtml += " <h2 id=\"header\">Entailment Visualization</h2>";
				if (strDecisionLabel != null) {
					strHtml += "<h3>Decision: " + strDecisionLabel;
					if (confidence != null) {
						strHtml += ", Confidence: " + confidence;
					}
					strHtml += "</h3>";
				}
				
				strHtml += " <PRE style=\"display:none;\"><CODE id=\"embedding-call\">\r\n";
				strHtml += " head.ready(function() {\r\n";
				strHtml += "     Util.embed(\r\n";
				strHtml += "         '${DIV_ID}',\r\n";
				strHtml += " collData,\r\n";
				strHtml += " docData,\r\n";
				strHtml += " webFontURLs\r\n";
				strHtml += "     );\r\n";
				strHtml += " });\r\n";
				strHtml += " </CODE></PRE>\r\n";
				strHtml += " <PRE style=\"display:none;\"><CODE id=\"embedding-entity-coll\"> \r\n";
				strHtml +=  strDocEntities ;
				strHtml += " </CODE></PRE>\r\n";
				strHtml += " <PRE style=\"display:none;\"><CODE id=\"embedding-text-doc\"> \r\n";
				strHtml +=  strDocText ;
				strHtml += " </CODE></PRE>\r\n";
				
				strHtml += " <PRE style=\"display:none;\"><CODE id=\"embedding-init-doc\">\r\n"; 
				strHtml += "  docData['entities'] = [ ];\r\n";
				strHtml += "  docData['relations'] = [ ];\r\n";
				strHtml += " </CODE></PRE>\r\n";
				
				strHtml += " <PRE style=\"display:none;\"><CODE id=\"embedding-entity-doc\"> \r\n";
				strHtml +=  strDocData ;
				strHtml += " </CODE></PRE>\r\n";
				strHtml += " <PRE style=\"display:none;\"><CODE id=\"embedding-alignment-entity-doc\"> \r\n";
				strHtml +=  docAlignmentData ;
				strHtml += " </CODE></PRE>\r\n";
				
				strHtml += " <PRE style=\"display:none;\"><CODE id=\"embedding-relation-coll\"> \r\n";
				strHtml +=  strRelationEntities ;
				strHtml += " </CODE></PRE>\r\n";
				strHtml += " <PRE style=\"display:none;\"><CODE id=\"embedding-relation-doc\"> \r\n";
				strHtml +=  strRelationData ;
				strHtml += " </CODE></PRE>\r\n";
				
				
				
				for (String strBlock : hashAlignmentData.keySet()) {
					strHtml += " <PRE style=\"display:none;\"><CODE id=\"embedding-relation-"+strBlock+"-doc\"> \r\n";
					
					strHtml +=  hashAlignmentEntities.get(strBlock) + "\r\n";
					strHtml +=  hashAlignmentData.get(strBlock) ;
					strHtml += " </CODE></PRE>\r\n";
					hashAlignmentData.put(strBlock, hashAlignmentData.get(strBlock) + "  ]; ");
				}
				
				//strHtml += " <DIV id=\"embedding-relation-example\"></DIV>\r\n";
				strHtml += " <DIV id=\"embedding-live-example\"></DIV>\r\n";
				
				strHtml += " <div style='width:100%;  text-align: center;'>";
				strHtml += " <table style='width:700px; margin: 0px auto;'><tr>\r\n";
				strHtml += "<td><input id=\"cb_DEP\" type=\"checkbox\" onclick='Update();' checked=\"checked\" />Dependency</td>\r\n";
				strHtml += "<td><input id=\"cb_POS\" type=\"checkbox\" onclick='Update();' checked=\"checked\" />POS</td>\r\n";
				for (String strBlock : hashAlignmentData.keySet()) {
					strHtml += "    <td><input id=\"cb_"+strBlock+"\" type=\"checkbox\" onclick=\"javascript:Update();\" checked=\"checked\" />"+strBlock+"</td>\r\n";
				}
				strHtml += "</tr></table><br/><hr/>\r\n";
				strHtml += "</div>";
				
				if(featureValues!=null)
				{
					strHtml += "<div><h3>Extracted features</h3></div>   " ;
					
					strHtml += "<style>.datagrid table { border-collapse: collapse; text-align: left; width: 100%; } .datagrid {font: normal 12px/150% Arial, Helvetica, sans-serif; background: #fff; overflow: hidden; border: 3px solid #006699; -webkit-border-radius: 11px; -moz-border-radius: 11px; border-radius: 11px; }.datagrid table td, .datagrid table th { padding: 4px 4px; }.datagrid table thead th {background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 100% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; color:#FFFFFF; font-size: 15px; font-weight: bold; border-left: 2px solid #E1EEF4; } .datagrid table thead th:first-child { border: none; }.datagrid table tbody td { color: #00496B; border-left: 1px solid #E1EEF4;font-size: 14px;font-weight: normal; }.datagrid table tbody .alt td { background: #E1EEF4; color: #00496B; }.datagrid table tbody td:first-child { border-left: none; }.datagrid table tbody tr:last-child td { border-bottom: none; }.datagrid table tfoot td div { border-top: 1px solid #006699;background: #E1EEF4;} .datagrid table tfoot td { padding: 0; font-size: 12px } .datagrid table tfoot td div{ padding: 2px; }.datagrid table tfoot td ul { margin: 0; padding:0; list-style: none; text-align: right; }.datagrid table tfoot  li { display: inline; }.datagrid table tfoot li a { text-decoration: none; display: inline-block;  padding: 2px 8px; margin: 1px;color: #FFFFFF;border: 1px solid #006699;-webkit-border-radius: 3px; -moz-border-radius: 3px; border-radius: 3px; background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 100% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; }.datagrid table tfoot ul.active, .datagrid table tfoot ul a:hover { text-decoration: none;border-color: #006699; color: #FFFFFF; background: none; background-color:#00557F;}div.dhtmlx_window_active, div.dhx_modal_cover_dv { position: fixed !important; }</style>";
					
					strHtml += " <div style='width:100%;  text-align: center;'>";
					strHtml += "<div style='width: 400px; margin: 0px auto;' class=\"datagrid\"><table>";
					strHtml += "<thead><tr><th style='width: 70%;'>header</th><th>header</th></tr></thead><tbody>";
					
					
					boolean bRow = true;
					for (FeatureValue featureValue : featureValues) {
						if(bRow)
							strHtml += "<tr>";
						else 
							strHtml += "<tr class=\"alt\">";
						
						strHtml += "<td>"+featureValue.getFeatureName()+"</td><td>"+featureValue.getDoubleValue()+"</td></tr>"; 
						bRow = !bRow;
						
					}
					strHtml += "</tbody></table></div></div><br/><br/>";
					
				
				}
				
				strHtml += " <DIV id=\"live-io\" style=\"display:none;\">\r\n";
				strHtml += " <P><TEXTAREA id=\"coll-input\" style=\"border: 2px inset currentColor; border-image: none; width: 40%; height: 400px; font-size: 11px; float: left; display: block;\" placeholder=\"Enter JSON for the collection object here...\">Enter JSON for the collection object here...</TEXTAREA>\r\n";
				strHtml += " <TEXTAREA id=\"doc-input\" style=\"border: 2px inset currentColor; border-image: none; width: 55%; height: 400px; font-size: 11px; float: right; display: block;\" placeholder=\"Enter JSON for the document object here...\">Enter JSON for the document object here...</TEXTAREA></P></DIV>\r\n";
				strHtml += " <STYLE type=\"text/css\">\r\n";
				strHtml += " text { font-size: 15px; }\r\n";
				strHtml += " .span text { font-size: 10px; }\r\n";
				strHtml += " .arcs text { font-size: 9px; }\r\n";
				strHtml += " </STYLE>\r\n";
	 
				
				strHtml += " <SCRIPT type=\"text/javascript\">\r\n";
				
				strHtml += " var packJSON = function (s) { \r\n";
			    strHtml += "     s = s.replace(/(\\{[^\\{\\}\\[\\]]*\\})/g,\r\n";
				strHtml += "                       function (a, b) { return b.replace(/\\s+/g, ' '); });\r\n";
			    strHtml += "     s = s.replace(/(\\[(?:[^\\[\\]\\{\\}]|\\[[^\\[\\]\\{\\}]*\\])*\\])/g,\r\n";
				strHtml += "                       function (a, b) { return b.replace(/\\s+/g, ' '); });\r\n";
				strHtml += "     return s;\r\n";
				strHtml += " } \r\n";
				
				
				strHtml += "     var bratLocation = 'http://weaver.nlplab.org/~brat/demo/v1.3';\r\n";
				strHtml += "     head.js(\r\n";
				strHtml += "         bratLocation + '/client/lib/jquery.min.js',\r\n";
				strHtml += "         bratLocation + '/client/lib/jquery.svg.min.js',\r\n";
				strHtml += "         bratLocation + '/client/lib/jquery.svgdom.min.js',\r\n";
				strHtml += "         bratLocation + '/client/src/configuration.js',\r\n";
				strHtml += "         bratLocation + '/client/src/util.js',\r\n";
				strHtml += "         bratLocation + '/client/src/annotation_log.js',\r\n";
				strHtml += "         bratLocation + '/client/lib/webfont.js',\r\n";
				strHtml += "         bratLocation + '/client/src/dispatcher.js',\r\n";
				strHtml += " 		 bratLocation + '/client/src/url_monitor.js',\r\n";
				strHtml += "         bratLocation + '/client/src/visualizer.js'\r\n";
				strHtml += "     );\r\n";
				strHtml += "     var webFontURLs = [\r\n";
				strHtml += "         bratLocation + '/static/fonts/Astloch-Bold.ttf',\r\n";
				strHtml += "         bratLocation + '/static/fonts/PT_Sans-Caption-Web-Regular.ttf',\r\n";
				strHtml += "         bratLocation + '/static/fonts/Liberation_Sans-Regular.ttf'\r\n";
				strHtml += "     ];\r\n";
				strHtml += "     var liveDispatcher;\r\n";
				strHtml += "     head.ready(function () {\r\n";
				strHtml += "         document.getElementById(\"cb_POS\").disabled = true;\r\n";
				strHtml += "         eval($('#embedding-entity-coll').text());\r\n";
				strHtml += "         eval($('#embedding-text-doc').text());\r\n";
				strHtml += "         eval($('#embedding-entity-doc').text());\r\n";
				strHtml += "      	 eval($('#embedding-alignment-entity-doc').text());";
				strHtml += "      	 docData['entities'] = docData['entities'].concat(docData['alignment_entity']);";

				strHtml += "         eval($('#embedding-relation-coll').text());\r\n";
				strHtml += "         eval($('#embedding-relation-doc').text());\r\n";
				
				for (String strBlock : hashAlignmentData.keySet()) {
					strHtml += " 	 	eval($('#embedding-relation-"+strBlock+"-doc').text());\r\n";
					strHtml += " 	 	docData['relations'] = docData['relations'].concat(docData['relations_"+strBlock+"']);\r\n";
				}
				//strHtml += "         Util.embed('embedding-relation-example', $.extend({}, collData),\r\n";
				//strHtml += "                 $.extend({}, docData), webFontURLs);   \r\n";
				
				
				strHtml += "         var collInput = $('#coll-input');\r\n";
				strHtml += "         var docInput = $('#doc-input');\r\n";
				strHtml += "         var liveDiv = $('#embedding-live-example');\r\n";

				strHtml += "         liveDispatcher = Util.embed('embedding-live-example',\r\n";
				strHtml += "                 $.extend({ 'collection': null }, collData),\r\n";
				strHtml += "                 $.extend({}, docData), webFontURLs);\r\n";
		        
				strHtml += "         var renderError = function () {\r\n";
				strHtml += "             collInput.css({ 'border': '2px solid red' });\r\n";
				strHtml += "             docInput.css({ 'border': '2px solid red' });\r\n";
				strHtml += "         };\r\n";
				strHtml += "         liveDispatcher.on('renderError: Fatal', renderError);\r\n";
				strHtml += "         var collInputHandler = function () {\r\n";
				strHtml += "             var collJSON;\r\n";
				strHtml += "             try {\r\n";
				strHtml += "                 collJSON = JSON.parse(collInput.val());\r\n";
				strHtml += "                 collInput.css({ 'border': '2px inset' });\r\n";
				strHtml += "             } catch (e) {\r\n";
				strHtml += "                 collInput.css({ 'border': '2px solid red' });\r\n";
				strHtml += "                 return;\r\n";
				strHtml += "             }\r\n";

				strHtml += "             try {\r\n";
				strHtml += "                 liveDispatcher.post('collectionLoaded',[$.extend({ 'collection': null }, collJSON)]);\r\n";
				strHtml += "                 docInput.css({ 'border': '2px inset' });\r\n";
				strHtml += "             } catch (e) {\r\n";
				strHtml += "                 console.error('collectionLoaded went down with:', e);\r\n";
				strHtml += "                 collInput.css({ 'border': '2px solid red' });\r\n";
				strHtml += "             }\r\n";
				strHtml += "         };\r\n";

				strHtml += "         var docInputHandler = function () {\r\n";
				strHtml += "             var docJSON;\r\n";
				strHtml += "             try {\r\n";
				strHtml += "                 docJSON = JSON.parse(docInput.val());\r\n";
				strHtml += "                 docInput.css({ 'border': '2px inset' });\r\n";
				strHtml += "             } catch (e) {\r\n";
				strHtml += "                 docInput.css({ 'border': '2px solid red' });\r\n";
				strHtml += "                 return;\r\n";
				strHtml += "             }\r\n";

				strHtml += "             try {\r\n";
				strHtml += "                 liveDispatcher.post('requestRenderData', [$.extend({}, docJSON)]);\r\n";
				strHtml += "                 collInput.css({ 'border': '2px inset' });\r\n";
				strHtml += "             } catch (e) {\r\n";
				strHtml += "                 console.error('requestRenderData went down with:', e);\r\n";
				strHtml += "                 collInput.css({ 'border': '2px solid red' });\r\n";
				strHtml += "             }\r\n";
				strHtml += "         };\r\n";

				strHtml += "         var collJSON = JSON.stringify(collData, undefined, '    ');\r\n";
		        strHtml += "         docJSON = JSON.stringify(docData, undefined, '    ')\r\n";
		        
		        strHtml += "         collInput.text(packJSON(collJSON));\r\n";
		        strHtml += "         docInput.text(packJSON(docJSON));\r\n";

		        strHtml += "         var listenTo = 'propertychange keyup input paste';\r\n";
		        strHtml += "         collInput.bind(listenTo, collInputHandler);\r\n";
		        strHtml += "         docInput.bind(listenTo, docInputHandler);\r\n";
				
				
				strHtml += "     });\r\n";
				strHtml += " </SCRIPT></DIV>\r\n";
				

				strHtml += " 	<div>\r\n";
					
				strHtml += " 	  <script language=\"javascript\" type=\"text/javascript\">\r\n";
				strHtml += " 	    function Update() {\r\n";
				strHtml += "           docData = \"\";\r\n";
				
				strHtml += "      	   eval($('#embedding-entity-coll').text());\r\n";
				strHtml += "      	   eval($('#embedding-relation-coll').text());\r\n";
				
				strHtml += "      	   eval($('#embedding-text-doc').text());\r\n";
				strHtml += "      	   eval($('#embedding-init-doc').text());\r\n";
				strHtml += "      	   var is_DEP = document.getElementById(\"cb_DEP\").checked;\r\n";
				strHtml += "      	   var chkPOS = document.getElementById(\"cb_POS\");\r\n";
				strHtml += "      	   var is_POS = chkPOS.checked;\r\n";
				strHtml += "      	   if (is_DEP) {\r\n";
				strHtml += "      	         eval($('#embedding-relation-doc').text());\r\n";
				strHtml += "      	         eval($('#embedding-entity-doc').text());\r\n";
				strHtml += "      	         chkPOS.checked = true;\r\n";
				strHtml += "      	         chkPOS.disabled = true;\r\n";
				strHtml += "      	   } else if (is_POS) {\r\n";
				strHtml += "      	   		 eval($('#embedding-entity-doc').text());\r\n";
				strHtml += "      	   		 chkPOS.disabled = false;\r\n";
				strHtml += "      	   }\r\n";
				
				
			for (String strBlock : hashAlignmentData.keySet()) {
				strHtml += " 	        var is_"+strBlock+" = document.getElementById(\"cb_"+strBlock+"\").checked;\r\n";
				strHtml += " 	        if (is_"+strBlock+") {\r\n";
				strHtml += " 	            eval($('#embedding-relation-"+strBlock+"-doc').text());\r\n";
				strHtml += " 	            docData['relations'] = docData['relations'].concat(docData['relations_"+strBlock+"']);\r\n";
				strHtml += " 	            docData['entities'] = docData['entities'].concat(docData['alignment_entity_"+strBlock+"']);\r\n";
				strHtml += " 	        }\r\n";
			}
				
				strHtml += " 	        var docJSON = JSON.stringify(docData, undefined, '    ');\r\n";
				strHtml += " 	        var docInput = $('#doc-input');\r\n";
				strHtml += " 	        docInput.text(packJSON(docJSON));\r\n";
				strHtml += " 	        docJSON = JSON.parse(docInput.val());\r\n";
				strHtml += " 	        liveDispatcher.post('requestRenderData', [$.extend({}, docJSON)]);\r\n";
					     
				strHtml += " 	    }\r\n";
				strHtml += " 	 </script>\r\n";
				strHtml += "   </div>\r\n";
				
				
				strHtml += "</BODY></HTML>\r\n";

				
			} catch (CASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			
			return strHtml;
		}
		
		
		private static String GetCss() {
			String ret = "";
			ret += "@font-face {font-family: Liberation Sans;src: local(\"Liberation Sans\"), local(\"Liberation-Sans\"), url(static/fonts/Liberation_Sans-Regular.ttf) format(\"truetype\");font-weight: normal;font-style: normal;}@font-face {font-family: PT Sans Caption;src: local(\"PT Sans Caption\"), local(\"PTSans-Caption\"), url(static/fonts/PT_Sans-Caption-Web-Regular.ttf) format(\"truetype\");font-weight: normal;font-style: normal;}#svg {margin: 34px auto 100px; padding-top: 15px;}.center_wrapper {left: 0px; top: 0px; width: 100%; height: 100%; display: table; position: absolute;}.center_wrapper > div {vertical-align: middle; display: table-cell;}.center_wrapper > div > div {width: 30em; color: rgb(46, 110, 158); font-family: \"Liberation Sans\", Verdana, Arial, Helvetica, sans-serif; font-size: 12px; margin-right: auto; margin-left: auto;}.center_wrapper > div > div h1 {text-align: center; font-size: 14px;}#no_svg_wrapper {display: none;}svg {border: 1px solid rgb(127, 162, 255); border-image: none; width: 100%; height: 1px; font-size: 15px;}svg.reselect {border: 1px solid rgb(255, 51, 51); border-image: none;}text {font-family: \"Liberation Sans\", Verdana, Arial, Helvetica, sans-serif; font-size: 13px;}path {pointer-events: none;}.span text {font-family: \"PT Sans Caption\", sans-serif; font-size: 10px; pointer-events: none; text-anchor: middle;}.span_type_label {font-family: \"PT Sans Caption\", sans-serif; font-size: 11px;}.arc_type_label {font-family: \"PT Sans Caption\", sans-serif; font-size: 11px;}.attribute_type_label .ui-button-text {font-family: \"PT Sans Caption\", sans-serif; font-size: 11px;}.span rect {stroke-width: 0.75;}.glyph {font-family: sans-serif; font-weight: bold; fill: #444444;}.attribute_warning {stroke: red;}.span rect.False_positive {stroke: #ff4141; stroke-width: 2;}.shadow_True_positive {fill: #00ff00;}.shadow_False_positive {fill: #ff4141;}.comment_False_positive#commentpopup {background-color: rgb(255, 65, 65);}.span rect.False_negative {fill: #ffffff; stroke: #c20000; stroke-width: 2;}.shadow_False_negative {fill: #c20000;}.comment_False_negative#commentpopup {background-color: rgb(194, 0, 0);}.span rect.AnnotationError {stroke-width: 1;}.shadow_AnnotationError {fill: #ff0000;}.comment_AnnotationError#commentpopup {background-color: rgb(255, 119, 119);}.span rect.AnnotationWarning {stroke-width: 1;}.shadow_AnnotationWarning {fill: #ff8800;}.comment_AnnotationWarning#commentpopup {background-color: rgb(255, 153, 0);}.shadow_AnnotatorNotes {fill: #3ab7ee;}.comment_AnnotatorNotes#commentpopup {background-color: rgb(215, 231, 238);}.shadow_Normalized {fill: #3aee37;}.comment_Normalized#commentpopup {background-color: rgb(215, 238, 231);}rect.Normalized {stroke-width: 1.5;}.shadow_AnnotationIncomplete {fill: #aaaaaa;}.span rect.AnnotationIncomplete {fill: #ffffff; stroke: #002200; stroke-width: 0.5;}.comment_AnnotationIncomplete#commentpopup {background-color: rgb(255, 255, 119);}.shadow_AnnotationUnconfirmed {fill: #eeeeee;}.span rect.AnnotationUnconfirmed {opacity: 0.5; stroke: #002200; stroke-width: 0.5;}.comment_AnnotationUnconfirmed#commentpopup {background-color: rgb(221, 221, 255);}.span rect.True_positive {}rect.shadow_EditHighlight {fill: #ffff99;}.shadow_EditHighlight_arc {stroke: #ffff99;}.span path {fill: none;}.span path.curly {stroke-width: 0.5;}.span path.boxcross {opacity: 0.5; stroke: black;}.arcs path {fill: none; stroke: #989898; stroke-width: 1;}.arcs .highlight path {opacity: 1; stroke: #000000; stroke-width: 1.5;}.arcs .highlight text {fill: black; stroke: black; stroke-width: 0.5;}.highlight.span rect {stroke-width: 2px;}.span rect.reselect {stroke-width: 2px;}.span rect.reselectTarget {stroke-width: 2px;}.arcs .reselect path {stroke: #ff0000 !important; stroke-width: 2px;}.arcs .reselect text {fill: #ff0000 !important;}.span rect.badTarget {stroke: #f00;}.arcs text {font-family: \"PT Sans Caption\", sans-serif; font-size: 9px; cursor: default; text-anchor: middle;}.background0 {fill: #ffffff; stroke: none;}.background1 {fill: #eeeeee; stroke: none;}.backgroundHighlight {fill: #ffff99; stroke: none;}.sentnum text {fill: #999999; text-anchor: end;}.sentnum path {stroke: #999999; stroke-width: 1px;}.span_cue {fill: #eeeeee !important;}.drag_stroke {stroke: black;}.drag_fill {fill: black;}.dialog {display: none;}#span_free_div {float: left;}#arc_free_div {float: left;}fieldset {border-radius: 5px; border: 1px solid rgb(166, 201, 226); border-image: none; margin-top: 5px; -webkit-border-radius: 5px; -moz-border-radius: 5px;}fieldset legend {border-radius: 3px; color: white; padding-right: 0.5em; padding-left: 0.5em; font-size: 90%; font-weight: bold; background-color: rgb(112, 168, 210); -webkit-border-radius: 3px; -moz-border-radius: 3px;}.label-like {color: rgb(46, 110, 158); font-family: monospace; font-size: 90%; font-weight: bold;}.accesskey {text-decoration: underline;}.shadow {box-shadow: 5px 5px 5px #444444; -moz-box-shadow: 5px 5px 5px #444444; -webkit-box-shadow: 5px 5px 5px #444444;}#span_selected {font-weight: bold;}#arc_origin {font-weight: bold;}#arc_target {font-weight: bold;}#commentpopup {padding: 10px; border-radius: 3px; border: 1px outset rgb(0, 0, 0); border-image: none; left: 0px; top: 0px; color: rgb(0, 0, 0); font-family: \"Liberation Sans\", Verdana, Arial, Helvetica, sans-serif; display: none; position: fixed; z-index: 20; max-width: 80%; opacity: 0.95; box-shadow: 5px 5px 5px #aaaaaa; background-color: rgb(245, 245, 249); -moz-box-shadow: 5px 5px 5px #aaaaaa; -webkit-box-shadow: 5px 5px 5px #aaaaaa; -webkit-border-radius: 3px; -moz-border-radius: 3px;}#more_info_readme {height: 350px;}#readme_container {position: relative;}#more_readme_button {padding: 2px 5px; top: -2px; right: -2px; position: absolute;}.comment_id {color: rgb(51, 51, 51); font-family: monospace; font-size: 75%; vertical-align: top; float: right;}.comment_type {}.comment_text {font-weight: bold;}.comment_type_id_wrapper {padding-right: 2em;}.norm_info_label {font-size: 80%; font-weight: bold;}.norm_info_value {font-size: 80%;}.norm_info_img {margin-left: 1em; float: right;}#search_form select {width: 100%;}.scroll_fset {height: 200px;}.scroll_fset fieldset {height: 100%; -ms-overflow-x: hidden; -ms-overflow-y: hidden;}.scroll_fset {margin-bottom: 2.5em;}.scroll_fset fieldset {padding-bottom: 2em;}.scroll_fset div.scroller {width: 100%; height: 100%; overflow: auto;}#span_highlight_link {float: right;}#arc_highlight_link {float: right;}#viewspan_highlight_link {float: right;}.unselectable {cursor: default; -moz-user-select: -moz-none; -khtml-user-select: none; -webkit-user-select: none; -o-user-select: none; user-select: none;}* {-webkit-tap-highlight-color: rgba(0, 0, 0, 0); -webkit-text-size-adjust: none; select: none;}.span rect.AddedAnnotation {stroke: #ff4141; stroke-width: 2;}.shadow_AddedAnnotation {fill: #ff4141;}.comment_AddedAnnotation#commentpopup {background-color: rgb(255, 204, 204);}.span rect.MissingAnnotation {stroke: #ffffff; stroke-width: 2;}.shadow_MissingAnnotation {opacity: 0.3; fill: #ff4141;}.comment_MissingAnnotation#commentpopup {background-color: rgb(255, 204, 204);}.span rect.MissingAnnotation + text {opacity: 0.5;}.span rect.ChangedAnnotation {stroke: #ffff99; stroke-width: 2;}.shadow_ChangedAnnotation {fill: #ff4141;}.comment_ChangedAnnotation#commentpopup {background-color: rgb(255, 204, 204);}";
			
			return ret;
		}

		private static void updateEntitiesAndRelations(Collection<AnnotationFS> col, int Identication) {
			for (AnnotationFS annotationFS : col) {
				Type type = annotationFS.getType();
				String typeShortName = type.getShortName();
				
				if(typeShortName.equals("Dependency"))
				{
					
					Token governor = ((Dependency)annotationFS).getGovernor();
					Token dependent = ((Dependency)annotationFS).getDependent();
					String dependencyType = ((Dependency)annotationFS).getDependencyType();
					
					String strGovernorId="T"+(governor.getBegin()+Identication)+"S"+(governor.getEnd()+Identication);
					String strDependentId="T"+(dependent.getBegin()+Identication)+"S"+(dependent.getEnd()+Identication);
					
					hashPOS.put(strGovernorId, governor.getPos().getPosValue());
					hashPOS.put(strDependentId, dependent.getPos().getPosValue());
					if(!hashRel.keySet().contains(strGovernorId))
						hashRel.put(strGovernorId, new HashMap<String, String>());
					
					hashRel.get(strGovernorId).put(strDependentId, dependencyType);
					
				}
			}
			
		}

		public static HashMap<String, Boolean> hashTypes = new HashMap<String, Boolean>();
		private static void checkAllTypes(Collection<AnnotationFS> col)
		{
			for (AnnotationFS annotationFS : col) {
				Type type = annotationFS.getType();
				if(!type.toString().startsWith("de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos"))
				{
					String typeShortName = type.getShortName();
					if(!hashTypes.keySet().contains(typeShortName))
						hashTypes.put(typeShortName, true);
				}
				
			}
			
		}
		private static void printAllTypes()
		{
			for (String strKey : hashTypes.keySet()) {
				System.out.println(strKey);
			}
		}
		
		
		private static boolean getIfThereIsDependency(Collection<AnnotationFS> col) {
			for (AnnotationFS annotationFS : col) {
				Type type = annotationFS.getType();
				String typeShortName = type.getShortName();
				if(typeShortName.equals("Dependency"))
					return true;
			}
			return false;
		}

		public static void main(String[] args) throws VisualizerGenerationException {
			
			JCas jCas;
			try {
				jCas = UimaUtils.loadXmi(new File("D:\\tmp\\xmi\\1.XMI"));
				
				
				Vector<FeatureValue> featureVector = new Vector<FeatureValue>();
				featureVector.add(new FeatureValue("feature1",0.1));
				featureVector.add(new FeatureValue("feature2",0.3));
				featureVector.add(new FeatureValue("feature3",0.7));
				TEDecisionWithAlignment decision = new                    
		                             TEDecisionWithAlignment(DecisionLabel.Entailment, 0.5, "", jCas, featureVector);
				
				//Set<String> drawnAnnotations = new Set<String>();
				Visualizer vis = new P1EdaVisualizer();
				String str = vis.generateHTML(decision);
				//String str = vis.generateHTML(jCas);
				
				try {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						    new FileOutputStream("temp.html")));
					bw.write(str);
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				File file = new File("temp.html");
				System.out.println(file.getAbsolutePath());
				
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			            desktop.browse(new URL("file:\\\\\\" + file.getAbsolutePath()).toURI());
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			    } 
				
			} catch (UimaUtilsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
