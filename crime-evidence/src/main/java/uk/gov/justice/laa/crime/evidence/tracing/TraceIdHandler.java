package uk.gov.justice.laa.crime.evidence.tracing;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TraceIdHandler {

    private final Tracer tracer;

    public String getTraceId() {
        return Optional.of(tracer).
                map(Tracer::currentTraceContext).
                map(CurrentTraceContext::context).
                map(TraceContext::traceId).
                orElse("");
    }
}
