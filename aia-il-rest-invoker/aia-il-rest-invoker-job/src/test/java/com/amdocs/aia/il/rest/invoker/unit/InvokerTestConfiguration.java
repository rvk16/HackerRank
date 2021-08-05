package com.amdocs.aia.il.rest.invoker.unit;

import com.amdocs.aia.il.rest.invoker.Invoker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {Invoker.class})
public class InvokerTestConfiguration {
}
