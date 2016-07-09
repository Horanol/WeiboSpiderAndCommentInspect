package com.Weibo.Exceptions;

public class EmptyPageException extends LogicException {
	public EmptyPageException() {
	}

	public EmptyPageException(String errMessage) {
		super.errMessage = errMessage;
	}
}
