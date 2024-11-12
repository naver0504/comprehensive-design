package com.example.comprehensivedegisn.api;

import com.example.comprehensivedegisn.batch.open_api.dto.ApartmentDetailResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
                "<response>\n" +
                        "<header>\n" +
                        "<resultCode>00</resultCode>\n" +
                        "<resultMsg>NORMAL SERVICE.</resultMsg>\n" +
                        "</header>\n" +
                        "<body>\n" +
                        "<items>\n" +
                        "<item>\n" +
                        "<거래금액> 23,000</거래금액>\n" +
                        "<거래유형>직거래</거래유형>\n" +
                        "<건축년도>2002</건축년도>\n" +
                        "<년>2024</년>\n" +
                        "<도로명>낙산5길</도로명>\n" +
                        "<도로명건물본번호코드>00022</도로명건물본번호코드>\n" +
                        "<도로명건물부번호코드>00000</도로명건물부번호코드>\n" +
                        "<도로명시군구코드>11110</도로명시군구코드>\n" +
                        "<도로명일련번호코드>01</도로명일련번호코드>\n" +
                        "<도로명지상지하코드>0</도로명지상지하코드>\n" +
                        "<도로명코드>4100019</도로명코드>\n" +
                        "<동> </동>\n" +
                        "<등기일자> </등기일자>\n" +
                        "<매도자>개인</매도자>\n" +
                        "<매수자>개인</매수자>\n" +
                        "<법정동> 창신동</법정동>\n" +
                        "<법정동본번코드>0023</법정동본번코드>\n" +
                        "<법정동부번코드>0326</법정동부번코드>\n" +
                        "<법정동시군구코드>11110</법정동시군구코드>\n" +
                        "<법정동읍면동코드>17400</법정동읍면동코드>\n" +
                        "<법정동지번코드>1</법정동지번코드>\n" +
                        "<아파트>그린</아파트>\n" +
                        "<월>6</월>\n" +
                        "<일>4</일>\n" +
                        "<일련번호>11110-41</일련번호>\n" +
                        "<전용면적>45.27</전용면적>\n" +
                        "<중개사소재지> </중개사소재지>\n" +
                        "<지번>23-326</지번>\n" +
                        "<지역코드>11110</지역코드>\n" +
                        "<층>3</층>\n" +
                        "<해제사유발생일> </해제사유발생일>\n" +
                        "<해제여부> </해제여부>\n" +
                        "</item>\n" +
                        "<item>\n" +
                        "<거래금액> 85,000</거래금액>\n" +
                        "<거래유형>중개거래</거래유형>\n" +
                        "<건축년도>2008</건축년도>\n" +
                        "<년>2024</년>\n" +
                        "<도로명>동망산길</도로명>\n" +
                        "<도로명건물본번호코드>00047</도로명건물본번호코드>\n" +
                        "<도로명건물부번호코드>00000</도로명건물부번호코드>\n" +
                        "<도로명시군구코드>11110</도로명시군구코드>\n" +
                        "<도로명일련번호코드>02</도로명일련번호코드>\n" +
                        "<도로명지상지하코드>0</도로명지상지하코드>\n" +
                        "<도로명코드>4100065</도로명코드>\n" +
                        "<동> </동>\n" +
                        "<등기일자> </등기일자>\n" +
                        "<매도자>개인</매도자>\n" +
                        "<매수자>개인</매수자>\n" +
                        "<법정동> 숭인동</법정동>\n" +
                        "<법정동본번코드>0002</법정동본번코드>\n" +
                        "<법정동부번코드>0001</법정동부번코드>\n" +
                        "<법정동시군구코드>11110</법정동시군구코드>\n" +
                        "<법정동읍면동코드>17500</법정동읍면동코드>\n" +
                        "<법정동지번코드>1</법정동지번코드>\n" +
                        "<아파트>종로센트레빌</아파트>\n" +
                        "<월>6</월>\n" +
                        "<일>1</일>\n" +
                        "<일련번호>11110-2224</일련번호>\n" +
                        "<전용면적>59.92</전용면적>\n" +
                        "<중개사소재지>서울 종로구</중개사소재지>\n" +
                        "<지번>2-1</지번>\n" +
                        "<지역코드>11110</지역코드>\n" +
                        "<층>7</층>\n" +
                        "<해제사유발생일> </해제사유발생일>\n" +
                        "<해제여부> </해제여부>\n" +
                        "</item>\n" +
                        "<item>\n" +
                        "<거래금액> 222,500</거래금액>\n" +
                        "<거래유형>중개거래</거래유형>\n" +
                        "<건축년도>2017</건축년도>\n" +
                        "<년>2024</년>\n" +
                        "<도로명>송월길</도로명>\n" +
                        "<도로명건물본번호코드>00099</도로명건물본번호코드>\n" +
                        "<도로명건물부번호코드>00000</도로명건물부번호코드>\n" +
                        "<도로명시군구코드>11110</도로명시군구코드>\n" +
                        "<도로명일련번호코드>01</도로명일련번호코드>\n" +
                        "<도로명코드>4100192</도로명코드>\n" +
                        "<동> </동>\n" +
                        "<등기일자> </등기일자>\n" +
                        "<매도자>개인</매도자>\n" +
                        "<매수자>개인</매수자>\n" +
                        "<법정동> 홍파동</법정동>\n" +
                        "<법정동본번코드>0199</법정동본번코드>\n" +
                        "<법정동부번코드>0000</법정동부번코드>\n" +
                        "<법정동시군구코드>11110</법정동시군구코드>\n" +
                        "<법정동읍면동코드>17900</법정동읍면동코드>\n" +
                        "<법정동지번코드>1</법정동지번코드>\n" +
                        "<아파트>경희궁자이(2단지)</아파트>\n" +
                        "<월>6</월>\n" +
                        "<일>2</일>\n" +
                        "<일련번호>11110-2445</일련번호>\n" +
                        "<전용면적>84.614</전용면적>\n" +
                        "<중개사소재지>서울 서대문구, 서울 종로구</중개사소재지>\n" +
                        "<지번>199</지번>\n" +
                        "<지역코드>11110</지역코드>\n" +
                        "<층>12</층>\n" +
                        "<해제사유발생일> </해제사유발생일>\n" +
                        "<해제여부> </해제여부>\n" +
                        "</item>\n" +
                        "</items>\n" +
                        "<numOfRows>3</numOfRows>\n" +
                        "<pageNo>1</pageNo>\n" +
                        "<totalCount>3</totalCount>\n" +
                        "</body>\n" +
                        "</response>";

        XmlMapper xmlMapper = new XmlMapper();
        ApartmentDetailRootElement apartmentDetailRootElement = xmlMapper.readValue(xml, ApartmentDetailRootElement.class);
        System.out.println(apartmentDetailRootElement);
    }
}