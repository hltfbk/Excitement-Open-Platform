package eu.excitementproject.eop.lap.biu.en.coreference.arkref;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.lap.biu.coreference.merge.WordWithCoreferenceTag;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefClient.ArkrefClientException;

public class ArkrefClientDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String text1 = "The video is interspersed with clips from the recent Israeli raid on the Gaza-bound aid ship, the Mavi Marmara, showing activists attacking Israeli soldiers in the clashes that would eventually claim the lives of nine activists and leave dozens, including Israeli commandos, injured. The Israeli government spokesperson's office accidentally circulated a link to the clip on Friday, after which they stated the video was \"not intended for general release. The contents of the video in no way represent the official policy of either the Government Press Office or of the State of Israel\". The clip was created by a Hebrew-language media satire site called Latma.";
		// String text="Indian, Pakistan military to discuss alleged Kashmir ceasefire violation Indian and Pakistani military commanders were to discuss Wednesday Indian charges that Pakistan fired mortar shells across the border into Indian-controlled Kashmir in violation of a 14-month ceasefire. The director-generals of military operations of the nuclear-armed neighbours were slated to talk by telephone about the incident that occurred late Tuesday. \"The director generals of military operations will be talking later today,\" an Indian government official said, declining to be named. \"The Pakistani side has denied firing. Let's see.\" The time of the hotline call between the Indian and Pakistani officials was not immediately known. On Wednesday, Islamabad denied it had violated the ceasefire in the disputed Himalayan state of Kashmir, spark of 2 of 3 wars between Indian and Pakistan, and over which they skirmished in 2002. India and Pakistan began the ceasefire 25/11/2003, after routinely exchanging artillery fire across the volatile Line of Control, the de facto border separating their armies in Kashmir, as part of a tentative peace process. \"No one from Pakistan has fired and there is no ceasefire violation by Pakistan,\" Pakistan military spokesman Major General Shaukat Sultan told AFP in Islamabad. The firing was front-page news in Indian newspapers. \"Pakistan opens fire across Line of Control,\" said The Hindu. Indian police said Tuesday at least one person was injured when about 15 mortars were fired into India from Pakistan over the Line of Control. The mortars came from across the border Tuesday evening and landed in India's Durga Post area in the Poonch sector, an Indian police spokesman said. Indian army officer Major General D. Samanwar told NDTV news channel the incident was tantamount to a ceasefire violation. \"Yes, it certainly is a violation. It's the first time it has happened but we have exercised full restraint,\" he said, adding troops had been put on alert. The 2 sides have been engaged in formal peace talks to normalise relations since January 2004.";
		try {
			/*	String[] arguments = {"-input", "D://MyWorkspace//arkref//demo//lili.txt"};
			ARKref.main(arguments);
			 */	

			System.out.println("Please enter text. For exit type \"exit\".");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String text = reader.readLine();
			while (text != null)
			{
				if (text.equalsIgnoreCase("exit"))
					break;
				System.out.println(text);
				System.out.println("processing...");
				File tempFile = File.createTempFile("arkref_temp", ".txt");
				ArkrefClient arkrefC = new ArkrefClient(text, tempFile.getAbsolutePath());
				arkrefC.process();
				System.out.println("processing done.");

				List<WordWithCoreferenceTag> output = arkrefC.getArkrefOutput();
				int i=1;
				for (WordWithCoreferenceTag wwct : output)
				{
					System.out.print(""+i+": ");
					System.out.print(wwct.getWord());
					if (wwct.getCoreferenceTag()!=null)
					{
						System.out.print("/"+wwct.getCoreferenceTag());
					}
					System.out.println();
					++i;
				}
				printDebugOutput(arkrefC);
				try{tempFile.delete();}
				catch(Exception e){System.out.println("Could not delete."); e.printStackTrace(System.out);}
				System.out.println();
				System.out.println(StringUtil.generateStringOfCharacter('-', 50));
				text = reader.readLine();
			}

		} catch (ArkrefClientException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}
	
	private static void printDebugOutput(ArkrefClient arkrefC)
	{
		System.out.println("======= DEBUG =======");
		List<WordAndStackTags> output = arkrefC.getArkrefStackOutput();
		int i=1;
		for (WordAndStackTags wast : output)
		{
			System.out.print( String.format("%-4d", i)+": " );
			System.out.print( String.format("%-12s", wast.getWord()) );
			if (wast.getTags()!=null)
			{
				System.out.print(": ");
				Stack<String> tags = new Stack<String>();
				tags.addAll(wast.getTags());
				Collections.reverse(tags);
				boolean firstIteration = true;
				for (String tag : tags)
				{
					if (firstIteration) firstIteration=false;
					else System.out.print(" / ");
					System.out.print(tag);
				}
			}
			System.out.println();
			++i;
		}
		System.out.println("=====================");
	}

}
