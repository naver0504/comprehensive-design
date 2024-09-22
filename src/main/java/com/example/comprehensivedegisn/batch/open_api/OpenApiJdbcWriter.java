package com.example.comprehensivedegisn.batch.open_api;


import com.example.comprehensivedegisn.batch.open_api.api.dto.ApartmentDetail;
import com.example.comprehensivedegisn.batch.open_api.api.dto.ApartmentDetailResponse;
import com.example.comprehensivedegisn.domain.DongEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class OpenApiJdbcWriter implements ItemWriter<ApartmentDetailResponse> {



    private final OpenApiDongDataHolder openApiDongDataHolder;
    private final JdbcTemplate jdbcTemplate;


    @Value("#{jobParameters[regionalCode]}")
    private String regionalCode;

    private final String INSERT_SQL =
            "INSERT INTO apartment_transaction (" +
                    "deal_amount, " +
                    "build_year, " +
                    "deal_year, " +
                    "road_name, " +
                    "road_name_seq, " +
                    "road_name_basement_code, " +
                    "road_name_code, " +
                    "dong, " +
                    "bonbun, " +
                    "bubun, " +
                    "land_code, " +
                    "apartment_name, " +
                    "deal_month, " +
                    "deal_day, " +
                    "area_for_exclusive_use, " +
                    "jibun, " +
                    "floor, " +
                    "registration_date, " +
                    "rdealer_law_dnm, " +
                    "dong_entity_id" +
                    ") " +
                    "VALUES " +
                    "( " +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? " +
                    ")";




    @Override
    public void write(Chunk<? extends ApartmentDetailResponse> chunk)  {
        openApiDongDataHolder.init();
        ApartmentDetailResponse apartmentDetailResponse = chunk.getItems().get(0);

        List<ApartmentDetail> items = apartmentDetailResponse.body().items();

        jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ApartmentDetail apartmentDetail = items.get(i);
                        ps.setString(1, apartmentDetail.dealAmount());
                        ps.setInt(2, apartmentDetail.buildYear());
                        ps.setInt(3, apartmentDetail.dealYear());
                        ps.setString(4, apartmentDetail.roadName());
                        ps.setInt(5, apartmentDetail.roadNameSeq());
                        ps.setInt(6, apartmentDetail.roadNameBasementCode());
                        ps.setInt(7, apartmentDetail.roadNameCode());
                        ps.setString(8, apartmentDetail.dong());
                        ps.setInt(9, apartmentDetail.bonbun());
                        ps.setInt(10, apartmentDetail.bubun());
                        ps.setInt(11, apartmentDetail.landCode());
                        ps.setString(12, apartmentDetail.apartmentName());
                        ps.setInt(13, apartmentDetail.dealMonth());
                        ps.setInt(14, apartmentDetail.dealDay());
                        ps.setDouble(15, apartmentDetail.areaForExclusiveUse());
                        ps.setString(16, apartmentDetail.jibun());
                        ps.setInt(17, apartmentDetail.floor());
                        ps.setString(18, apartmentDetail.registrationDate());
                        ps.setString(19, apartmentDetail.rdealerLawDnm());

                        DongEntity dongEntity = openApiDongDataHolder.getDongEntity(regionalCode, apartmentDetail.eupmyeondongCode());
                        ps.setLong(20, dongEntity.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return items.size();
                    }
                });

    }
}
