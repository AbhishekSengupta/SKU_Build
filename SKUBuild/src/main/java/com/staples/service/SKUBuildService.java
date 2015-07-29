package com.staples.service;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class SKUBuildService 
{
	private ArrayList<String> fileNameList = null;
	private static final String OUTPUT_ZIP_FILE = "/home/abhishek/Documents/Output/MyFile.zip";
    private static final String SOURCE_FOLDER = "/home/abhishek/Documents/Output";
	
	public String processFiles(MultipartFile contentTemplateGenerator,MultipartFile attributeReport, MultipartFile staplesMasterStyleGuide) throws IOException
	{
		
		fileNameList = new ArrayList<>();
		String status="Processing Failed.";
		System.out.println(contentTemplateGenerator);
		System.out.println(attributeReport);
		System.out.println(staplesMasterStyleGuide);		
		XSSFWorkbook cTGWorkbook = new XSSFWorkbook(contentTemplateGenerator.getInputStream());
		XSSFSheet worksheet = cTGWorkbook.getSheetAt(0);
		Set<String> uniqueClassId=new HashSet<String>();
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
		Date date = new Date();
		String dateFinal=dateFormat.format(date);
		System.out.println(dateFinal);
		String vendor="";
		for(int i=1;i < worksheet.getLastRowNum();i++) 
		{
			XSSFRow row = worksheet.getRow(i);
			uniqueClassId.add(row.getCell(1).getRawValue());
		}
		File file=null;
		String fileName = null;
		for(String s:uniqueClassId)
		{
			for(int i=1;i < worksheet.getLastRowNum();i++)
			{
				XSSFRow row = worksheet.getRow(i);
				String vendortemp= row.getCell(6).getStringCellValue();
				String classId= row.getCell(1).getRawValue();
				if(s.equals(classId))
				{
					if(!vendor.equalsIgnoreCase(vendortemp))
					{
						fileName = SOURCE_FOLDER+"/"+s+"_"+vendortemp+"_"+dateFinal+".xlsx";
						
						file = new File(fileName);
						if (file.createNewFile())
						{
					        System.out.println("File is created:"+file.getName());
					        vendor=vendortemp;
					    }
					}
				}
			}
			vendor="";
			InputStream fin=new
			FileInputStream("src/main/java/com/staples/product/template/Content_Smartsheet_Template- CTG 7 15 15.xlsx");
			XSSFWorkbook cTGWorkbook_temp=new XSSFWorkbook(fin);
			FileOutputStream fout=new FileOutputStream(file);
			cTGWorkbook_temp.write(fout);
			fout.close();
			System.out.println("Output File Format Created!!");
			fileNameList.add(fileName);
		}
		
		if(fileNameList!=null && !fileNameList.isEmpty()){			
	    	zipIt(OUTPUT_ZIP_FILE);
		}
		
		status=OUTPUT_ZIP_FILE.split("/")[5];
		return status;
	}
	
	
	 public void zipIt(String zipFile){
		 
	     byte[] buffer = new byte[1024];
	 
	     try{
	 
	    	FileOutputStream fos = new FileOutputStream(zipFile);
	    	ZipOutputStream zos = new ZipOutputStream(fos);
	 
	    	System.out.println("Output to Zip : " + zipFile);
	 
	    	for(String file : fileNameList){
	 
	    		System.out.println("File Added : " + file);
	    		ZipEntry ze= new ZipEntry(file);
	        	zos.putNextEntry(ze);
	 
	        	FileInputStream in = 
	                       new FileInputStream(file);
	 
	        	int len;
	        	while ((len = in.read(buffer)) > 0) {
	        		zos.write(buffer, 0, len);
	        	}
	 
	        	in.close();
	    	}
	 
	    	zos.closeEntry();
	    	//remember close it
	    	zos.close();
	 
	    	System.out.println("Done");
	    }catch(IOException ex){
	       ex.printStackTrace();   
	    }
	   }
	 
	 
	  
	  private String generateZipEntry(String file){
	    	return file.substring(SOURCE_FOLDER.length()+1, file.length());
	    }
	 
}