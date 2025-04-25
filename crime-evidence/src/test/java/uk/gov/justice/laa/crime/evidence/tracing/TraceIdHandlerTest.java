package uk.gov.justice.laa.crime.evidence.tracing;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceIdHandlerTest {

    private static final String TRACE_ID = "trace-id";

    @Mock
    Tracer tracer;
    @Mock
    CurrentTraceContext currentTraceContext;
    @Mock
    TraceContext traceContext;
    @InjectMocks
    TraceIdHandler traceIdHandler;

    @Test
    void givenNullCurrentTraceContext_whenGetTraceIdIsInvoked_thenBlankIsReturned() {
        when(tracer.currentTraceContext()).thenReturn(null);
        assertTrue(traceIdHandler.getTraceId().isBlank());
    }

    @Test
    void givenNullTraceContext_whenGetTraceIdIsInvoked_thenBlankIsReturned() {
        when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
        when(currentTraceContext.context()).thenReturn(null);
        assertTrue(traceIdHandler.getTraceId().isBlank());
    }

    @Test
    void givenNonNullTraceContext_whenGetTraceIdIsInvoked_thenTraceIdIsReturned() {
        when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
        when(currentTraceContext.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn(TRACE_ID);
        assertEquals(TRACE_ID, traceIdHandler.getTraceId());
    }
}
