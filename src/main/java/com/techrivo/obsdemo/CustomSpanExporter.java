package com.techrivo.obsdemo;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CustomSpanExporter implements SpanExporter {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Logger log = LoggerFactory.getLogger(CustomSpanExporter.class);
    private final File logFile;

    public CustomSpanExporter() throws IOException {
        FileSystemResource resource = new FileSystemResource("span-exports.log");
        this.logFile = resource.getFile();
        if (!logFile.exists()) {
            boolean newFile = logFile.createNewFile();
            log.info("Created new log file: {}", newFile);
        }
        log.info("File absolute path: {}", logFile.getAbsolutePath());
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> collection) {
        CompletableResultCode resultCode = new CompletableResultCode();
        executorService.submit(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                for (SpanData span : collection) {
                    writer.write(formatSpanData(span));
                    writer.newLine();
                }
                log.debug("Successfully exported {} spans to file.", collection.size());
                resultCode.succeed();
            } catch (IOException e) {
                log.warn("Failed to export spans to file.", e);
                resultCode.fail();
            }
        });
        return resultCode;
    }

    private String formatSpanData(SpanData span) {
        return String.format("SpanId: %s, TraceId: %s, Name: %s, Start: %d, End: %d, Attributes: %s",
                span.getSpanId(),
                span.getTraceId(),
                span.getName(),
                span.getStartEpochNanos(),
                span.getEndEpochNanos(),
                span.getAttributes().asMap());
    }

    @Override
    public CompletableResultCode flush() {
        return null;
    }

    @Override
    public CompletableResultCode shutdown() {
        return null;
    }
}