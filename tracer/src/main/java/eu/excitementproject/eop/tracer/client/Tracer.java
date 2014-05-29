package eu.excitementproject.eop.tracer.client;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tracer implements EntryPoint  {

	/**
	 * Create a remote service proxy to talk to the server-side search service.
	 */
	private final EntailmentServiceAsync entailmentService = GWT.create(EntailmentService.class);
	
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final Label label = new Label();
	/**
	 * This is the entry point method.
	 */ 
	public void onModuleLoad() {

		//JCas jcas;
		
		mainPanel.add(label);
		
		try{					
			entailmentService.resolve("","",
					new AsyncCallback<String>() {

						public void onFailure(Throwable e) {
							label.setText(e.toString());							
						}

						public void onSuccess(String jcas) {
							label.setText(jcas.toString());							
						}
							
					}
				);
			
			RootPanel.get("TracingApplication").add(mainPanel);
			
		} catch (Exception e) {
			label.setText(e.toString());
		}
	}
}
