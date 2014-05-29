package eu.excitementproject.eop.tracer.client;

import com.google.gwt.user.client.rpc.RemoteService;


import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.excitementproject.eop.tracer.shared.EntailmentServiceException;

/**
 * The client side stub for the RPC service.
 */ 
@RemoteServiceRelativePath("entailment")
public interface EntailmentService extends RemoteService {
	//int init();
	String resolve(String  text,String hypothesis) throws EntailmentServiceException;
	//Set<Annotation> getAnnotations(int id);
	//Set<Alignment> getAlignments(int id);
	//String getDecision(int id);
	//int close();
}
 