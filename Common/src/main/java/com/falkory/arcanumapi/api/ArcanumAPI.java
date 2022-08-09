package com.falkory.arcanumapi.api;

import com.falkory.arcanumapi.book.BookLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcanumAPI {

	public static final String MOD_ID = "arcanumapi";
	public static final String MOD_NAME = "Arcanum API";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
	public static BookLoader bookLoader;
}