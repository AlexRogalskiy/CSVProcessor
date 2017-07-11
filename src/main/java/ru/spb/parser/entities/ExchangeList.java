/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.parser.entities;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * Entity class to store information on list of available exchanges
 * 
 * @author alexander.rogalskiy
 * @version 1.0
 * @since   2017-07-10
 *
 */
@Root(name="DataEx")
public class ExchangeList {
    /**
     * List of available exchanges
     */
    @Path("ExList")
    @ElementList(inline=true, required=true, name = "ExList", entry="TDataEx")
    private List<ExchangeEntity> entities;
    /**
     * Max payload lower time frame
     */
    @Attribute(name = "max-payload-lower-bound", required = false)
    private String maxPayloadLowerBound;
    /**
     * Max payload upper time frame
     */
    @Attribute(name = "max-payload-upper-bound", required = false)
    private String maxPayloadUpperBound;
    /**
     * Max payload rate
     */
    @Attribute(name = "max-payload-rate", required = false)
    private Long maxPayloadRate;
        
    /**
     * @return the entities
     */
    public List<ExchangeEntity> getEntities() {
        if(null == this.entities) {
            this.entities = new ArrayList<>();
        }
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(List<ExchangeEntity> entities) {
        this.entities = entities;
    }
    
    /**
     * @return the maxPayloadUpperBound
     */
    public String getMaxPayloadUpperBound() {
        return maxPayloadUpperBound;
    }

    /**
     * @param maxPayloadUpperBound the maxPayloadUpperBound to set
     */
    public void setMaxPayloadUpperBound(String maxPayloadUpperBound) {
        this.maxPayloadUpperBound = maxPayloadUpperBound;
    }

    /**
     * @return the maxPayloadLowerBound
     */
    public String getMaxPayloadLowerBound() {
        return maxPayloadLowerBound;
    }

    /**
     * @param maxPayloadLowerBound the maxPayloadLowerBound to set
     */
    public void setMaxPayloadLowerBound(String maxPayloadLowerBound) {
        this.maxPayloadLowerBound = maxPayloadLowerBound;
    }
    
    /**
     * @return the maxPayloadRate
     */
    public Long getMaxPayloadRate() {
        return maxPayloadRate;
    }

    /**
     * @param maxPayloadRate the maxPayloadRate to set
     */
    public void setMaxPayloadRate(Long maxPayloadRate) {
        this.maxPayloadRate = maxPayloadRate;
    }
}
