/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.compatibilityaction;

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
public abstract class CompatibilityDataProvider implements Describable<CompatibilityDataProvider>, Serializable {

    /**
     * Method to create and insert an object into a database. 
     * 
     * @param <T>
     * @param t
     * @return 
     * @throws CompatibilityDataException 
     */
    public abstract <T> T create(T t) throws CompatibilityDataException;
    
    public abstract <T> T read(Object key, Class<T> clazz) throws CompatibilityDataException;
    
    public <T> T create(T t, TaskListener listener) throws CompatibilityDataException {
        return null;
    }
    
    public abstract static class CompatabilityDataProviderDescriptor extends Descriptor<CompatibilityDataProvider> {
        
        public static ExtensionList<CompatabilityDataProviderDescriptor> all() {
            return Jenkins.getInstance().getExtensionList(CompatabilityDataProviderDescriptor.class);
        }
    
    }
}
