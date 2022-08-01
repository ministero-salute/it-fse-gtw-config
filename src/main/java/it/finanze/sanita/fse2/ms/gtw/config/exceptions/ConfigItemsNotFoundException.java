package it.finanze.sanita.fse2.ms.gtw.config.exceptions;

/**
 * Exception thrown when a configuration item is not found in database.
 * 
 * @author Simone Lungarella
 */
public class ConfigItemsNotFoundException extends RuntimeException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4430720371354323215L;

	/**
	 * Message constructor.
	 * 
	 * @param msg Message to be shown.
	 */
	public ConfigItemsNotFoundException(final String msg) {
		super(msg);
	}

	/**
	 * Complete constructor.
	 * 
	 * @param msg Message to be shown.
	 * @param e   Exception to be shown.
	 */
	public ConfigItemsNotFoundException(final String msg, final Exception e) {
		super(msg, e);
	}

	/**
	 * Exception constructor.
	 * 
	 * @param e Exception to be shown.
	 */
	public ConfigItemsNotFoundException(final Exception e) {
		super(e);
	}

}
