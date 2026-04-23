package com.omnaphade.custom_exception;

public class InsufficientResourcesException extends RuntimeException {
	public InsufficientResourcesException(String errMesg) {
		super(errMesg);
	}
}
