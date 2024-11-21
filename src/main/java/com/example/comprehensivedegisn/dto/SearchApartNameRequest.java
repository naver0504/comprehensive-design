package com.example.comprehensivedegisn.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchApartNameRequest {

    private Gu gu;
    private String dong;

    public boolean isNotValid() {
        return isGuNotValid() | !StringUtils.hasText(dong);
    }

    private boolean isGuNotValid() {
        return gu == null || gu == Gu.NONE;
    }
}
