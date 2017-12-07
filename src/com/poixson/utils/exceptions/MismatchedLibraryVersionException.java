package com.poixson.utils.exceptions;


public class MismatchedLibraryVersionException extends Exception {
	private static final long serialVersionUID = 1L;

	public final String expectedVersion;
	public final String libraryVersion;



	public static MismatchedLibraryVersionException getNew(
			final String expectedVersion, final String libraryVersion) {
		return new MismatchedLibraryVersionException(
			expectedVersion,
			libraryVersion
		);
	}
	public MismatchedLibraryVersionException(
			final String expectedVersion, final String libraryVersion) {
		super(
			(new StringBuilder())
				.append("Expected version: ")
				.append(expectedVersion)
				.append(" found library version: ")
				.append(libraryVersion)
				.toString()
		);
		this.expectedVersion = expectedVersion;
		this.libraryVersion  = libraryVersion;
	}



}
