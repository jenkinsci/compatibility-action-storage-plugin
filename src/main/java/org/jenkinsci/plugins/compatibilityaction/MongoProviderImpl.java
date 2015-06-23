/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.compatibilityaction;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.Secret;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.mongojack.JacksonDBCollection;

/**
 *
 * @author Mads
 */
public class MongoProviderImpl extends CompatibilityDataProvider {
    
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
    
    public MongoProviderImpl() { }        
   
    public Descriptor<CompatibilityDataProvider> getDescriptor() {
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
    
    public String getCollection() {
        return this.collection;
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
    public <T> T create(T t) throws CompatibilityDataException {
        MongoClient client = null;
        try {
            client = getService().createClient();
            JacksonDBCollection coll = JacksonDBCollection.wrap(client.getDB(database).getCollection(collection), t.getClass());            
            return (T)coll.insert(t).getSavedObject();

        } catch (Exception ex) {
            throw new CompatibilityDataException(String.format("Failed to insert object %s", t), ex);
        } finally {
            if(client != null) {
                client.close();
            }
        }       
    }

    /**
     * Creates an instance of of a Java object in the database. 
     
     * The class must have attributes a method/field annotated with @Id for Jackson to
     * use as an identity when data is inserted into our mongodb.     
     * @param <T>
     * @param t
     * @param listener
     * @return
     * @throws CompatibilityDataException 
     */
    @Override
    public <T> T create(T t, TaskListener listener) throws CompatibilityDataException {
        MongoClient client = null;
        try {
            DB db = getService().createClient().getDB(database);
            
            //If the collection name does not exists. Create it.
            if(!db.getCollectionNames().contains(collection)) {
                db.createCollection(collection, null);
            }
            
            JacksonDBCollection coll = JacksonDBCollection.wrap(db.getCollection(collection), t.getClass());
            listener.getLogger().println(String.format("%s Collection initialized.", CompatibilityDataPlugin.LOG_PREFIX));
            T myobj = (T)coll.insert(t).getSavedObject();            
            return myobj;
        } catch (Exception ex) {
            throw new CompatibilityDataException(String.format("Failed to insert object %s", t), ex);
        } finally {
            if(client != null) {
                client.close();
            }
        } 
    }

    
    
    
    @Override
    public <T> T read(Object key, Class<T> clazz) throws CompatibilityDataException {
        MongoClient client = null;
        try {
            client = getService().createClient();
            JacksonDBCollection coll = JacksonDBCollection.wrap(client.getDB(database).getCollection(collection), clazz);
            return (T)coll.findOneById(key);
        } catch (Exception ex) {
            throw new CompatibilityDataException(String.format("Failed to fetch object with key %s", key ), ex);
        } finally {
            if(client != null) {
                client.close();
            }
        } 
    }
    
    public <T> List<T> readMany(BasicDBObject query, Class<T> clazz) throws CompatibilityDataException {
        List<T> list = new ArrayList<T>();
        org.mongojack.DBCursor cursor = null;
        MongoClient client = null;
        try {
            client = getService().createClient();
            JacksonDBCollection coll = JacksonDBCollection.wrap(client.getDB(database).getCollection(collection), clazz);
            cursor = coll.find(query);
            while(cursor.hasNext()) {
                 list.add((T)cursor.next());
            }            
        } catch (Exception ex) {
            throw new CompatibilityDataException(String.format("Failed to fetch object with query %s", query ), ex);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            if (client != null)  {
                client.close();
            }
        }
        return list;
    }
    
    public int count(DBObject query) throws CompatibilityDataException {  
        MongoClient client = null;
        try {
            client =  getService().createClient();
            return client.getDB(database).getCollection(collection).find(query).sort(new BasicDBObject("registrationDate", 1)).size();
        } catch (Exception ex) {
            throw new CompatibilityDataException(String.format("(Unknown Host) Failed to fetch object with query %s", query ), ex);
        } finally {
            if(client != null) client.close();            
        }
    }
    
    public List<DBObject> listAndSort(DBObject query, BasicDBObject sorter) throws CompatibilityDataException {
        List<DBObject> objects = new ArrayList<DBObject>();
        
        DBCursor cursor = null;
        MongoClient client = null;
        try {
            client = getService().createClient();
            cursor = client.getDB(database).getCollection(collection).find(query).sort(sorter);
            while(cursor.hasNext()) {
                objects.add(cursor.next());
            }
        } catch (Exception ex) {
            throw new CompatibilityDataException(String.format("(Unknown Host) Failed to fetch object with query %s", query ), ex);
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            if(client != null) {
                client.close();
            }
        }
        return objects;
    }
        
    @Extension
    public static final class MongoProviderDescriptor extends CompatabilityDataProviderDescriptor {
       
        public MongoProviderDescriptor() {
            load();
        }
        
        @Override
        public String getDisplayName() {
            return "MongoDB";
        }
        
        public FormValidation doTestConnection(@QueryParameter("host") String host, @QueryParameter("port") int port, @QueryParameter("password") Secret password, @QueryParameter("username") String username, @QueryParameter("database") String database, @QueryParameter("collection") String collection) {
            String res = "";
            try {
                res = new MongoDBHolderService(username, password, port, host, database).testConnection(collection);
            } catch (CompatibilityDataException ex) {
                return FormValidation.error(ex.getMessage());
            } catch (Exception unknown) {
                return FormValidation.error(unknown.getMessage());
            }
            
            String msg = !StringUtils.isBlank(res) ? res : "";
            
            return FormValidation.okWithMarkup("Connection established. Listing a random element." + msg);
        }
    }

    @Override
    public String toString() {
        return "Mongo@" + this.getHost() + ":"+this.getPort()+"@"+this.getCollection();
    }
}
