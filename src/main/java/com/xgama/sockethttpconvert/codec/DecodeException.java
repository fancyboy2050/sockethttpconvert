package com.xgama.sockethttpconvert.codec;

public class DecodeException extends Exception{

	private static final long serialVersionUID = 810832366504600639L;
	
	public DecodeException(){
		
	}
	
	public DecodeException(String message, Throwable cause){
		super(message, cause);
	}
	
	public DecodeException(String message){
		super(message);
	}
	
	public DecodeException(Throwable throwable){
		super(throwable);
	}

}
