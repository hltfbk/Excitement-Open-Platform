package eu.excitementproject.eop.distsim.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loggers factory
 * 
 * @author Meni Adler
 * @since 21/07/2011
 * 
 */
public class Loggers { 

	private static Map<String, Logger> _loggers = new HashMap<String, Logger>();
	//private static final String logfile = "DistSim.log";
	private static final Level DEFAULT_LEVEL = Level.FINEST;
	private static Handler _handler;

	public static Logger get(String name) {
		Logger logger = _loggers.get(name);
		if (logger == null) {
			logger = Logger.getLogger(name); 
			logger.setLevel(DEFAULT_LEVEL);
			if (_handler != null)
				logger.addHandler(_handler);
			_loggers.put(name, logger);
		}
		return logger;
	} 

	static {
		try {
			_handler = new ConsoleHandler(); // new FileHandler(logfile);
			_handler.setFormatter(new BasicFormatter());
		} catch (Exception e) {
			e.printStackTrace();
			_handler = null;
		}
	}
}
