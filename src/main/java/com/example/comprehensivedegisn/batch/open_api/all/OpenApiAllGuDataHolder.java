package com.example.comprehensivedegisn.batch.open_api.all;

import com.example.comprehensivedegisn.adapter.domain.DongEntity;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.repository.dong.DongRepository;
import com.example.comprehensivedegisn.batch.open_api.DataHolder;
import com.example.comprehensivedegisn.batch.open_api.dto.GuDong;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OpenApiAllGuDataHolder implements DataHolder<GuDong> {

    private Map<Gu, Map<String, Integer>> guDongMap;
    private final DongRepository dongRepository;

    @PostConstruct
    @Override
    public void init() {
        guDongMap = dongRepository.findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                DongEntity::getGu,
                                Collectors.toMap(
                                        DongEntity::getDongName,
                                        DongEntity::getId
                                )
                        )
                );
    }

    @Override
    public Integer getDongEntityId(GuDong guDong) {
        return guDongMap.get(guDong.gu()).get(guDong.dongName());
    }
}
