package com.pelatro.entitylist.upload.server;

import java.util.ArrayList;
import java.util.List;

public class EntListUidLookUp {

	private static EntListUidLookUp instance;

	private List<String> uids = new ArrayList<String>();

	private EntListUidLookUp() {
	}

	public static EntListUidLookUp getInstance() {
		if ( instance == null ) {
			instance = new EntListUidLookUp();
		}
		return instance;
	}

	public void loadUids( List<String> uids ) {
		this.uids = uids;
	}

	public void add( String uid ) {
		uids.add( uid );
	}

	public boolean checkForUid( String uid ) {
		if ( uids.contains( uid ) ) {
			return true;
		}
		return false;
	}

}
