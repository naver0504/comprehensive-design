package com.example.comprehensivedegisn.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDate;

@SpringBootTest
class OpenApiUtilsTest {

    @Autowired
    private OpenApiUtils openApiUtils;

    @Test
    void localDateTimeTest() throws MalformedURLException {
        OpenApiRequest openApiRequest = OpenApiRequest.builder()
                .pageNo(1)
                .contractDate(LocalDate.now())
                .build();

        URI uri = openApiUtils.createURI(openApiRequest);
        System.out.println(uri);
    }

}