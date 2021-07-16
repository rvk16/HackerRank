package com.amdocs.aia.il.sqlite.api.unit;

import com.amdocs.aia.il.sqlite.config.AutoConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AutoConfiguration.class})
@AutoConfigureWebClient
public abstract class AbstractApiControllerUnitTest {

}