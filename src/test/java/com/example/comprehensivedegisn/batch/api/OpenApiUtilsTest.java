package com.example.comprehensivedegisn.batch.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDate;

//@SpringBootTest
class OpenApiUtilsTest {

//    @Autowired
//    private OpenApiUtils openApiUtils;

    @Test
    void localDateTimeTest() throws MalformedURLException {
        LocalDate localDate = LocalDate.of(2023, 1, 1);
        LocalDate localDate1 = localDate.minusMonths(1);
        System.out.println(localDate1);
    }

}