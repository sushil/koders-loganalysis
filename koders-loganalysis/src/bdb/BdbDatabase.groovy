/*
 * koders-loganalysis: Tools/Scripts to analyze Koders usage log. 
 * Copyright (C) by contributors. See CONTRIBUTORS.txt for full list.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

/**
 * @author skb
 */

package bdb;

import java.io.File;
import java.io.FileNotFoundException;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * SampleDatabase defines the storage containers, indices and foreign keys
 * for the sample database.
 *
 */
public class BdbDatabase {

    private static final String CLASS_CATALOG = "java_class_catalog";
    private static final String SAMPLE_ACTIVITIES_STORE = "sample_activities_store";

    private Environment env;
    private Database sampleActivitiesDb;
    private StoredClassCatalog javaCatalog;

    /**
     * Open all storage containers, indices, and catalogs.
     */
    public BdbDatabase(String homeDirectory)
        throws DatabaseException, FileNotFoundException {

        // Open the Berkeley DB environment in transactional mode.
        //
        System.out.println("Opening environment in: " + homeDirectory);
        EnvironmentConfig envConfig = new EnvironmentConfig();
        
        envConfig.setTransactional(false);
        envConfig.setAllowCreate(true);
        env = new Environment(new File(homeDirectory), envConfig);

        // Set the Berkeley DB config for opening all stores.
        //
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(false);
        dbConfig.setAllowCreate(true);

        // Create the Serial class catalog.  This holds the serialized class
        // format for all database records of serial format.
        //
        Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig);
        javaCatalog = new StoredClassCatalog(catalogDb);

        // Open the Berkeley DB database for the part, supplier and shipment
        // stores.  The stores are opened with no duplicate keys allowed.
        //
        sampleActivitiesDb = env.openDatabase(null, SAMPLE_ACTIVITIES_STORE, dbConfig);
    
    }

    /**
     * Return the storage environment for the database.
     */
    public final Environment getEnvironment() {

        return env;
    }

    /**
     * Return the class catalog.
     */
    public final StoredClassCatalog getClassCatalog() {

        return javaCatalog;
    }

    /**
     * Return the part storage container.
     */
    public final Database getSampleActivitiesDatabase() {

        return sampleActivitiesDb;
    }

    
    /**
     * Close all databases and the environment.
     */
    public void close()
        throws DatabaseException {
        sampleActivitiesDb.close();
        // And don't forget to close the catalog and the environment.
        javaCatalog.close();
        env.close();
    }
}
