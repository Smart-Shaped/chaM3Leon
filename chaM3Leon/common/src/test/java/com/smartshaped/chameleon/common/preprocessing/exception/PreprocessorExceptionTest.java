package com.smartshaped.chameleon.common.preprocessing.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PreprocessorExceptionTest {

	@Test
	void testConstructorWithMessage() {
		String errorMessage = "Exception in the Preprocessor";
		PreprocessorException exception = new PreprocessorException(errorMessage);

		String expectedMessage = "Exception in the Preprocessor. Caused by: \n" + errorMessage;
		assertEquals(expectedMessage, exception.getMessage());
	}

	@Test
	void testConstructorWithThrowable() {
		Throwable cause = new RuntimeException("Runtime error");
		PreprocessorException exception = new PreprocessorException(cause);

		assertAll(() -> assertNotNull(exception),
				() -> assertTrue(exception.getMessage().contains("Exception in the Preprocessor")),
				() -> assertTrue(exception.getMessage().contains("Runtime error")),
				() -> assertEquals(cause, exception.getCause()));
	}

	@Test
	void testConstructorWithMessageAndThrowable() {
		String errorMessage = "Exception in the Preprocessor";
		Throwable cause = new RuntimeException("Runtime error");
		PreprocessorException exception = new PreprocessorException(errorMessage, cause);

		String expectedMessage = errorMessage + "\n" + cause.getMessage();
		assertEquals(expectedMessage, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

	@Test
	void testExceptionThrown() {
		assertThrows(PreprocessorException.class, () -> {
			throw new PreprocessorException("Test com.smartshaped.chameleon.batch.exception");
		});
	}

	@Test
	void testExceptionThrownWithCause() {
		Throwable cause = new IllegalArgumentException("Illegal Argument");
		PreprocessorException exception = assertThrows(PreprocessorException.class, () -> {
			throw new PreprocessorException("Test com.smartshaped.chameleon.batch.exception", cause);
		});
		assertEquals(cause, exception.getCause());
	}
}
