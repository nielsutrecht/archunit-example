package example.barservice.domain;

import lombok.Value;

import java.io.Serializable;

@Value
public class Bar implements Serializable {
    private String bar;
}
