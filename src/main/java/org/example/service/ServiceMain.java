package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ConfigBucket;
import org.example.dto.DescriptionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ServiceMain {

    private ExecutorService executorService;
    private AtomicLong n = new AtomicLong();

    @Autowired
    public ServiceMain(ConfigBucket configBucket) {
        this.executorService = Executors.newFixedThreadPool((int) configBucket.getTokens());
    }

    public boolean create(DescriptionDTO text) throws ExecutionException, InterruptedException {
        return executorService.submit(() -> {
            n.incrementAndGet();
            FileWriter fileWriter = new FileWriter("file ".concat(n.toString()).concat(".json"), false);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(fileWriter, text);
            return true;
        }).get();
    }
}
