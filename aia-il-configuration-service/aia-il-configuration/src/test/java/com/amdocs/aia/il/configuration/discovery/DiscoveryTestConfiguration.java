package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.service.external.SerializationIDAssigner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = { ExternalModelDiscoverer.class })
@Import({SerializationIDAssigner.class, MessageHelper.class})
public class DiscoveryTestConfiguration {
}
