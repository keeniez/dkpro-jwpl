/*******************************************************************************
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.tudarmstadt.ukp.wikipedia.api.hibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants;

public class WikiHibernateUtil implements WikiConstants {

    private static Map<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();

    public static SessionFactory getSessionFactory(DatabaseConfiguration config) {

        if (config.getLanguage() == null) {
            throw new ExceptionInInitializerError("Database configuration error. 'Language' is empty.");
        }
        else if (config.getHost() == null) {
            throw new ExceptionInInitializerError("Database configuration error. 'Host' is empty.");
        }
        else if (config.getDatabase() == null) {
            throw new ExceptionInInitializerError("Database configuration error. 'Database' is empty.");
        }

        String uniqueSessionKey = config.getLanguage().toString() + config.getHost() + config.getDatabase();
        if (!sessionFactoryMap.containsKey(uniqueSessionKey)) {
        	Configuration configuration = getConfiguration(config);
            ServiceRegistryBuilder ssrb = new ServiceRegistryBuilder().applySettings(configuration.getProperties());            
            SessionFactory sessionFactory = configuration.buildSessionFactory(ssrb.buildServiceRegistry());
            sessionFactoryMap.put(uniqueSessionKey, sessionFactory);
        }
        return sessionFactoryMap.get(uniqueSessionKey);
    }


    private static Properties getProperties(DatabaseConfiguration config) {
        String host     = config.getHost();
        String db       = config.getDatabase();
        String user     = config.getUser();
        String password = config.getPassword();

        Properties p = new Properties();

        // Database connection settings
        p.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        p.setProperty("hibernate.connection.url", "jdbc:mysql://" + host + "/" + db);

        p.setProperty("hibernate.connection.characterEncoding", "UTF-8");

        p.setProperty("hibernate.connection.useUnicode","true");
        p.setProperty("hibernate.connection.username", user);
        p.setProperty("hibernate.connection.password", password);

        // SQL dialect
        p.setProperty("hibernate.dialect","org.hibernate.dialect.MySQLDialect");

        // Enable Hibernate's automatic session context management
        p.setProperty("hibernate.current_session_context_class","thread");

        // Echo all executed SQL to stdout
        p.setProperty("hibernate.show_sql","false");

        // Update schema
        p.setProperty("hibernate.hbm2ddl.auto","update");
        
        return p;
    }

    private static Configuration getConfiguration(DatabaseConfiguration config) {
        Configuration cfg = new Configuration()
        .addClass(Category.class)
        .addClass(MetaData.class)
        .addClass(Page.class)
        .addClass(PageMapLine.class)
//        .addClass(RelatednessCacheLine.class)
        .addProperties(getProperties(config));
        return cfg;
    }

}
