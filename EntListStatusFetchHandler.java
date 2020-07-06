package com.pelatro.entitylist.upload.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EntListStatusFetchHandler implements HttpHandler {

	private String uid;

	private String key;

	private String logsFilePath;

	private static final String ENTITY_LIST_FILENAME_DELIMITER = "@";

	private static final String ENTITY_LIST_NAME = "entityListName";

	private static final String TOTAL_RECORDS = "totalRecords";

	private static final String ACCEPTED_RECORDS = "acceptedRecords";

	private static final String REJECTED_RECORDS = "rejectedRecords";

	private static final String ENTITY_LIST_UID = "entityListUid";

	private static final String STATUS_RESPONSE = "statusResponse";

	String authorizationFails = "Authorization failed";

	public EntListStatusFetchHandler( String key, String logsFilePath ) {
		this.key = key;
		this.logsFilePath = logsFilePath;
	}

	@Override
	public void handle( HttpExchange t ) throws IOException {
		OutputStream os = t.getResponseBody();
		String query = t.getRequestURI().getQuery();
		String inputKey = t.getRequestHeaders().get( "AuthorizedToken" ).toString().replaceAll( "\\p{P}", "" );
		if ( inputKey.equals( key ) ) {
			System.out.println( "Serving request Fetch Status " );
			String fileName;

			File folderPath = new File( logsFilePath );

			File listOfFiles[] = folderPath.listFiles();

			JSONObject apiInfo = new JSONObject();
			String values[] = query.split( "=" );
			uid = values[1];
			String uploadStatus = "Upload Success";
			String status = "Upload Pending for this uid: " + uid;
			String nopresent = "UID " + uid + " is invalid!!!!";
			boolean isPresent = false;
			for ( File file : listOfFiles ) {
				if ( file.isFile() ) {
					if ( file.getName().endsWith( uid + ".txt" ) ) {
						fileName = file.getName();
						System.out.println( "File name " + fileName );
						String data[] = fileName.split( ENTITY_LIST_FILENAME_DELIMITER );
						if ( Integer.valueOf( ( data[2] ) ) == 0 && Integer.valueOf( ( data[3] ) ) == 0 && Integer.valueOf( ( data[4] ) ) == 0 )
							uploadStatus = "Upload Failed";
						try {
							apiInfo.put( ENTITY_LIST_NAME, data[0] );
							apiInfo.put( STATUS_RESPONSE, uploadStatus );
							apiInfo.put( TOTAL_RECORDS, data[2] );
							apiInfo.put( ACCEPTED_RECORDS, data[3] );
							apiInfo.put( REJECTED_RECORDS, data[4] );
							apiInfo.put( ENTITY_LIST_UID, uid );
						}
						catch ( Exception e ) {
							System.out.println( "Error while generation JSON reponse" );
						}
						isPresent = true;
						t.getResponseHeaders().add( "Content-type", "text/plain" );
						t.sendResponseHeaders( 200, 0 );
						os.write( apiInfo.toString().getBytes() );
						os.flush();
						break;
					}
				}
			}
			isPresent = EntListUidLookUp.getInstance().checkForUid( uid );
			if ( !isPresent ) {
				JSONObject entListJson = new JSONObject();
				try {
					entListJson.put( STATUS_RESPONSE, nopresent );
				}
				catch ( JSONException e ) {
					System.out.println( "Error occurred while forming JSON response body" );
					e.printStackTrace();
				}
				t.getResponseHeaders().add( "Content-type", "text/plain" );
				t.sendResponseHeaders( 200, 0 );
				os.write( entListJson.toString().getBytes() );
				os.flush();
			}
			JSONObject entListJson = new JSONObject();
			try {
				entListJson.put( STATUS_RESPONSE, status );
			}
			catch ( JSONException e ) {
				System.out.println( "Error occurred while forming JSON response body" );
				e.printStackTrace();
			}
			t.getResponseHeaders().add( "Content-type", "text/plain" );
			t.sendResponseHeaders( 200, 0 );
			os.write( entListJson.toString().getBytes() );
			os.flush();
		}
		else {
			String code = "Authorization Failed";
			JSONObject entListJson = new JSONObject();
			try {
				entListJson.put( STATUS_RESPONSE, code );
			}
			catch ( JSONException e ) {
				System.out.println( "Error occurred while forming JSON response body" );
				e.printStackTrace();
			}
			t.getResponseHeaders().add( "Content-type", "text/plain" );
			t.sendResponseHeaders( 400, 0 );
			String outpusRes = entListJson.toString();
			os.write( outpusRes.getBytes() );
		}

		os.close();
	}

}