package eu.excitementproject.eop.core.component.scoring;



public class ScorerUtility {

	/**
	 * check whether the task is IE
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	static double isTaskIE(String task) {
		if (task.equalsIgnoreCase("IE")) {
			return 1;
		}
		return 0;
	}

	/**
	 * check whether the task is IR
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	static double isTaskIR(String task) {
		if (task.equalsIgnoreCase("IR")) {
			return 1;
		}
		return 0;
	}

	/**
	 * check whether the task is QA
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	static double isTaskQA(String task) {
		if (task.equalsIgnoreCase("QA")) {
			return 1;
		}
		return 0;
	}

	/**
	 * check whether the task is SUM
	 * 
	 * @param task
	 * @return 1: yes; 0: no.
	 */
	static double isTaskSUM(String task) {
		if (task.equalsIgnoreCase("SUM")) {
			return 1;
		}
		return 0;
	}
	
	
}
