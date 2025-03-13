package com.example.comprehensivedegisn.adapter.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NativeSetHint {
    USE_INDEX("USE INDEX");

    private final String name;


    @Getter
    @RequiredArgsConstructor
    public enum Index {
        IDX_GU_DONG_NAME("idx_gu_dong_name"),
        IDX_DEAL_DATE("idx_deal_date");

        private final String name;
    }
}
