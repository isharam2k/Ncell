package com.pelatro.entitylist.upload.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class EntListUploadServer {

	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext( "/mviva_offers", new EntUploadHandler( ) );
		server.setExecutor( null );
		server.start();

	}

}
