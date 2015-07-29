package com.staples.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staples.service.SKUBuildService;

@RestController
@RequestMapping("/services/")
public class ServiceController 
{
	private static final String SOURCE_FOLDER = "/home/abhishek/Documents/Output/";
	private static int BUFFER_SIZE = 100000;
	@Autowired
	SKUBuildService skuBuildService;
	@RequestMapping(value="uploadService", method=RequestMethod.POST,consumes="multipart/form-data")
	public List<String> uploadService(@RequestParam("ContentTemplateGenerator") MultipartFile contentTemplateGenerator,
			@RequestParam("AttributeReport") MultipartFile attributeReport, @RequestParam("StaplesMasterStyleGuide") MultipartFile staplesMasterStyleGuide)
	{
		String status="Processing Failed.";
		System.out.println("Inside Service");
		List<String> statusList=new ArrayList<String>();
		try
		{
			
			status=skuBuildService.processFiles(contentTemplateGenerator,attributeReport,staplesMasterStyleGuide);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		statusList.add(status);
		return statusList;
	}
	
	@RequestMapping(value="download", method=RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody void handleFileUpload(@RequestParam(value="fileName") String fileName,HttpServletRequest request,
            HttpServletResponse response){
		try{
		File downloadFile = new File(SOURCE_FOLDER+fileName);
        FileInputStream inputStream = new FileInputStream(downloadFile);
        ServletContext context = request.getServletContext();
        // get MIME type of the file
        String mimeType = context.getMimeType(SOURCE_FOLDER+fileName);
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        System.out.println("MIME type: " + mimeType);
 
        // set content attributes for the response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
 
        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",downloadFile.getName());
        response.setHeader(headerKey, headerValue);
 
        // get output stream of the response
        OutputStream outStream = response.getOutputStream();
 
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
 
        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
 
        inputStream.close();
        outStream.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
