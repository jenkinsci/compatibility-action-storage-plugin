/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.externaldata;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import hudson.util.Secret;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Mads
 */
public class MongoDBHolderService implements Serializable {
    
    private Secret password;
    private String user;
    private int port;
    private String host;
    private String database;

    public MongoDBHolderService(String user, Secret password, int port, String host, String database) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.database = database;
        this.port = port;
    }
    
    public MongoDBHolderService() { }
    
    public MongoClient createClient() throws UnknownHostException {
        if(!StringUtils.isBlank(Secret.toString(password))) {
            return new MongoClient(new ServerAddress(host, port), Arrays.asList(MongoCredential.createCredential(user, database, Secret.toString(password).toCharArray())));
        } else {
            ServerAddress addr = new ServerAddress(host, port);
            return new MongoClient(addr);                
        }
    }
    
    public String testConnection(String collection) throws ExternalDataException {
        try {
            StringBuilder builder = new StringBuilder();
            DB db = createClient().getDB(database);
            Set<String> collections = db.getCollectionNames();
            if(collections.isEmpty()) {
                throw new ExternalDataException("No collections found in database");
            }
            
            if(!collections.contains(collection)) {
                throw new ExternalDataException("The specified collection was not found in the database");
            }
            
            DBObject cur = db.getCollection(collection).findOne();           
            builder.append("<br/>").append(cur);
             
            return builder.toString();            
        } catch (UnknownHostException ex) {
            throw new ExternalDataException("Unable to list collections in database. Check your connection settings", ex);
        }
    }

    /**
     * @return the password
     */
    public Secret getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(Secret password) {
        this.password = password;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }
}
