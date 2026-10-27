/*
 * Copyright 2026-2026 the original author or authors.
 */

package io.modelcontextprotocol.json;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;

class McpJsonMapperTest {

	@Test
	void shouldUseJackson2Mapper() {
		assertThat(McpJsonDefaults.getMapper()).isInstanceOf(JacksonMcpJsonMapper.class);
	}

	@ParameterizedTest
	@MethodSource("customMappers")
	void callToolResultsPreserveJsonNullWithCustomMapper(McpJsonMapper mapper) throws Exception {
		String json = "{\"content\":[],\"structuredContent\":null,\"structuredContentValue\":\"ignored\",\"future\":true}";
		var legacy = mapper.readValue(json, io.modelcontextprotocol.spec.McpSchema.CallToolResult.class);
		var modern = mapper.readValue(json, io.modelcontextprotocol.modern.McpSchema.CallToolResult.class);

		assertThat(legacy.structuredContent()).isSameAs(JsonNull.INSTANCE);
		assertThat(modern.structuredContent()).isSameAs(JsonNull.INSTANCE);
		assertThat(mapper.writeValueAsString(legacy)).contains("\"structuredContent\":null");
		assertThat(mapper.writeValueAsString(modern)).contains("\"structuredContent\":null");

		String absentJson = "{\"content\":[],\"structuredContentValue\":\"ignored\"}";
		var absentLegacy = mapper.readValue(absentJson, io.modelcontextprotocol.spec.McpSchema.CallToolResult.class);
		var absentModern = mapper.readValue(absentJson, io.modelcontextprotocol.modern.McpSchema.CallToolResult.class);

		assertThat(absentLegacy.structuredContent()).isNull();
		assertThat(absentModern.structuredContent()).isNull();
		assertThat(mapper.writeValueAsString(absentLegacy)).doesNotContain("\"structuredContent\"");
		assertThat(mapper.writeValueAsString(absentModern)).doesNotContain("\"structuredContent\"");
	}

	static Stream<Named<McpJsonMapper>> customMappers() {
		ObjectMapper fieldVisibility = new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
				JsonAutoDetect.Visibility.ANY);
		ObjectMapper skipNulls = new ObjectMapper().setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP));
		return Stream.of(Named.of("field visibility ANY", new JacksonMcpJsonMapper(fieldVisibility)),
				Named.of("skip null setters", new JacksonMcpJsonMapper(skipNulls)));
	}

}
