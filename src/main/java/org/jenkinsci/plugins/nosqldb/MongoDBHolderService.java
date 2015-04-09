/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.nosqldb;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import hudson.util.Secret;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 *
 * @author Mads
 */
public class MongoDBHolderService {
    
    private Secret password;
    private String user;
    private int port;
    private String host;
    private String database;
    private static MongoClient client = null;

    
    public MongoDBHolderService(String user, Secret password, int port, String host, String database) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.database = database;
        this.port = port;
    }
    
    public MongoClient getClient() throws UnknownHostException {
        if(client == null) {
            if(password != null) {
                client = new MongoClient(new ServerAddress(host, port), Arrays.asList(MongoCredential.createCredential(user, database, Secret.toString(password).toCharArray())));
            } else {
                ServerAddress addr = new ServerAddress(host, port);
                client = new MongoClient(addr);                
            }
        }
        return client;        
    }
}
