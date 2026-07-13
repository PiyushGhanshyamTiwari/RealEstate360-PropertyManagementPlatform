package com.cts.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.function.Executable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cts.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Common reusable support for every controller unit test.
 *
 * <p>It centralises the three pieces of boilerplate shared by all controller tests:
 * <ul>
 *     <li>a Jackson {@link ObjectMapper} that understands {@code java.time} types so request
 *     payloads containing {@code LocalDate}/{@code LocalDateTime} serialize correctly,</li>
 *     <li>a {@link MockMvc} factory using {@code standaloneSetup} wired with the real
 *     {@link GlobalExceptionHandler} so exception-to-status mappings are exercised, and</li>
 *     <li>a helper that asserts an unhandled service failure is surfaced (not swallowed) by the
 *     controller, which is how unmapped exceptions manifest with {@code standaloneSetup}.</li>
 * </ul>
 */
public abstract class AbstractControllerTest {

    /** Shared, thread-safe Jackson mapper used to build JSON request bodies. */
    protected static final ObjectMapper OBJECT_MAPPER = buildObjectMapper();

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Builds a {@link MockMvc} instance for a single controller using standalone setup and the
     * application's real {@link GlobalExceptionHandler}.
     *
     * @param controller the controller under test (already created via {@code @InjectMocks})
     * @return a ready-to-use {@link MockMvc}
     */
    protected MockMvc buildMockMvc(Object controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * Serializes any object to a JSON string using the shared mapper.
     *
     * @param value the payload to serialize
     * @return the JSON representation
     */
    protected String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize test payload", ex);
        }
    }

    /**
     * Asserts that performing a request surfaces a server-side failure whose message contains the
     * expected fragment somewhere in the exception chain. Unmapped runtime exceptions thrown by a
     * service are not converted to a JSON 500 body by {@code standaloneSetup}; instead they bubble
     * out of {@code MockMvc.perform(...)}, so this verifies the controller does not swallow them.
     *
     * @param expectedMessageFragment text expected somewhere in the thrown exception chain
     * @param request                 the request execution to run
     */
    protected void assertRequestFailsWith(String expectedMessageFragment, Executable request) {
        Throwable thrown = assertThrows(Throwable.class, request);
        boolean matched = false;
        Throwable current = thrown;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains(expectedMessageFragment)) {
                matched = true;
                break;
            }
            current = current.getCause();
        }
        assertTrue(matched,
                "Expected an exception in the chain containing: " + expectedMessageFragment);
    }
}
