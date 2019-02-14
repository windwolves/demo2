package com.leading.demo2.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Valid
public class SmsObject {
    @NotNull(message = "must not be null.")
    @Size(max=765)
    private String message;

    @NotNull(message = "must not be null.")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @NotEmpty
    private List<String> targetNumbers;

    private String sender;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getTargetNumbers() {
        return targetNumbers == null ? new ArrayList<>() : new ArrayList<>(targetNumbers);
    }

    public void setTargetNumbers(List<String> targetNumbers) {
        this.targetNumbers = targetNumbers == null ? new ArrayList<>() : new ArrayList<>(targetNumbers);
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTargetNumbersString() {
        StringBuilder targetNumbersStringBuilder = new StringBuilder();
        if (targetNumbers != null) {
            for (int i = 0; i < targetNumbers.size(); i++) {
                if (i == 0) {
                    targetNumbersStringBuilder.append(targetNumbers.get(0));
                } else {
                    targetNumbersStringBuilder.append(",");
                    targetNumbersStringBuilder.append(targetNumbers.get(i));
                }
            }
        }
        return targetNumbersStringBuilder.toString();
    }

    public String getSender(){
        return sender;
    }

}
