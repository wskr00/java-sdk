/*
 * Copyright 2026-2026 the original author or authors.
 */

package io.modelcontextprotocol.json;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents an explicit JSON {@code null}, distinct from an absent optional field
 * represented by Java {@code null}. For example, pass {@link #INSTANCE} as structured
 * content to include {@code "structuredContent": null} in a tool result.
 *
 * <p>
 * Custom {@link McpJsonMapper} implementations must serialize this value as JSON
 * {@code null}. Custom {@link io.modelcontextprotocol.json.schema.JsonSchemaValidator}
 * implementations must validate it as JSON {@code null}.
 */
public enum JsonNull {

	/**
	 * The JSON {@code null} value.
	 */
	INSTANCE;

	/**
	 * Returns the JSON representation of this value.
	 * @return {@code null}
	 */
	@JsonValue
	public Object value() {
		return null;
	}

	@Override
	public String toString() {
		return "null";
	}

}
