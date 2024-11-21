package com.example.comprehensivedegisn.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchAreaRequest{

    private Gu gu;
    private String dong;
    private String apartmentName;

    public boolean isNotValid() {
        return isGuNotValid() | isDongNotValidate() || isApartmentNameNotValid();
    }

    private boolean isApartmentNameNotValid() {
        return !StringUtils.hasText(apartmentName);
    }

    private boolean isDongNotValidate() {
        return !StringUtils.hasText(dong);
    }

    private boolean isGuNotValid() {
        return gu == null || gu == Gu.NONE;
    }
}
