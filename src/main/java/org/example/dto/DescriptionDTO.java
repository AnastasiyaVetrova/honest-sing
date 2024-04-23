package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

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
