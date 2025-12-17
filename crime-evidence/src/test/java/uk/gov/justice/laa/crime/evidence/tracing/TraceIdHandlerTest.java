package uk.gov.justice.laa.crime.evidence.tracing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        assertThat(traceIdHandler.getTraceId().isBlank());
    }

    @Test
    void givenNullTraceContext_whenGetTraceIdIsInvoked_thenBlankIsReturned() {
        when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
        when(currentTraceContext.context()).thenReturn(null);
        assertThat(traceIdHandler.getTraceId().isBlank());
    }

    @Test
    void givenNonNullTraceContext_whenGetTraceIdIsInvoked_thenTraceIdIsReturned() {
        when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
        when(currentTraceContext.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn(TRACE_ID);
        assertThat(traceIdHandler.getTraceId()).isEqualTo(TRACE_ID);
    }
}
