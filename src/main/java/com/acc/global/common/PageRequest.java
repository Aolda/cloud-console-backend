package com.acc.global.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequest {

    private String marker;
    private Direction direction = Direction.next;
    private Integer limit = 10;

    public enum Direction {
        next,
        prev
    }

}
