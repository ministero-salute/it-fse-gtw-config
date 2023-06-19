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
package it.finanze.sanita.fse2.ms.gtw.config.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.config.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;


/**
 *  Abstract mongo repository. 
 */
@Component
@Slf4j
public abstract class AbstractMongoRepository<T, K> {

	/**
	 * Template to access at MongoDB.
	 */
    @Autowired
    private MongoTemplate mongoTemplate;
	
	/**
	 * Insert All.
	 * 
	 * @param entity	etys
	 * @return			
	 */
	protected void insertAll(final List<T> entity) {
		try {
			mongoTemplate.insertAll(entity);
		} catch(Exception ex) {
			log.error("Error inserting all etys " + getClass() , ex);
			throw new BusinessException("Error inserting all etys " + getClass() , ex);
		}
	}
	 
}
