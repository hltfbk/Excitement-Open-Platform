package eu.excitementproject.eop.tracer.client;

//import org.apache.uima.jcas.JCas;

import com.google.gwt.user.client.rpc.AsyncCallback;



import eu.excitementproject.eop.tracer.shared.EntailmentServiceException;

/**
 * The async counterpart of <code>SearchService</code>.
 */  
public interface EntailmentServiceAsync {
	void resolve(String text, String hypotheis, AsyncCallback<String> callback) throws EntailmentServiceException;
}
