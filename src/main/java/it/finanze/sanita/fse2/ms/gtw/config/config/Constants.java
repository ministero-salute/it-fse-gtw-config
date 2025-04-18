/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.config.config;

/**
 * Constants application.
 */
public final class Constants {

 
	public static final class Collections {

		public static final String CONFIG_DATA = "config_data";

		private Collections() {

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
		public static final String DOCKER = "docker";

		/** 
		 * Constructor.
		 */
		private Profile() {
			//This method is intentionally left blank.
		}
	}

	public static final class Properties {

		public static final String CONTROL_LOG_PERSISTENCE_ENABLED = "control-log-persistence-enabled";
		public static final String KPI_LOG_PERSISTENCE_ENABLED = "kpi-log-persistence-enabled";
		public static final String ISSUER_CF_CLEANING = "issuer-cf-cleaning";
		public static final String SUBJECT_CLEANING = "subject-cleaning";
		public static final String AUDIT_ENABLED = "audit-enabled";
		public static final String CFG_ITEMS_RETENTION_DAY = "cfg-items-retention-day";
		public static final String VALIDATED_DOCUMENT_RETENTION_DAY = "validated-document-retention-day";
		public static final String EXPIRING_DATE_DAY = "expiring-date-day";
		public static final String DELETE_EARLY_STRATEGY = "delete-early-strategy";
		public static final String REMOVE_EDS_ENABLED = "remove-eds-enabled";
		public static final String AUDIT_INI_ENABLED = "audit-ini-enabled";

		/**
		 * Constructor.
		 */
		private Properties() {
			//This method is intentionally left blank.
		}
	}
  
	/**
	 *	Constants.
	 */
	private Constants() {

	}

}
