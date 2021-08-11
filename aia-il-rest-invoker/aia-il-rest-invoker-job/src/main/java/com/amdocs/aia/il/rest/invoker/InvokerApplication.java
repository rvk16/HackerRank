package com.amdocs.aia.il.rest.invoker;

import com.amdocs.aia.il.rest.invoker.configuration.InvokerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication(scanBasePackages = "com.amdocs.aia.il.rest.invoker")
public class InvokerApplication implements CommandLineRunner, ExitCodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvokerApplication.class);

    private final Invoker invoker;

    @Autowired
    public InvokerApplication(final Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void run(final String... args) {
        InvokerResponse invokerResponse = new InvokerResponse();

        try {
            invokerResponse = invoker.run();
        } catch (final Exception e) {
            System.err.println(e.getMessage()); // NOSONAR
            invokerResponse.setSuccess(false);
            invokerResponse.setMsg("Exception");

        } finally {
           if(invokerResponse.isSuccess()){
               LOGGER.info("Invoker job ended successfully");
               invoker.close();

           }
           else
           {
               LOGGER.info("Invoker job failed, additional info:" + invokerResponse.getMsg());
               //todo check how to close the job in case job failed
               invoker.close();
           }
        }
    }

    @Override
    public int getExitCode() {
        return invoker.getExitCode();
    }


    public static void main(final String[] args) {
        try (final ConfigurableApplicationContext context = new SpringApplicationBuilder(InvokerApplication.class)
                .web(WebApplicationType.NONE).run(args)) {

            final int exitCode = SpringApplication.exit(context);
            System.exit(exitCode);
        }
    }

}