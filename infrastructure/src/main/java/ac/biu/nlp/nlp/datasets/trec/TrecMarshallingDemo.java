/**
 * 
 */
package ac.biu.nlp.nlp.datasets.trec;

import java.io.File;
import java.util.List;

import ac.biu.nlp.nlp.general.configuration.JaxbLoader;

/**
 * @author Amnon Lotan
 * @since 05/07/2011
 * 
 */
public class TrecMarshallingDemo {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		System.setProperty("java.endorsed.dirs", "b:/jars/jwsdp-2.2.1/lib");
		
		// test TREC
		
		String basePackage = DataFile.BASE_TREC_GENERATED_PACKAGE;
		File trecFolder = new File("//nlp-srv/data2/CORPORA2/TREC");
		for (String corpus: trecFolder.list())
		{
			File corpusDir = new File(trecFolder, corpus);
			if (corpusDir.isDirectory())
			{
				String pack = basePackage + corpus.toLowerCase();
				JaxbLoader<DataFile> loader = new JaxbLoader<DataFile>(pack);
				for (String fileOrDirStr : corpusDir.list())
				{
					File fileOrDir = new File(corpusDir, fileOrDirStr);
					if (fileOrDir.isDirectory())
						for( String file : fileOrDir.list())
							try
							{
								List<Doc> docs = loader.load(new File(fileOrDir, file)).getDocs();
								for(Doc doc : docs){
									System.out.println(doc.getDOCNO());
								}

							} catch (Exception e)
							{
								System.out.println(fileOrDir + "\\" + file + "\n" + e.toString());
							}
					else	// fileOrDir is a file
						try 
						{
							List<Doc> docs = loader.load(fileOrDir).getDocs();
							for(Doc doc : docs){
								System.out.println(doc.getDOCNO());
							}
						} catch (Exception e)
						{
							System.out.println("Bad file: "+ fileOrDir + "\n" + e.toString());
						}
					
						
				}
			}
		}
	}

}
