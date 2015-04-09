/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.nosqldb;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.util.Secret;
import java.net.UnknownHostException;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.mongojack.JacksonDBCollection;

/**
 *
 * @author Mads
 */
public class MongoProviderImpl extends NoSQLProvider {
    
    private String database,collection,username;
    private String host = "localhost";
    private int port = 27017;
    private Secret password;
    private MongoDBHolderService service;
    
    @DataBoundConstructor
    public MongoProviderImpl(String host, int port, String database, String collection, String username, Secret password) {
        this.port = port;
        this.host = host;
        this.database = database;
        this.collection = collection;
        this.username = username;
        this.password = password;
        this.service = new MongoDBHolderService(username, password, port, host, database);
    }
    
    public MongoProviderImpl(MongoDBHolderService service) {
        this.service = service;
    }
   
    public Descriptor<NoSQLProvider> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(MongoProviderImpl.class);
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

    /**
     * @param collection the collection to set
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
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
     * @return the service
     */
    public MongoDBHolderService getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(MongoDBHolderService service) {
        this.service = service;
    }

    @Override
    public <T> T create(T t) throws NoSQLDataException {
        try {
            JacksonDBCollection coll = JacksonDBCollection.wrap(getService().getClient().getDB(database).getCollection(collection), t.getClass());            
            return (T)coll.insert(t).getSavedObject();
        } catch (UnknownHostException ex) {
            throw new NoSQLDataException(String.format("Failed to insert object %s", t), ex);
        }        
    }

    @Override
    public <T> T read(Object key, Class<T> clazz) throws NoSQLDataException {
        try {
            JacksonDBCollection coll = JacksonDBCollection.wrap(getService().getClient().getDB(database).getCollection(collection), clazz);
            return (T)coll.findOneById(key);
        } catch (UnknownHostException ex) {
            throw new NoSQLDataException(String.format("Failed to fetch object with key %s", key ), ex);
        }
    }
    
    @Extension
    public static final class MongoProviderDescriptor extends NoSQLDescriptor {
        
        public MongoProviderDescriptor() {
            load();
        }
        
        @Override
        public String getDisplayName() {
            return "MongoDB";
        }
    }

    @Override
    public String toString() {
        return this.getHost();
    }
}
