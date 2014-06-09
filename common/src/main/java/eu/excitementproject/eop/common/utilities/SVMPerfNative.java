package eu.excitementproject.eop.common.utilities;
/**
 * Calls C-dll versions of SVM-Perf learn and SVM-Perf classify from {@link http://svmlight.joachims.org/svm_perf.html}
 * <p>
 * Uses JNI
 * <p>
 * <b>Prereq:</b> The folder containing the JNI-ready OS correspondent compilations of {@code svm_perf_learn.dll/so} and 
 * {@code svm_perf_classify.dll/so} must be in the Java path. 
 * Should be found under {@code JARS\svm\svm_perf_libraries\}.
 * <br>E.g. add {@code -Djava.library.path="b:\jars\svm\svm_perf_libraries\win64"} to the java params of your main class.
 * 
 * @author Amnon Lotan
 *
 * @since 24/03/2011
 */
public class SVMPerfNative
{
    /**
     * call svm-perf-learn, using files
     * 
     * @param args e.g. "-c 0.01 -v 0 examples.txt model.txt"
     */
	public static native void svmPerfLearn(String args);
	
	/**
	 * call svm-perf-learn, and pass the examples as a string argument
	 * 
	 * @param args e.g. "-c 0.01 -v 0"
	 * @param examples e.g. made by SVMperf#writeExampleString()
	 * @return the model file in a string
	 */
	public static native String svmPerfLearn (String args, String examples);  
	
	/**
	 * call svm-perf-classify, using conventional files
	 * 
	 * @param args e.g. "-v 0 -y 0 examples.txt model.txt predictions.txt"
	 * @return
	 */
	public static native void svmPerfClassify	(String args);

	/**
     * call svm-perf-classify, passing the examples and model, and returning the predictions, as strings 
     * 
     * @param args e.g. "-c 0.01 c:/1.txt c:/2.out"
	 * @param examples the examples in a string
	 * @param model the trained model in a string
     * @return the predictions in a string
     */
	public static native String svmPerfClassify	(String args, String examples, String model);    

	static {
        System.loadLibrary("svm_perf_learn");
		System.loadLibrary("svm_perf_classify");
    }        
}
