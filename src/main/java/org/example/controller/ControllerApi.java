package org.example.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.example.dto.DescriptionDTO;
import org.example.service.ServiceMain;
import org.example.config.ConfigBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v3/lk/documents")
public class ControllerApi {

    private final ServiceMain serviceMain;
    private final Bucket bucket;

    @Autowired
    public ControllerApi(ServiceMain serviceMain, ConfigBucket configBucket) {
        this.serviceMain = serviceMain;
        Bandwidth limit = Bandwidth.classic(configBucket.getCapacity(),
                Refill.greedy(configBucket.getTokens(),
                        Duration.ofMinutes(configBucket.getTime())));
        this.bucket = Bucket.builder().addLimit(limit).build();
    }

    @PostMapping("/create")
    public ResponseEntity<Boolean> create(@RequestBody DescriptionDTO text) {
        if (bucket.tryConsume(1)) {
            try {
                return new ResponseEntity<>(serviceMain.create(text), HttpStatus.OK);
            } catch (ExecutionException | InterruptedException e) {
                new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(false, HttpStatus.TOO_MANY_REQUESTS);
    }
}
