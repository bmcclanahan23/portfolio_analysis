package com.nimh.portfolio_analysis;

import com.opencsv.CSVWriter;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.simple.*;

import java.io.BufferedWriter; 
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONObject;
import org.json.JSONArray; 

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
			FileWriter fileWriter = new FileWriter(portfolioFile.getParent()+"\\relations-"+ portfolioFile.getName()+".json"); 
		    BufferedWriter bf = new BufferedWriter(fileWriter))
		{
			Iterable<CSVRecord> csvRecords = CSVFormat.EXCEL.withHeader().parse(fileReader); 
			Integer i = 0; 
			for(CSVRecord csvRecord: csvRecords){
				JSONObject grantObject = new JSONObject(); 
				grantObject.put("title",csvRecord.get("Title"));
				grantObject.put("pi_name", csvRecord.get("PI Name (Contact)")); 
				grantObject.put("animal", csvRecord.get("Animal ")); 
				grantObject.put("human", csvRecord.get("Human "));
				grantObject.put("fiscal_year", csvRecord.get("FY")); 
				grantObject.put("grant", csvRecord.get("Grant ")); 
				String saText = csvRecord.get("SA Text"); 
				if(saText.length()>2)
				{
					JSONArray relationArray = new JSONArray(); 
				    Document doc = new Document(saText);
				    for (Sentence sentence : doc.sentences()) {
				    	JSONObject relationObject = new JSONObject(); 
				        for(RelationTriple triple: sentence.openieTriples()){
				        	relationObject.put("confidence", triple.confidenceGloss()); 
				        	relationObject.put("subject", triple.subjectGloss());
				        	relationObject.put("relation", triple.relationGloss());
				        	relationObject.put("object", triple.objectGloss());
				        	relationArray.put(relationObject); 
				        }
				        
				    }
				    grantObject.put("relations", relationArray); 
	                if(i++%20==0)
	                {
	                	System.out.println("number of grants annotated "+i);
	                	logger.debug("number of grants annotated {}",i);
	                }
				}
				grantObject.put("sa_text", saText); 
				bf.write(grantObject.toString()+"\n"); 
			}
		}
		catch(Exception e){
			System.out.println("error now in exception "+e.getMessage());
			throw new RuntimeException(e.getMessage()); 
		}
	}

}
