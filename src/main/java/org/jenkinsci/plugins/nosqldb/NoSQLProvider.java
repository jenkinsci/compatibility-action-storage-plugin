/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.nosqldb;

import hudson.ExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;
import java.io.Serializable;
import jenkins.model.Jenkins;

/**
 *
 * @author Mads
 */
public abstract class NoSQLProvider implements Describable<NoSQLProvider>, Serializable {

    public abstract <T> T create(T t) throws NoSQLDataException;
    public abstract <T> T read(Object key, Class<T> clazz) throws NoSQLDataException;
    
    public abstract static class NoSQLDescriptor extends Descriptor<NoSQLProvider> {
        
        public static ExtensionList<NoSQLDescriptor> all() {
            return Jenkins.getInstance().getExtensionList(NoSQLDescriptor.class);
        }
    
    }
}
