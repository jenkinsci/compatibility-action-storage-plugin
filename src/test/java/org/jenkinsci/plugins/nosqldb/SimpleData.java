/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.nosqldb;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mongojack.Id;
import org.mongojack.ObjectId;


/**
 *
 * @author Mads
 */
public class SimpleData {
    
    private String id;
    private String moreData;
    private String moreTestData;
    
    public SimpleData() { }
    
    public SimpleData(String moreData, String moreTestData) {
        this.moreData = moreData;
        this.moreTestData = moreTestData;
    }

    /**
     * @return the id
     */
    @ObjectId
    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @ObjectId
    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the moreData
     */
    public String getMoreData() {
        return moreData;
    }

    /**
     * @param moreData the moreData to set
     */
    public void setMoreData(String moreData) {
        this.moreData = moreData;
    }

    /**
     * @return the moreTestData
     */
    public String getMoreTestData() {
        return moreTestData;
    }

    /**
     * @param moreTestData the moreTestData to set
     */
    public void setMoreTestData(String moreTestData) {
        this.moreTestData = moreTestData;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof SimpleData)) {
            return false;
        }
        return ((SimpleData)o).getId().equals(getId());
    }

    
    
     
    
    
    
}
