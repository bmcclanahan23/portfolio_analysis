package com.nimh.portfolio_analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader; 
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair; 

public class GeoPointExtractor {
	final static Logger logger = LoggerFactory.getLogger(GeoPointExtractor.class); 
	
	private final Map<String,Pair<Float,Float>> zipToCoords = new HashMap<>(); 
	
	public GeoPointExtractor(String postalDataBaseFileName){
		ClassLoader classLoader = getClass().getClassLoader();
		try(FileReader fileReader = new FileReader(new File(classLoader.getResource(postalDataBaseFileName).getFile()));
			BufferedReader bufferedReader = new BufferedReader(fileReader) ; 
			CSVReader csvReader = new CSVReader(bufferedReader, '\t')
			){
			String [] nextLine; 
			Integer i=0; 
			while((nextLine = csvReader.readNext()) != null){
				if(nextLine[1] != null && nextLine[9] != null && nextLine[10] != null)
					zipToCoords.put(nextLine[1]
							        , Pair.of(Float.parseFloat(nextLine[9])
							        , Float.parseFloat(nextLine[10]))); 
				if(i++%100000==0){
					logger.debug("on row {}",i);
				}
			}

			
		}
		catch(Exception e){
			throw new RuntimeException(e); 
		}
	}
	
	public Map<String,Pair<Float,Float>> getZipToCoords(){
		return zipToCoords; 
	}
}
