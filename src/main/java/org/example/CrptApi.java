package org.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class CrptApi {
    public static void main(String[] args) {
        SpringApplication.run(org.example.Main.class, args);
    }

    @RestController
    @RequestMapping("/api/v3/lk/documents")
    public class Controller {
        private final ServiceMain serviceMain;
        private final Bucket bucket;

        @Autowired
        public Controller(ServiceMain serviceMain, ConfigBucket configBucket) {
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
    @Component
    @ConfigurationProperties(prefix = "bucket-settings")
    @Getter
    @Setter
    public class ConfigBucket {
        private long capacity;
        private long tokens;
        private long time;
    }
    @Getter
    @Setter
    public class DescriptionDTO {

        private ParticipantInnDTO description;
        @JsonProperty("doc_id")
        private String docId;
        @JsonProperty("doc_status")
        private String docStatus;
        @JsonProperty("doc_type")
        private TypeDTO docType;
        private boolean importRequest;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("participant_inn")
        private String participantInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date productionDate;
        @JsonProperty("production_type")
        private String productionType;
        private List<ProductDTO> products;
        @JsonProperty("reg_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date regDate;
        @JsonProperty("reg_number")
        private String regNumber;

    }
    @Getter
    @Setter
    public class ParticipantInnDTO {

        private String participantInn;
    }
    @Getter
    @Setter
    public class ProductDTO {
        @JsonProperty("certificate_document")
        private String certificateDocument;
        @JsonProperty("certificate_document_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date certificateDocumentDate;
        @JsonProperty("certificate_document_number")
        private String certificateDocumentNumber;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date productionDate;
        @JsonProperty("tnved_code")
        private String tnvedCode;
        @JsonProperty("uit_code")
        private String uitCode;
        @JsonProperty("uitu_code")
        private String uituCode;
    }
    public enum TypeDTO {
        LP_INTRODUCE_GOODS(109);
        private int code;

        TypeDTO(int code) {
            this.code = code;
        }
    }
}