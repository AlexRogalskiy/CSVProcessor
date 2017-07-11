/**
 * 
 */
package ru.spb.parser.entities;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * Entity class to store information on list of ticket operations by exchange
 * 
 * @author alexander.rogalskiy
 * @version 1.0
 * @since   2017-07-10
 *
 */
@Root(name="TDataEx")
public class ExchangeEntity {
    /**
     * @param maxPayloadLowerBound the maxPayloadLowerBound to set
     */
    public void setMaxPayloadLowerBound(String maxPayloadLowerBound) {
        this.maxPayloadLowerBound = maxPayloadLowerBound;
    }
    	/**
	 * List of operations by exchange
	 */
	@Path("TList")
	@ElementList(inline=true, name="TList", required=true, entry="T")
        private List<TicketEntity> entities;
        /**
	 * Exchange ID
	 */
        @Attribute(name = "name", required = true)
        private String exchangeId;
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
        * @return the list of ticket entities
        */
	public List<TicketEntity> getEntities() {
                if(null == this.entities) {
                    this.entities = new ArrayList<>();
                }
                return entities;
	}

        /**
        * @param entities the ticket entities to set
        */
	public void setEntities(List<TicketEntity> entities) {
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
        
        /**
        * @return the exchangeId
        */
        public String getExchangeId() {
           return exchangeId;
        }

        /**
        * @param exchangeId the exchangeId to set
        */
        public void setExchangeId(String exchangeId) {
           this.exchangeId = exchangeId;
        }
}
