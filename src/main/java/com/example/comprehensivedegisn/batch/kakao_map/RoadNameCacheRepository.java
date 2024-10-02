package com.example.comprehensivedegisn.batch.kakao_map;


import com.example.comprehensivedegisn.batch.kakao_map.dto.RoadNameLocationRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class RoadNameCacheRepository {

    private final Map<String, RoadNameLocationRecord> roadNameCache = new ConcurrentHashMap<>();

    public void save(String roadName, RoadNameLocationRecord apartmentGeoRecord) {

        if(roadName == null) return;
        roadNameCache.put(roadName, apartmentGeoRecord);
    }

    public Optional<RoadNameLocationRecord> findByRoadName(String roadName) {
        return roadName != null && roadNameCache.containsKey(roadName) ? Optional.of(roadNameCache.get(roadName)) : Optional.empty();
    }
}
