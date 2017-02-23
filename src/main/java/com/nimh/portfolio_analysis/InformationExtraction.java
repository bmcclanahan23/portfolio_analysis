package com.nimh.portfolio_analysis;

import com.opencsv.CSVWriter;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.simple.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class InformationExtraction {
	

	final static Logger logger = LoggerFactory.getLogger(InformationExtraction.class); 
	
	public static void main(String args[]){
		
		Properties props = new Properties();
		logger.debug("loading annotators");
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
	    logger.debug("done loading annotator");

		File portfolioFile = new File(args[0]); 
		 
		System.out.println("parent "+portfolioFile.getParent()+"\\"+ portfolioFile.getName());
		try(FileReader fileReader =  new FileReader(portfolioFile); 
			FileWriter fileWriter = new FileWriter(portfolioFile.getParent()+"\\relations-"+ portfolioFile.getName());
		    CSVWriter csvWriter =  new CSVWriter(fileWriter)) 
		{
			Iterable<CSVRecord> csvRecords = CSVFormat.EXCEL.withHeader().parse(fileReader); 
			Integer i = 0; 
			String[] row = new String[]{"grant","confidence","subject","relation","object"};
			csvWriter.writeNext(row);
			for(CSVRecord csvRecord: csvRecords){
				String saText = csvRecord.get("SA Text"); 
				String grant = csvRecord.get("Grant "); 
				if(saText.length()>2)
				{
				    Document doc = new Document(saText);
				    for (Sentence sentence : doc.sentences()) {
				        for(RelationTriple triple: sentence.openieTriples()){
				        	row[0] = grant; 
				        	row[1] = triple.confidenceGloss(); 
				        	row[2] = triple.subjectGloss(); 
				        	row[3] = triple.relationGloss(); 
				        	row[4] = triple.objectGloss(); 
				        	csvWriter.writeNext(row);
				        }
				        
				    }
	                if(i%20==0)
	                	logger.debug("nuber of grants annotated {}",i);
				}
			}
		}
		catch(Exception e){
			System.out.println("error now in exception "+e.getMessage());
			throw new RuntimeException(e.getMessage()); 
		}
	}

}
