package com.pelatro.entitylist.upload.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EntUploadHandler implements HttpHandler {

	private String filePath;

	private static final String FILENAME_DELIMETER = "@";

	private static final String FILE_SEPARATOR = "/";

	private static final String TXT = ".txt";

	public EntUploadHandler( String path ) {
		this.filePath = path;
	}

	@Override
	public void handle( HttpExchange t ) throws IOException {
		System.out.println( "---- Recieved request" );
		for ( Entry<String, List<String>> header : t.getRequestHeaders().entrySet() ) {
			System.out.println( header.getKey() + ": " + header.getValue().get( 0 ) );
		}
		DiskFileItemFactory d = new DiskFileItemFactory();

		try {
			ServletFileUpload up = new ServletFileUpload( d );
			List<FileItem> result = up.parseRequest( new RequestContext() {

				@Override
				public String getCharacterEncoding() {
					return "UTF-8";
				}

				@Override
				public int getContentLength() {
					return 0; //tested to work with 0 as return
				}

				@Override
				public String getContentType() {
					return t.getRequestHeaders().getFirst( "Content-type" );
				}

				@Override
				public InputStream getInputStream() throws IOException {
					return t.getRequestBody();
				}

			} );
			t.getResponseHeaders().add( "Content-type", "text/plain" );
			t.sendResponseHeaders( 200, 0 );
			OutputStream os = t.getResponseBody();
			Map<String, String> tagsMap = new HashMap<String, String>();
			InputStream inputStream = null;
			for ( FileItem fi : result ) {
				if ( fi.isFormField() ) {
					String fieldName = fi.getFieldName();
					String fieldValue = fi.getString();
					tagsMap.put( fieldName, fieldValue );
					System.out.println( "filename" + fieldName + "fieldValue" + fieldValue );
				}
				else {
					inputStream = fi.getInputStream();
				}
			}
			writeToDirectory( tagsMap, inputStream );
			os.close();

		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	private void writeToDirectory( Map<String, String> tags, InputStream stream ) throws IOException {
		if ( stream == null ) {
			return;
		}
		String processId = tags.get( "processId" );
		String buId = tags.get( "buId" );
		String entityId = tags.get( "entityId" );
		String entityName = tags.get( "entityListName" );
		String containerId = tags.get( "containerId" );
		Random rand = new Random();
		int number = rand.nextInt( ( 9999999 - 1024 ) + 1 ) + 1024;
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyymmdd" );
		String formatedDate = simpleDateFormat.format( date );
		String fileName = filePath + FILE_SEPARATOR + entityName + FILENAME_DELIMETER 
				+ processId + FILENAME_DELIMETER + entityId + FILENAME_DELIMETER 
				+ containerId + FILENAME_DELIMETER + buId + FILENAME_DELIMETER 
				+ number + FILENAME_DELIMETER + formatedDate + TXT;
		File file = new File( fileName );
		FileUtils.copyInputStreamToFile( stream, file );
	}

}
