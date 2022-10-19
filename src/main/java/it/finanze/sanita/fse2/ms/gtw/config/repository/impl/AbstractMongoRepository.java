/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
