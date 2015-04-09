/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.nosqldb;

import java.io.IOException;

/**
 *
 * @author Mads
 */
public class NoSQLDataException extends IOException {
    public NoSQLDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
