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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class SKUBuildService 
{
	private ArrayList<String> fileNameList = null;
	private static final String OUTPUT_ZIP_FILE = "/home/abhishek/Documents/Output/Product.zip";
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
			//cTGWorkbook_temp.write(fout);
			XSSFWorkbook cTGWorkbookWithCTG=writeCTGI(cTGWorkbook_temp,contentTemplateGenerator,s,staplesMasterStyleGuide,attributeReport);
			//XSSFWorkbook cTGWorkbookWithAttr=writeAttribute(cTGWorkbookWithCTG, staplesMasterStyleGuide, s);
			//fout.close();
			//fin.close();
			//fout=new FileOutputStream(file);
			cTGWorkbookWithCTG.write(fout);
			fout.close();
			System.out.println("Output File Format Created!!");
			fileNameList.add(fileName);
		}
		
		if(fileNameList!=null && !fileNameList.isEmpty()){			
	    	zipIt(OUTPUT_ZIP_FILE);
		}
		
		status=OUTPUT_ZIP_FILE.split("/")[5];
		System.out.println(status);
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
	 
	  
	  public XSSFWorkbook writeCTGI(XSSFWorkbook cTGWorkbook,MultipartFile fileIn,String classId,MultipartFile staplesMasterStyleGuide, MultipartFile attributeReport)
	  {
		  try 
		  {
			List<XSSFCell> attributeCell=  writeStyle(staplesMasterStyleGuide,classId);
			List<String> attributes=getAttributes(attributeReport,classId);
			XSSFWorkbook cTGWorkbookFin = new XSSFWorkbook(fileIn.getInputStream());
			XSSFSheet worksheetIn = cTGWorkbookFin.getSheetAt(0);
			XSSFSheet worksheetOut = cTGWorkbook.getSheetAt(0);
			int i =1;
			int j=4;
			XSSFCell cell=null;
			for(i=1;i < worksheetIn.getLastRowNum();i++) 
			{
				XSSFRow rowIn = worksheetIn.getRow(i);
				XSSFRow rowOut = worksheetOut.createRow(j);
				
				if(rowIn.getCell(1).getNumericCellValue()==Integer.parseInt(classId))
				{
					XSSFCell cell1 = rowOut.createCell(13);
					if(rowIn.getCell(2)!=null)
						cell1.setCellValue(rowIn.getCell(2).getStringCellValue());
					XSSFCell cell2 = rowOut.createCell(15);
					cell2.setCellValue(classId);
					XSSFCell cell3 = rowOut.createCell(19);
					if(rowIn.getCell(3)!=null)
						cell3.setCellValue(rowIn.getCell(3).getStringCellValue());
					XSSFCell cell4 = rowOut.createCell(20);
					if(rowIn.getCell(0)!=null)
						cell4.setCellValue(rowIn.getCell(0).getStringCellValue());
					XSSFCell cell5 = rowOut.createCell(24);
					if(rowIn.getCell(3)!=null)
						cell5.setCellValue(rowIn.getCell(3).getStringCellValue());
					XSSFCell cell6 = rowOut.createCell(179);
					if(rowIn.getCell(5)!=null)
						cell6.setCellValue(rowIn.getCell(5).getNumericCellValue());
					
					XSSFCell cell7 = rowOut.createCell(26);
					cell7=attributeCell.get(0);
					
					int z=1;
					for(int k=36;k<48;k++)
					{
						cell=rowOut.createCell(k);
						cell.setCellValue((attributeCell.get(z)).getStringCellValue());
						z++;
					}
					int counter=1;
					int cellCount=63;
					for(String attribute:attributes)
					{
						
						if(counter<=50)
						{
							String lable=attribute.split("###")[0];
							String value=attribute.split("###")[1];
							cell=rowOut.createCell(cellCount);
							cell.setCellValue(lable);
							cell=rowOut.createCell(cellCount+1);
							cell.setCellValue(value);
							counter++;
							cellCount+=2;
						}
						
					}
				}
				j++;
				
			}			
		  } 
		 catch (IOException e) 
		 {
			e.printStackTrace();
		 }
		 return cTGWorkbook;
	  }
	  
	  public List<XSSFCell> writeStyle(MultipartFile fileIn,String classId)
	  {
		  List<XSSFCell> attributeCell = new ArrayList<XSSFCell>();
		  try
		  {
			  XSSFWorkbook cTGWorkbookFin = new XSSFWorkbook(fileIn.getInputStream());
			  XSSFSheet worksheetIn = cTGWorkbookFin.getSheetAt(0);
			  for(int i=1;i < worksheetIn.getLastRowNum();i++) 
			  {
				  XSSFRow rowIn = worksheetIn.getRow(i);
				  if(XSSFCell.CELL_TYPE_NUMERIC==rowIn.getCell(7).getCellType())
				  {
					  if(rowIn.getCell(7).getNumericCellValue()==Integer.parseInt(classId))
					  {
						  attributeCell.add(rowIn.getCell(14));
						  int j=20;
						  for(;j<44;)
						  {
							  attributeCell.add(rowIn.getCell(j));
							  j+=2;
						  }
						  break;
					  }
				  }
			  }
		  }
		  catch(Exception e){
			  e.printStackTrace();
		  }
		return attributeCell;
	  }
	  
	  public List<String> getAttributes(MultipartFile attributeReport,String classId)
	  {
		  List<String> attributes=new ArrayList<String>();
		  try
		  {
			  XSSFWorkbook cTGWorkbookFin = new XSSFWorkbook(attributeReport.getInputStream());
			  XSSFSheet worksheetIn = cTGWorkbookFin.getSheetAt(0);
			  for(int i=2;i < worksheetIn.getLastRowNum();i++) 
			  {
				  XSSFRow rowIn = worksheetIn.getRow(i);
				  if(XSSFCell.CELL_TYPE_NUMERIC==rowIn.getCell(1).getCellType())
				  {
					  if(rowIn.getCell(1).getNumericCellValue()==Integer.parseInt(classId))
					  {
						  attributes.add(rowIn.getCell(2).getStringCellValue()+"###"+rowIn.getCell(7).getStringCellValue());
					  }
				  }
				  
			  }
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return attributes;
	  }
}
