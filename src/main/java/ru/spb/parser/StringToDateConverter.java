/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.parser;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.time.LocalTime;
import org.apache.commons.lang3.StringUtils;
import ru.spb.parser.format.DateFormatter;

/**
 * Custom converter class to process string to date conversion
 * 
 * @author alexander.rogalskiy
 * @version 1.0
 * @since   2017-07-10
 *
 */
public class StringToDateConverter<T> extends AbstractBeanField<T> {
    
    public StringToDateConverter() { }

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, CsvConstraintViolationException {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return DateFormatter.stringToDate(value, "HH:mm:ss.SSS");
    }
    
    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        String result = "";
        if(null != value) {
            return value.toString();
        }
        return result;
    }
    
}
