package org.qTeam.api.core;

public class TimeParsingException extends Exception {

	private static final long serialVersionUID = 1L;

	public TimeParsingException() {
		super();
	}

	public TimeParsingException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TimeParsingException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public TimeParsingException(final String message) {
		super(message);
	}

	public TimeParsingException(final Throwable cause) {
		super(cause);
	}

}
