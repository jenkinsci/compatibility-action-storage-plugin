/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.externaldata;

import hudson.ExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import java.io.Serializable;
import jenkins.model.Jenkins;

/**
 *
 * @author Mads
 */
public abstract class ExternalDataProvider implements Describable<ExternalDataProvider>, Serializable {

    /**
     * Method to create and insert an object into a database. 
     * 
     * @param <T>
     * @param t
     * @return 
     * @throws ExternalDataException 
     */
    public abstract <T> T create(T t) throws ExternalDataException;
    
    public abstract <T> T read(Object key, Class<T> clazz) throws ExternalDataException;
    
    public <T> T create(T t, TaskListener listener) throws ExternalDataException {
        return null;
    }
    
    public abstract static class NoSQLDescriptor extends Descriptor<ExternalDataProvider> {
        
        public static ExtensionList<NoSQLDescriptor> all() {
            return Jenkins.getInstance().getExtensionList(NoSQLDescriptor.class);
        }
    
    }
}
