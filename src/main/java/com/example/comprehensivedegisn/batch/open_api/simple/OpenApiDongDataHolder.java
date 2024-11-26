package com.example.comprehensivedegisn.batch.open_api.simple;


import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.repository.dong.QuerydslDongRepository;
import com.example.comprehensivedegisn.batch.open_api.DataHolder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@RequiredArgsConstructor
public class OpenApiDongDataHolder implements DataHolder<String> {

    private final QuerydslDongRepository QuerydslDongRepository;
    private Map<String, Integer> dongMap;

    @Value("#{jobParameters[regionalCode]}")
    private String regionalCode;

    @PostConstruct
    @Override
    public void init(){
        dongMap = QuerydslDongRepository.findByGuToMap(Gu.getGuFromRegionalCode(regionalCode));
    }

    @Override
    public Integer getDongEntityId(String dongCode){
        return dongMap.get(dongCode);
    }
}
