package eu.excitementproject.eop.lap.biu.en.parser.minipar;

import java.util.ArrayList;


/**
 * JNI interface to Minipar.
 * <P>
 * A shared library (.dll .so), create by Asher Stern is activated
 * by the methods of this class.
 * @author Asher Stern
 *
 */
public class MiniparJni
{
	public native boolean init(String dataDir,String features);
	public native ArrayList<String> parse(String sentence);
	

}
