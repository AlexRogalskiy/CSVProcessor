/**
 * 
 */
package ru.spb.parser.entities;

import com.opencsv.bean.CsvBindByName;
import java.util.Date;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import ru.spb.parser.format.DateFormatter;

/**
 * Entity class to store information on ticket transaction
 * 
 * @author alexander.rogalskiy
 * @version 1.0
 * @since   2017-07-10
 *
 */
@Root(name="T")
public class TicketEntity {
            
        public static class DateConverter implements Converter<Date> { 
            @Override
            public Date read(InputNode node) throws Exception {
                String value = node.getAttribute("timestamp").getValue();
                return DateFormatter.stringToDate(value, TicketEntity.DEFAULT_DATE_FORMAT);
            }

            @Override
            public void write(OutputNode node, Date value) throws Exception {
                node.setAttribute("timestamp", DateFormatter.dateToString(value, TicketEntity.DEFAULT_DATE_FORMAT));
            }
        }
        
        /**
	 * Default date format
	 */
        @Transient
        public static final String DEFAULT_DATE_FORMAT = "HH:mm:ss.SSS";
	/**
	 * Operation timestamp
	 */
        @CsvBindByName(column = "timestamp", required = true)
        //@CsvDate("HH:mm:ss.SSS")
        //@CsvCustomBindByName(column = "timestamp", converter = StringToDateConverter.class)
        @Attribute(name = "timestamp", required = true)
        //@Convert(DateConverter.class)
        private String timestamp;
        /**
	 * Operation price by unit
	 */
        @CsvBindByName(column = "price", required = true)
        @Attribute(name = "price", required = true)
        private Double price;
        /**
	 * Operation volume
	 */
        @CsvBindByName(column = "volume", required = true)
        @Attribute(name = "volume", required = true)
        private Long volume;
        /**
	 * Exchange ID
	 */
        @CsvBindByName(column = "exchangeId", required = true)
        @Transient
        private String exchangeId;
        /**
	 * Max frame limit
	 */
        @Transient
        private Long maxFrame;
    
        /**
        * @return the timestamp
        */
        public String getTimestamp() {
           return timestamp;
         }

        /**
        * @param timestamp the timestamp to set
        */
        public void setTimestamp(String timestamp) {
           this.timestamp = timestamp;
        }

        /**
        * @return the price
        */
        public Double getPrice() {
           return price;
        }

        /**
        * @param price the price to set
        */
        public void setPrice(Double price) {
           this.price = price;
        }

        /**
        * @return the volume
        */
        public Long getVolume() {
           return volume;
        }

        /**
        * @param volume the volume to set
        */
        public void setVolume(Long volume) {
           this.volume = volume;
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
        
        /**
        * @return the maxFrame
        */
        public Long getMaxFrame() {
           return maxFrame;
        }

        /**
        * @param maxFrame the maxFrame to set
        */
        public void setMaxFrame(Long maxFrame) {
           this.maxFrame = maxFrame;
        }
       
        public Date getDate(String format) {
           return DateFormatter.stringToDate(this.getTimestamp(), format);
        }
       
        public Long getBySecondFrame() {
           return this.getDate(TicketEntity.DEFAULT_DATE_FORMAT).getTime();
        }
}
