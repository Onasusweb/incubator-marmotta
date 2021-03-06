/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.marmotta.kiwi.test.sesame.repository;

import org.apache.marmotta.kiwi.config.KiWiConfiguration;
import org.apache.marmotta.kiwi.persistence.h2.H2Dialect;
import org.apache.marmotta.kiwi.sail.KiWiStore;
import org.apache.marmotta.kiwi.test.junit.KiWiDatabaseRunner;
import org.junit.runner.RunWith;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnectionTest;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.SailWrapper;

import java.sql.SQLException;

import static org.junit.Assert.fail;

/**
 * Run the {@link RepositoryConnectionTest}s.
 * @author Jakob Frank <jakob@apache.org>
 *
 */
@RunWith(KiWiDatabaseRunner.class)
public class KiWiRepositoryConnectionTest extends RepositoryConnectionTest {

    private final KiWiConfiguration config;

    public KiWiRepositoryConnectionTest(KiWiConfiguration config) {
        this.config = config;
    }
    
    /* (non-Javadoc)
     * @see org.openrdf.repository.RepositoryConnectionTest#createRepository()
     */
    @Override
    protected Repository createRepository() throws Exception {
        config.setDefaultContext(null);
        Sail store = new SailWrapper(new KiWiStore(config)) {
            @Override
            public void shutDown() throws SailException {
                try {
                    ((KiWiStore)getBaseSail()).getPersistence().dropDatabase();
                } catch (SQLException e) {
                    fail("SQL exception while deleting database");
                }

                super.shutDown();
            }
        };
        return new SailRepository(store);
    }

}
