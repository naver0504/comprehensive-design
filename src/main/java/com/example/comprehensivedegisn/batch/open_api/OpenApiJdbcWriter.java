package com.example.comprehensivedegisn.batch.open_api;


import com.example.comprehensivedegisn.batch.open_api.dto.ApartmentDetail;
import com.example.comprehensivedegisn.batch.open_api.dto.ApartmentDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class OpenApiJdbcWriter implements ItemWriter<ApartmentDetailResponse> {

    private final OpenApiDongDataHolder openApiDongDataHolder;
    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_SQL =
            "INSERT INTO apartment_transaction (" +
                    "deal_amount, " +
                    "build_year, " +
                    "deal_year, " +
                    "road_name, " +
                    "road_name_bonbun, " +
                    "road_name_bubun, " +
                    "road_name_code, " +
                    "bonbun, " +
                    "bubun, " +
                    "apartment_name, " +
                    "deal_month, " +
                    "deal_day, " +
                    "area_for_exclusive_use, " +
                    "jibun, " +
                    "floor, " +
                    "dong_entity_id" +
                    "deal_date" +
                    ") " +
                    "VALUES " +
                    "( " +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? " +
                    ")";




    @Override
    public void write(Chunk<? extends ApartmentDetailResponse> chunk)  {
        ApartmentDetailResponse apartmentDetailResponse = chunk.getItems().get(0);

        List<ApartmentDetail> items = apartmentDetailResponse.body().items();

        jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ApartmentDetail apartmentDetail = items.get(i);
                ps.setString(1, apartmentDetail.dealAmount().trim());
                ps.setInt(2, apartmentDetail.buildYear());
                ps.setInt(3, apartmentDetail.dealYear());
                ps.setString(4, apartmentDetail.roadName() != null ? apartmentDetail.roadName().trim() : null);
                ps.setInt(5, apartmentDetail.roadNameBonbun());
                ps.setInt(6, apartmentDetail.roadNameBubun());
                ps.setInt(7, apartmentDetail.roadNameCode());
                ps.setInt(8, apartmentDetail.bonbun());
                ps.setInt(9, apartmentDetail.bubun());
                ps.setString(10, apartmentDetail.apartmentName().trim());
                ps.setInt(11, apartmentDetail.dealMonth());
                ps.setInt(12, apartmentDetail.dealDay());
                ps.setDouble(13, apartmentDetail.areaForExclusiveUse());
                ps.setString(14, apartmentDetail.jibun().trim());
                ps.setInt(15, apartmentDetail.floor());
                ps.setLong(16, openApiDongDataHolder.getDongEntityId(apartmentDetail.eupmyeondongCode().trim()));
                ps.setDate(17, java.sql.Date.valueOf(apartmentDetail.getDealDate()));
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }
}
