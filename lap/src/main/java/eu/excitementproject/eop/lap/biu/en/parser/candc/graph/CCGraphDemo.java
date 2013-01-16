package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraph;
import eu.excitementproject.eop.common.datastructures.dgraph.view.DirectedGraphToDot;
import eu.excitementproject.eop.common.utilities.Utils;


public class CCGraphDemo {
	protected static List<String> retrieveCandCOutput2()
	{
		String[] retArray = new String[]{


				"(ncmod _ controls_4 use_3)",
				"(ncmod _ controls_4 aircraft_2)",
				"(ncmod _ controls_4 small_1)",
				"(det controls_4 Some_0)",
				"(ncmod _ position_11 one_10)",
				"(dobj from_9 position_11)",
				"(ncmod _ moved_8 from_9)",
				"(ncmod _ yoke_19 single_18)",
				"(det yoke_19 a_17)",
				"(ncmod _ pilot_32 left-seat_31)",
				"(det pilot_32 the_30)",
				"(ncmod _ pilot_36 right-seat_35)",
				"(det pilot_36 the_34)",
				"(ncmod _ Bonanza_40 Beechcraft_39)",
				"(ncmod _ Bonanza_40 i.e._38)",
				"(dobj (_37 Bonanza_40)",
				"(ncmod _ pilot_36 (_37)",
				"(conj or_33 pilot_36)",
				"(conj or_33 pilot_32)",
				"(ncmod _ pilot_36 either_29)",
				"(ncmod _ pilot_32 either_29)",
				"(dobj of_28 pilot_36)",
				"(dobj of_28 pilot_32)",
				"(ncmod _ front_27 of_28)",
				"(dobj in_26 front_27)",
				"(ncmod _ position_25 in_26)",
				"(dobj into_24 position_25)",
				"(iobj swung_23 into_24)",
				"(aux swung_23 be_22)",
				"(aux be_22 can_21)",
				"(ncsubj swung_23 yoke_19 obj)",
				"(cmod that_20 yoke_19 can_21)",
				"(dobj as_16 yoke_19)",
				"(ncmod _ as_16 such_15)",
				"(ncmod _ another_13 as_16)",
				"(dobj to_12 another_13)",
				"(ncmod _ moved_8 to_12)",
				"(aux moved_8 be_7)",
				"(aux be_7 can_6)",
				"(cmod that_5 controls_4 can_6)",
				"(ncsubj moved_8 controls_4 obj)",
				"<c> Some|some|DT|I-NP|O|NP[nb]/N small|small|JJ|I-NP|O|N/N aircraft|aircraft|NN|I-NP|O|N/N use|use|NN|I-NP|O|N/N controls|control|NNS|I-NP|O|N that|that|WDT|B-NP|O|(NP\\NP)/(S[dcl]\\NP) can|can|MD|I-VP|O|(S[dcl]\\NP)/(S[b]\\NP) be|be|VB|I-VP|O|(S[b]\\NP)/(S[pss]\\NP) moved|move|VBN|I-VP|O|S[pss]\\NP from|from|IN|I-PP|O|((S\\NP)\\(S\\NP))/NP one|one|CD|I-NP|O|N/N position|position|NN|I-NP|O|N to|to|TO|I-PP|O|((S\\NP)\\(S\\NP))/NP another|another|DT|I-NP|O|NP ,|,|,|O|O|, such|such|JJ|I-PP|O|(NP\\NP)/(NP\\NP) as|as|IN|I-PP|O|(NP\\NP)/NP a|a|DT|I-NP|O|NP[nb]/N single|single|JJ|I-NP|O|N/N yoke|yoke|NN|I-NP|O|N that|that|WDT|B-NP|O|(NP\\NP)/(S[dcl]\\NP) can|can|MD|I-VP|O|(S[dcl]\\NP)/(S[b]\\NP) be|be|VB|I-VP|O|(S[b]\\NP)/(S[pss]\\NP) swung|swing|VBN|I-VP|O|(S[pss]\\NP)/PP into|into|IN|I-PP|O|PP/NP position|position|NN|I-NP|O|N in|in|IN|I-PP|O|(NP\\NP)/NP front|front|NN|I-NP|O|N of|of|IN|I-PP|O|(NP\\NP)/NP either|either|CC|O|O|NP/NP the|the|DT|I-NP|O|NP[nb]/N left-seat|left-seat|JJ|I-NP|O|N/N pilot|pilot|NN|I-NP|O|N or|or|CC|O|O|conj the|the|DT|I-NP|O|NP[nb]/N right-seat|right-seat|JJ|I-NP|O|N/N pilot|pilot|NN|I-NP|O|N (|(|LRB|O|O|(NP\\NP)/NP i.e.|i.e.|FW|I-NP|O|N/N Beechcraft|beechcraft|FW|I-NP|O|N/N Bonanza|Bonanza|NNP|I-NP|O|N )|)|RRB|O|O|RRB .|.|.|O|O|."
		};


		ArrayList<String> ret = Utils.arrayToCollection(retArray, new ArrayList<String>(retArray.length));

		return ret;

	}

	
	protected static List<String> retrieveCandCOutput()
	{
		String[] retArray = new String[]{
				"(det aircraft_2 an_1)",
				"(conj and_6 copilot_7)",
				"(conj and_6 pilot_5)",
				"(det copilot_7 a_4)",
				"(det pilot_5 a_4)",
				"(dobj with_3 copilot_7)",
				"(dobj with_3 pilot_5)",
				"(ncmod _ aircraft_2 with_3)",
				"(conj and_11 trainee_12)",
				"(conj and_11 instructor_10)",
				"(conj or_9 trainee_12)",
				"(conj or_9 instructor_10)",
				"(conj or_9 aircraft_2)",
				"(dobj On_0 trainee_12)",
				"(dobj On_0 instructor_10)",
				"(dobj On_0 aircraft_2)",
				"(det aircraft_15 the_14)",
				"(det crew_23 the_22)",
				"(dobj changing_24 seats_25)",
				"(xmod _ crew_23 changing_24)",
				"(ncsubj changing_24 crew_23 _)",
				"(dobj without_21 crew_23)",
				"(ncmod _ control_20 without_21)",
				"(dobj of_19 control_20)",
				"(iobj capable_18 of_19)",
				"(xcomp _ made_17 capable_18)",
				"(aux made_17 is_16)",
				"(ncsubj made_17 aircraft_15 obj)",
				"(ncmod _ is_16 On_0)",
				"<c> On|on|IN|I-PP|O|(S/S)/NP an|an|DT|I-NP|O|NP[nb]/N aircraft|aircraft|NN|I-NP|O|N with|with|IN|I-PP|O|(NP\\NP)/NP a|a|DT|I-NP|O|NP[nb]/N pilot|pilot|NN|I-NP|O|N and|and|CC|I-NP|O|conj copilot|copilot|NN|I-NP|O|N ,|,|,|I-NP|O|, or|or|CC|I-NP|O|conj instructor|instructor|NN|I-NP|O|N and|and|CC|I-NP|O|conj trainee|trainee|NN|I-NP|O|N ,|,|,|O|O|, the|the|DT|I-NP|O|NP[nb]/N aircraft|aircraft|NN|I-NP|O|N is|be|VBZ|I-VP|O|(S[dcl]\\NP)/(S[pss]\\NP) made|make|VBN|I-VP|O|(S[pss]\\NP)/(S[adj]\\NP) capable|capable|JJ|I-ADJP|O|(S[adj]\\NP)/PP of|of|IN|I-PP|O|PP/NP control|control|NN|I-NP|O|N without|without|IN|I-PP|O|(NP\\NP)/NP the|the|DT|I-NP|O|NP[nb]/N crew|crew|NN|I-NP|O|N changing|change|VBG|I-NP|O|(S[ng]\\NP)/NP seats|seat|NNS|I-NP|O|N .|.|.|O|O|."};
		
		ArrayList<String> ret = Utils.arrayToCollection(retArray, new ArrayList<String>(retArray.length));
		
		return ret;
	}
	
	public static void main(String[] args)
	{
		try
		{
			CandCOutputToGraph toGraph = new CandCOutputToGraph(retrieveCandCOutput2());
			toGraph.generateGraph();
			DirectedGraph<CCNode, CCEdgeInfo> graph = toGraph.getGraph();
			DirectedGraphToDot<CCNode, CCEdgeInfo> toDot = 
				new DirectedGraphToDot<CCNode, CCEdgeInfo>(graph, new CCGraphStringRepresentation(), System.out);
			toDot.printDot();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
	}

	/**
	 * @param args
	 */
	public static void main2(String[] args)
	{
		try
		{
			if (args.length<1)
				throw new Exception("args");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
			boolean stop = false;
			while (!stop)
			{
				LinkedList<String> ccOutput = new LinkedList<String>();
				String line = reader.readLine();

				while ( (line != null) && (line.trim().length()>0) )
				{
					ccOutput.add(line);
					line = reader.readLine();
				}
				if (ccOutput.size()>0)
				{




					CandCOutputToGraph toGraph = new CandCOutputToGraph(ccOutput);
					toGraph.generateGraph();
					DirectedGraph<CCNode, CCEdgeInfo> graph = toGraph.getGraph();
					DirectedGraphToDot<CCNode, CCEdgeInfo> toDot = 
						new DirectedGraphToDot<CCNode, CCEdgeInfo>(graph, new CCGraphStringRepresentation(), System.out);
					toDot.printDot();
					System.out.println("---------------------------------");
				}
				
				if (null==line)
					stop = true;
			}
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}





	}

}
