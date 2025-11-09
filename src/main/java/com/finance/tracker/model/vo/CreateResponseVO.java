package com.finance.tracker.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class CreateResponseVO {
    private final UUID id;
}
