package org.springframework.data.util;

/**
 * Compatibility interface for Spring Data 4 / Spring Boot 4 to satisfy Springdoc OpenAPI introspection.
 *
 * @param <S> the type
 */
public interface TypeInformation<S> extends org.springframework.data.core.TypeInformation<S> {
}
