package com.pelatro.entitylist.upload.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class EntListUploadServer {

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
		String host = hm.get( "host" );
		String port = hm.get( "port" );
		String key = hm.get( "key" );
		String filePath = hm.get( "filePath" );

		HttpServer server = HttpServer.create( new InetSocketAddress( Integer.parseInt( port ) ), 0 );
		server.createContext( "/ent_list_upload", new EntUploadHandler( filePath, key ) );
		server.createContext( "/ent_list_fetch_status", new EntStatusHandler( key ) );
		System.out.println( "Starting server at " + port + " !!" );
		server.setExecutor( null );
		server.start();
	}

}
