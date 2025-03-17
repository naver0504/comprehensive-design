
package com.example.comprehensivedegisn.api_client.predict.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;

public interface ApartmentQuery {
    Gu getGu();
    String getDongName();
    double getAreaForExclusiveUse();
    int getFloor();
    int getBuildYear();
}
