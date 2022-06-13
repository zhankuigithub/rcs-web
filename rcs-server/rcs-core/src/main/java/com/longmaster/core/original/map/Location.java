package com.longmaster.core.original.map;

import lombok.Data;

@Data
public class Location {
    private Double latitude;
    private Double longitude;
    private String label = "Googleplex";
}
