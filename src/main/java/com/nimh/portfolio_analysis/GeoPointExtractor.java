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
	
	private final Map<Integer,Pair<Float,Float>> zipToCoords = new HashMap<>(); 
	
	public GeoPointExtractor(String postalDataBaseFileName){
		ClassLoader classLoader = getClass().getClassLoader();
		try(FileReader fileReader = new FileReader(new File(classLoader.getResource(postalDataBaseFileName).getFile()));
			BufferedReader bufferedReader = new BufferedReader(fileReader) ; 
			CSVReader csvReader = new CSVReader(bufferedReader, '\t')
			){
			String [] nextLine; 
			while((nextLine = csvReader.readNext()) != null){
				if(nextLine[1] != null && nextLine[10] != null && nextLine[11] != null)
					zipToCoords.put(Integer.parseInt(nextLine[1])
							        , Pair.of(Float.parseFloat(nextLine[10])
							        , Float.parseFloat(nextLine[10]))); 
			}

			
		}
		catch(Exception e){
			throw new RuntimeException(e); 
		}
	}
	
	public Map<Integer,Pair<Float,Float>> getZipToCoords(){
		return zipToCoords; 
	}
}
