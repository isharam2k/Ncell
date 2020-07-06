package com.pelatro.entitylist.upload.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.net.httpserver.HttpServer;

public class EntListUploadServer {

	private static final String PORT = "port";

	private static final String KEY = "key";

	private static final String FILEPATH = "filePath";

	private static final String LOGSPATH = "logsFile";

	private static final String UIDFILEPATH = "uidFile";

	public static void main( String[] args ) throws IOException {

		FileReader confFile;
		BufferedReader br;
		String line[] = new String[2];
		String text;
		HashMap<String, String> hm = new HashMap<>();
		try {
			confFile = new FileReader( args[0] );
			br = new BufferedReader( confFile );
			text = br.readLine();
			while ( text != null && text.length() != 0 ) {
				text = br.readLine();
				if ( !( text.contains( ":" ) ) )
					break;
				line = text.split( ":" );
				line[0] = line[0].replace( "\"", "" ).replace( ",", "" );
				line[1] = line[1].replace( "\"", "" ).replace( ",", "" );
				hm.put( line[0], line[1] );
			}
		}
		catch ( NullPointerException | IOException e ) {

			e.printStackTrace();
		}
		String port = hm.get( PORT );
		String key = hm.get( KEY );
		String filePath = hm.get( FILEPATH );
		String logsFilePath = hm.get( LOGSPATH );
		String uidFilePath = hm.get( UIDFILEPATH );
		List<String> uids = new ArrayList<String>();
		File file = new File( uidFilePath );
		if ( !file.exists() ) {
			file.createNewFile();
		}
		FileReader fr = new FileReader( file );
		BufferedReader bw = new BufferedReader( fr );
		String storedUid = null;
		while ( ( storedUid = bw.readLine() ) != null ) {
			uids.add( storedUid );
		}
		bw.close();
		EntListUidLookUp.getInstance().loadUids( uids );
		HttpServer server = HttpServer.create( new InetSocketAddress( Integer.parseInt( port ) ), 0 );
		server.createContext( "/ent_list_upload", new EntUploadHandler( filePath, key, uidFilePath ) );
		server.createContext( "/ent_list_fetch_status", new EntListStatusFetchHandler( key, logsFilePath ) );
		System.out.println( "Starting server at " + port + " !!" );
		server.setExecutor( null );
		server.start();

	}

}