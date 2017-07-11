/**
 * 
 */
package ru.spb.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.simpleframework.xml.convert.AnnotationStrategy;

import ru.spb.parser.entities.ExchangeEntity;
import ru.spb.parser.entities.TicketEntity;
import ru.spb.parser.entities.ExchangeList;
import ru.spb.parser.format.DateFormatter;

/**
 * Main class to handle basic CSV converter operations
 * 
 * @author alexander.rogalskiy
 * @version 1.0
 * @since   2017-07-10
 *
 */
public class CSVprocessor {
	private final Logger LOGGER = LogManager.getLogger(CSVprocessor.class);
	private static final char DEFAULT_FIELD_DELIMETER = ',';
	private static final char DEFAULT_QUOTE_ESCAPE_CHARACTER = '\'';
	private static final char DEFAULT_ESCAPE_CHARACTER = '\\';
	
	private static final String DEFAULT_OUTPUT_FILE_EXTENSION = ".xml";
	private static final String DEFAULT_INPUT_FILE_EXTENSION = ".csv";
	
	private static final String DEFAULT_TICKET_FILE_MARKER = "TicketEX";
	
	private static final String DEFAULT_FILE_ENCODING = "Cp1251";
	
	private CmdLineProcessor cmdProcessor = null;
	
	public static void main(String[] args) {
		new CSVprocessor().init(args);
	}
	
	private void init(String[] args) {
		cmdProcessor = new CmdLineProcessor(args);
		
		if(null != cmdProcessor.getInputSource()) {
			createTicketEntry();
		}
		
		if(null != cmdProcessor.getInputSourceDir()) {
			processDirectory();
		}
	}

	public List<String[]> readAll(File fileName) {
		CSVReader reader = this.getCSVReader(fileName);
                List<String[]> entryList = new ArrayList<>();
		try {
			entryList = reader.readAll();
			closeReader(reader);
		} catch (IOException e) {
			LOGGER.error("Error occured while reading from file stream < " + fileName.getAbsolutePath() + " > :" + e.getMessage());
		}
		return entryList;
	}
	
	private char getCSVFieldDelimiter() {
		return (null == cmdProcessor.getDelimiter() ? DEFAULT_FIELD_DELIMETER : cmdProcessor.getDelimiter());
	}
	
	public CSVReader getCSVReader(String fileName) {
		return this.getCSVReader(fileName, getCSVFieldDelimiter(), DEFAULT_QUOTE_ESCAPE_CHARACTER, DEFAULT_ESCAPE_CHARACTER);
	}
	
	public CSVReader getCSVReader(File fileName) {
		return this.getCSVReader(fileName, getCSVFieldDelimiter(), DEFAULT_QUOTE_ESCAPE_CHARACTER, DEFAULT_ESCAPE_CHARACTER);
	}
	
	public CSVReader getCSVReader(String fileName, char separator, char quoteEscapeChar, char escapeChar) {
		return this.getCSVReader(new File(fileName), separator, quoteEscapeChar, quoteEscapeChar);
	}
	
	public CSVReader getCSVReader(File fileName, char separator, char quoteEscapeChar, char escapeChar) {
		CSVReader reader = null;
		try {
			Reader fileReader = new InputStreamReader(new FileInputStream(fileName), DEFAULT_FILE_ENCODING);
			reader = new CSVReader(fileReader, separator, quoteEscapeChar, escapeChar);
			//reader = new CSVReader(new FileReader(fileName), CSVReader.DEFAULT_SEPARATOR, CSVReader.DEFAULT_QUOTE_CHARACTER, 0);
		} catch (FileNotFoundException e) {
			LOGGER.error("Error occured while initializing CSV reader for file < " + fileName + " > :" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Error occured while initializing uncorrect character encoding for file < " + fileName + " > :" + e.getMessage());
		}
		return reader;
	}
	
	private <T> List<T> getEntityListByPosition(CSVReader reader, String[] columns, Class<T> type) {
		List<T> entityList = new ArrayList<>();
		if(null != reader) {
			ColumnPositionMappingStrategy<T> mapping = new ColumnPositionMappingStrategy<>();
			mapping.setType(type);
			mapping.setColumnMapping(columns);
			
		    CsvToBean<T> csvToBean = new CsvToBean<>();
		    entityList = csvToBean.parse(mapping, reader);
	    }
	    return entityList;
	}
	
	private <T> List<T> getEntityListByName(CSVReader reader, Map<String, String> columnMap, Class<T> type) {
		List<T> entityList = new ArrayList<>();
		if(null != reader) {
			HeaderColumnNameTranslateMappingStrategy<T> mapping = new HeaderColumnNameTranslateMappingStrategy<>();
			mapping.setType(type);
			mapping.setColumnMapping(columnMap);
			
		    CsvToBean<T> csvToBean = new CsvToBean<>();
		    entityList = csvToBean.parse(mapping, reader);
	    }
	    return entityList;
	}
	
	private List<TicketEntity> getTicketEntityList(CSVReader reader) {
		//String[] columns = new String[] {"timestamp", "price", "volume", "exchangeId"};
                Map<String, String> columnMap = new HashMap<>();
                columnMap.put("Time", "timestamp");
                columnMap.put("PRICE", "price");
                columnMap.put("SIZE", "volume");
                columnMap.put("EXCHANGE", "exchangeId");
		return this.getEntityListByName(reader, columnMap, TicketEntity.class);
	}
        
        public static <T> Collector<T, ?, Map<Integer, T>> toMap() {
            return Collector.of(HashMap::new, (map, t) -> map.put(map.size(), t), 
                    (m1, m2) -> {
                        int s = m1.size();
                        m2.forEach((k, v) -> m1.put(k+s, v));
                        return m1;
                    });
        }
	
	private void createTicketEntry() {
		this.createTicketEntry(getCSVReader(cmdProcessor.getInputSource()));
	}
        
        private Function<TicketEntity, Integer> getTicketGroupFunction(List<TicketEntity> ticketList) {
            Function<TicketEntity, Integer> indexGroupingFunc = (entity) -> {
                int start = ticketList.indexOf(entity), end = start;
                while (end < ticketList.size() && Math.abs(ticketList.get(end).getBySecondFrame() - entity.getBySecondFrame()) < 1000) {
                    end++;
                }
                entity.setMaxFrame(ticketList.get(end-1).getBySecondFrame());
                return (end - start);
            }; 
            return indexGroupingFunc;
        }
	
	private void createTicketEntry(CSVReader reader) {
                ExchangeList exchangeList = new ExchangeList();
                List<TicketEntity> ticketList = this.getTicketEntityList(reader);
                if(null != ticketList) {
                    //group by timeframe
                    Map<Integer, List<TicketEntity>> exchangeMapping = ticketList.stream().collect(Collectors.groupingBy(getTicketGroupFunction(ticketList)));
                    Map.Entry<Integer, List<TicketEntity>> exchangeKey = Collections.max(exchangeMapping.entrySet(), Map.Entry.comparingByKey());
                    exchangeList.setMaxPayloadUpperBound(DateFormatter.dateToString(new Date(exchangeKey.getValue().get(0).getMaxFrame()), TicketEntity.DEFAULT_DATE_FORMAT));
                    exchangeList.setMaxPayloadLowerBound(exchangeKey.getValue().get(0).getTimestamp());
                    exchangeList.setMaxPayloadRate(exchangeKey.getKey().longValue());
                    
                    //group by exchange and timeframe
                    Map<String, Map.Entry<Integer, List<TicketEntity>>> exchangeMaxMap = new HashMap<>();
                    Map<String, List<TicketEntity>> ticketExchangeMapping = ticketList.stream().collect(Collectors.groupingBy(TicketEntity::getExchangeId));
                    ticketExchangeMapping.entrySet().forEach((entry) -> {
                        Map<Integer, List<TicketEntity>> exchangeMapping_ = entry.getValue().stream().collect(Collectors.groupingBy(getTicketGroupFunction(entry.getValue())));
                        exchangeMaxMap.put(entry.getKey(), Collections.max(exchangeMapping_.entrySet(), Map.Entry.comparingByKey()));
                    });
                    
                    Map<String, List<TicketEntity>> resultMap = ticketList.stream().collect(Collectors.groupingBy(TicketEntity::getExchangeId));
                    resultMap.entrySet().stream().map((entry) -> {
                        ExchangeEntity exchangeEntity = new ExchangeEntity();
                        exchangeEntity.setEntities(entry.getValue());
                        exchangeEntity.setExchangeId(entry.getKey());
                        
                        exchangeEntity.setMaxPayloadUpperBound(DateFormatter.dateToString(new Date(exchangeMaxMap.get(entry.getKey()).getValue().get(0).getMaxFrame()), TicketEntity.DEFAULT_DATE_FORMAT));
                        exchangeEntity.setMaxPayloadLowerBound(exchangeMaxMap.get(entry.getKey()).getValue().get(0).getTimestamp());
                        exchangeEntity.setMaxPayloadRate(exchangeMaxMap.get(entry.getKey()).getKey().longValue());
                        return exchangeEntity;
                    }).forEachOrdered((exchangeEntity) -> {
                        exchangeList.getEntities().add(exchangeEntity);
                    });
                }
		this.writeExchangeList(cmdProcessor.getOutputSourceDir(), exchangeList);
	}
	
	private void processDirectory() {
		List<String> fileList = this.readDirectory();
                fileList.stream().map((fileName) -> getCSVReader(fileName)).map((reader) -> {
                    createTicketEntry(reader);
                return reader;
            }).forEachOrdered((reader) -> {
                closeReader(reader);
            });
	}
	
	private List<String> readDirectory() {
		List<String> fileList = new ArrayList<>();
		if(null != cmdProcessor.getInputSourceDir()) {
			this.readDirectoryFiles(cmdProcessor.getInputSourceDir(), fileList);
		}
		return fileList;
	}
	
	private void readDirectoryFiles(final File dirName, final List<String> fileList) {
		for (final File fileEntry : dirName.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	this.readDirectoryFiles(fileEntry, fileList);
	        } else {
	            if(fileEntry.isFile() && fileEntry.getName().contains(DEFAULT_INPUT_FILE_EXTENSION)) {
	            	fileList.add(fileEntry.getAbsolutePath() + "\\" + fileEntry.getName());
				}
	        }
	    }
	}
	
	private String createFileName(File dirName, String fileMarker) throws IOException {
		if(null == dirName) {
			throw new IOException("ERROR: not valid directory name < " + dirName + " >");
		}
		if(!dirName.exists()) {
			if (dirName.mkdirs()) {
				LOGGER.info("Directory < " + dirName.getAbsolutePath() + " > has been successfully created");
            } else {
            	LOGGER.info("ERROR: failed to create directory < " + dirName.getAbsolutePath() + " >");
            	throw new IOException("ERROR: failed to create directory < " + dirName.getAbsolutePath() + " >");
            }
		}
		if(!dirName.isDirectory()) {
			throw new IOException("ERROR: not valid directory path < " + dirName.getAbsolutePath() + " >");
		}
		String fileDate = DateFormatter.dateToString(new Date(), "yyyyMMddHHmmssSSS");
		String name = dirName.getAbsolutePath() + "\\" + fileMarker + fileDate + DEFAULT_OUTPUT_FILE_EXTENSION;
		return name;
	}
	
	private void writeExchangeList(File dirName, ExchangeList entity) {
		try {
			String name = createFileName(dirName, DEFAULT_TICKET_FILE_MARKER);
			this.write(entity, new File(name));
		} catch (IOException e) {
			LOGGER.error("ERROR: cannot write input stream to output source < " + dirName + " > :" + e.getMessage());
		}
	}
	
	public void write(Object source, File fileName) {
		this.write(source, fileName, StandardCharsets.UTF_8);
	}
	
	private void write(Object source, File fileName, Charset charset) {
		String prolog = "<?xml version=\"1.0\" encoding=\"" + charset.displayName() + "\"?>";
		Serializer serializer = new Persister(new AnnotationStrategy(), new Format(2, prolog));

		try {
                    try (Writer outputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), DEFAULT_FILE_ENCODING))) {
                        serializer.write(source, outputStream);
                    }
		} catch (FileNotFoundException e) {
			LOGGER.error("File not found < " + fileName.getAbsolutePath() + " > :" + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("Error occured while writing Entity to file < " + fileName.getAbsolutePath() + " > :" + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Error occured while operating with output stream < " + fileName.getAbsolutePath() + " > :" + e.getMessage());
		}
	}
	
	private void closeReader(CSVReader reader) {
		if(null != reader) {
			try {
				reader.close();
			} catch (IOException e) {
				LOGGER.error("Error occured while closing CSVReader:" + e.getMessage());
			}
		}
	}
}
