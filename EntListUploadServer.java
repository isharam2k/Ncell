package com.pelatro.entitylist.upload.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class EntListUploadServer {

	public static void main( String[] args ) throws IOException {
		HttpServer server = HttpServer.create( new InetSocketAddress( 8000 ), 0 );
		server.createContext( "/ent_list_upload", new EntUploadHandler( args[0] ) );
		server.createContext( "/ent_list_fetch_status", new EntListStatusFetchHandler() );
		System.out.println( "Starting server at 8000!!" );
		server.setExecutor( null );
		server.start();

	}

}
