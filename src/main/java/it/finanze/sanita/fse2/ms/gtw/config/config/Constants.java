package it.finanze.sanita.fse2.ms.gtw.config.config;

/**
 * 
 * @author vincenzoingenito
 *
 * Constants application.
 */
public final class Constants {

	/**
	 *	Path scan.
	 */
	public static final class ComponentScan {

		/**
		 * Base path.
		 */
		public static final String BASE = "it.sanita.config";

		/**
		 * Controller path.
		 */
		public static final String CONTROLLER = "it.sanita.config.controller";

		/**
		 * Service path.
		 */
		public static final String SERVICE = "it.sanita.config.service";

		/**
		 * Configuration path.
		 */
		public static final String CONFIG = "it.sanita.config.config";
		
		/**
		 * Configuration mongo path.
		 */
		public static final String CONFIG_MONGO = "it.sanita.config.mongo";
		
		/**
		 * Configuration mongo repository path.
		 */
		public static final String REPOSITORY_MONGO = "it.sanita.config.repository";

		public static final class Collections {

			public static final String CONFIG_DATA = "config_data";

			private Collections() {

			}
		}
		
		private ComponentScan() {
			//This method is intentionally left blank.
		}

	}
 
	public static final class Profile {

		/**
		 * Test profile.
		 */
		public static final String TEST = "test";

		public static final String TEST_PREFIX = "test_";

		/**
		 * Dev profile.
		 */
		public static final String DEV = "dev";

		/** 
		 * Constructor.
		 */
		private Profile() {
			//This method is intentionally left blank.
		}
	}
  
	/**
	 *	Constants.
	 */
	private Constants() {

	}

}
