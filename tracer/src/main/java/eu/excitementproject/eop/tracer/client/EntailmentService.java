package eu.excitementproject.eop.tracer.client;



//import org.apache.uima.jcas.JCas;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.excitementproject.eop.tracer.shared.EntailmentServiceException;

/**
 * The client side stub for the RPC service.
 */ 
@RemoteServiceRelativePath("entailment")
public interface EntailmentService extends RemoteService {
	String resolve(String  text,String hypothesis) throws EntailmentServiceException;
}
 