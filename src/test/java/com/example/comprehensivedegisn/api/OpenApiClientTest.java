package com.example.comprehensivedegisn.api;

import com.example.comprehensivedegisn.api.dto.ApartmentDetailRootElement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class OpenApiClientTest {

    @Autowired
    private OpenApiClient openApiClient;
    @Test
    void localDateTimeTest() throws Exception {

        LocalDate localDate = LocalDate.now().minusMonths(3);
        openApiClient.request(1, localDate);
    }

    @Test
    void xmlMapperTest() throws JsonProcessingException {
        String xml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response>\n" +
                "<header>\n" +
                "<resultCode>00</resultCode>\n" +
                "<resultMsg>NORMAL SERVICE.</resultMsg>\n" +
                "</header>\n" +
                "<body>\n" +
                "<item>\n" +
                "<거래금액>82,500</거래금액>\n" +
                "<건축년도>2008</건축년도>\n" +
                "<년>2015</년>\n" +
                "<도로명>사직로8길</도로명>\n" +
                "<도로명건물본번호코드>00004</도로명건물본번호코드>\n" +
                "<도로명건물부번호코드>00000</도로명건물부번호코드>\n" +
                "<도로명시군구코드>11110</도로명시군구코드>\n" +
                "<도로명일련번호코드>03</도로명일련번호코드>\n" +
                "<도로명지상지하코드>0</도로명지상지하코드>\n" +
                "<도로명코드>4100135</도로명코드>\n" +
                "<법정동>사직동</법정동>\n" +
                "<법정동본번코드>0009</법정동본번코드>\n" +
                "<법정동부번코드>0000</법정동부번코드>\n" +
                "<법정동시군구코드>11110</법정동시군구코드>\n" +
                "<법정동읍면동코드>11500</법정동읍면동코드>\n" +
                "<법정동지번코드>1</법정동지번코드>\n" +
                "<아파트>광화문풍림스페이스본(9-0)</아파트>\n" +
                "<월>12</월>\n" +
                "<일>1</일>\n" +
                "<전용면적>94.51</전용면적>\n" +
                "<지번>9</지번>\n" +
                "<지역코드>11110</지역코드>\n" +
                "<층>11</층>\n" +
                "<해제여부>O</해제여부>\n" +
                "<해제사유발생일>23.01.31</해제사유발생일>\n" +
                "<거래유형>중개거래</거래유형>\n" +
                "<중개사소재지>서울 서초구</중개사소재지>\n" +
                "<등기일자>23.01.31</등기일자>\n" +
                "<매도자>개인</매도자>\n" +
                "<매수자>개인</매수자>\n" +
                "<동>101</동>\n" +
                "</item>\n" +
                "</body>\n" +
                "</response>";

        XmlMapper xmlMapper = new XmlMapper();
        ApartmentDetailRootElement apartmentDetailRootElement = xmlMapper.readValue(xml, ApartmentDetailRootElement.class);
        System.out.println(apartmentDetailRootElement);
    }
}