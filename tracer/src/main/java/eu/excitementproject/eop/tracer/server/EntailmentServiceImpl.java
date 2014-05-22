package eu.excitementproject.eop.tracer.server;

//import org.apache.uima.jcas.JCas;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import eu.excitementproject.eop.tracer.client.EntailmentService;
import eu.excitementproject.eop.tracer.shared.EntailmentServiceException;
import eu.excitementproject.eop.common.utilities.uima.UimaUtils;
import eu.excitementproject.eop.common.utilities.uima.UimaUtilsException;


public class EntailmentServiceImpl extends RemoteServiceServlet implements EntailmentService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntailmentServiceImpl()  {
	}

	@Override
	public String resolve(String  text,String hypothesis) throws EntailmentServiceException {
		try {
			UimaUtils.newJcas();
			return "Hello World";
		} catch (UimaUtilsException e) {
			throw new EntailmentServiceException(e);
		} 
	}

}
