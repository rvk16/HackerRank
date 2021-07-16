package com.amdocs.aia.il.deployer;

import com.amdocs.aia.il.deployer.database.ConnectionManager;
import com.amdocs.aia.il.deployer.utils.ExecutionReport;
import com.amdocs.aia.il.deployer.utils.TestLevelProperty;
import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {ILDeployerComponentTest.class})
@ContextConfiguration(initializers = ILDeployerComponentTest.PropertyOverrideContextInitializer.class)
@ComponentScan(basePackages = {"com.amdocs.aia.il.deployer", "com.amdocs.aia.repo.client"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CommandLineRunner.class))
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(
        properties = {
                "aia.repo.elements.local.path=classpath:export_ref_flag.zip",
                "aia.il.deployer.db.localDatacenter=datacenter1"
        }
)
 class ILDeployerComponentTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ILDeployerComponentTest.class);

    @Autowired
    private IntegrationLayerDeployer integrationLayerDeployer;

    @Autowired
    private ExecutionReport executionReport;

    @Autowired
    private SourceTableDeployerProvider tableDeployerProvider;
    @Autowired
    private ConnectionManager connectionManager;
    private CqlSession session;

    private static List<Method> configurableTestMethods;
    private static Method nextTestMethod;

    /**
     * In general, spring test does not allow us to configure the application context per-test method
     * (although it DOES create a different application context per method,especially since we're using
     * the @DirtiesContext annotation for our methods) so, we keep track of the executed methods, and we
     * implement ApplicationContextInitializer (see class PropertyOverrideContextInitializer) in which we
     * initialize the application context before each test.
     * <p>
     * NOTE that it is important that we annotate EVERY method in this class with the 'Order' annotation so that we'll know the execution order
     */

    @BeforeAll
    static void setupTestClass() {
        configurableTestMethods = Arrays.stream(ILDeployerComponentTest.class.getMethods())
                .filter(method -> method.isAnnotationPresent(Test.class))
                .filter(method -> !method.isAnnotationPresent(Disabled.class))
                .filter(method -> method.isAnnotationPresent(Order.class))
                .sorted(Comparator.comparing(method -> method.getAnnotation(Order.class).value()))
                .collect(Collectors.toList());
        LOGGER.info("List of configurable test methods: {}", configurableTestMethods);
        nextTestMethod = configurableTestMethods.isEmpty() ? null : configurableTestMethods.get(0);
    }

    @BeforeEach
    public void setUp() {
        session = connectionManager.getSession();
    }

    @Test
    @Order(1)
    @DirtiesContext
    void addConfiguration() {
        integrationLayerDeployer.execute();
        assertEquals(22, executionReport.getTotalTableCount());
        assertEquals(0 , executionReport.getFailedTables().size());
    }


    @Test
    @Order(2)
    @DirtiesContext
    void checkSchemaExists() {
        integrationLayerDeployer.execute();
        assertEquals(22, executionReport.getTotalTableCount());
        assertEquals(0, executionReport.getFailedTables().size());
        SourceTableDeployer tableDeployer = tableDeployerProvider.getTableDeployer("BCMAPP");
        assertTrue(tableDeployer.exists("BCMAPP"));
        assertTrue(tableDeployer.exists("BCMAPP_Relation"));
    }

    public static class PropertyOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            if (nextTestMethod != null) {
                LOGGER.info("Initializing test-level application context properties for test method {}", nextTestMethod.getName());
                TestLevelProperty[] properties = nextTestMethod.getAnnotationsByType(TestLevelProperty.class);
                if (properties.length > 0) {
                    for (TestLevelProperty property : properties) {
                        LOGGER.info("Setting property {}={}", property.key(), property.value());
                        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, property.key() + "=" + property.value());
                    }
                } else {
                    LOGGER.info("No test-level application context properties set for test method {}", nextTestMethod.getName());
                }
            }
        }
    }

    /**
     * We need these assertion method since some databases change the case of identifiers
     */
    private static void assertEqualsIgnoreCase(String expected, String actual) {
        assertEquals(toLower(expected), toLower(actual));
    }

    private static void assertEqualsIgnoreCase(List<String> expected, List<String> actual) {
        assertEquals(toLower(expected), toLower(actual));
    }

    private static Object toLower(String s) {
        return s != null ? s.toLowerCase() : null;
    }

    private static List<String> toLower(List<String> strings) {
        return strings.stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}