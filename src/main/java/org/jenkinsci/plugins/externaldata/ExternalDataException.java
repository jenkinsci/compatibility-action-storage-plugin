/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.externaldata;

import java.io.IOException;

/**
 *
 * @author Mads
 */
public class ExternalDataException extends IOException {
    
    public ExternalDataException(String message) {
        super(message);
    }
    
    public ExternalDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
