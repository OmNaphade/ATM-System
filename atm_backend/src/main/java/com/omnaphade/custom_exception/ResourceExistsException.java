package com.omnaphade.custom_exception;

public class ResourceExistsException extends RuntimeException {
	public ResourceExistsException(String errMesg) {
		super(errMesg);
	}
}
