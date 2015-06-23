/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.compatibilityaction;

import java.io.IOException;

/**
 *
 * @author Mads
 */
public class CompatibilityDataException extends IOException {
    
    public CompatibilityDataException(String message) {
        super(message);
    }
    
    public CompatibilityDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
