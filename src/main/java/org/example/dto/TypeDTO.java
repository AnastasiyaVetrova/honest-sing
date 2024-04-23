package org.example.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TypeDTO {
    LP_INTRODUCE_GOODS(109);
    private int code;

    TypeDTO(int code) {
        this.code = code;
    }
    @JsonValue
    public int getCode() {
        return code;
    }
}
