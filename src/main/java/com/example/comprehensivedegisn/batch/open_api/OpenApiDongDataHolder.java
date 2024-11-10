package com.example.comprehensivedegisn.batch.open_api;


import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.repository.dong.QuerydslDongRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@RequiredArgsConstructor
public class OpenApiDongDataHolder {

    private final QuerydslDongRepository QuerydslDongRepository;
    private Map<String, Integer> dongMap;

    @Value("#{jobParameters[regionalCode]}")
    private String regionalCode;

    @PostConstruct
    public void init(){
        dongMap = QuerydslDongRepository.findByGuToMap(Gu.getGuFromRegionalCode(regionalCode));
    }

    public Integer getDongEntityId(String dongCode){
        return dongMap.get(dongCode);
    }
}
