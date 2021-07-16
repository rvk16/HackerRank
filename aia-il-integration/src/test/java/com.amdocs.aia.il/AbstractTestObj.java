package com.amdocs.aia.il;

import com.amdocs.aia.common.core.test.utils.OpenShiftUtils;
import com.amdocs.aia.common.core.test.utils.PropertiesUtils;
import com.amdocs.aia.common.core.test.utils.TestContext;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Pod;
import io.restassured.RestAssured;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.amdocs.aia.il.utils.AssertUtils.repetitive;
import static com.amdocs.aia.il.utils.LogUtils.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Listeners(value = TestListener.class)
public abstract class AbstractTestObj {

    private static String branchName;
    private static String version;
    private static String commitId;
    protected static Path testRunPath;

    private final boolean isEnsureNamespace;

    protected OpenShiftUtils openShiftUtils;

    private static String testMode;

    public AbstractTestObj() {
        isEnsureNamespace = false;
    }

    public AbstractTestObj(final boolean isEnsureNamespace) {
        this.isEnsureNamespace = isEnsureNamespace;
    }

    @BeforeSuite(alwaysRun = true)
    public static void createTestResultsDir() {
        try {
            initGitDetails();
            if (runInJenkins()) {
                testRunPath = Paths.get("/root/tests/" + commitId);
            } else {
                testRunPath = Paths.get("./" + LocalDateTime.now().toString().replace(':', '-'));
                commitId = System.getProperty("user.name").toLowerCase();
            }
            try {
                Files.createDirectories(testRunPath);
            } catch (final IOException e) {
                // ignore
            }
        } catch (final RuntimeException e){
            // ignore Runtime Exception
        }
    }

    private static void initGitDetails() {
        Properties gitProps = PropertiesUtils.loadGitProperties("git.properties");
        version = gitProps.getProperty("git.build.version");
        branchName = gitProps.getProperty("git.branch");
        commitId = gitProps.getProperty("git.commit.id.abbrev");
    }

    protected static boolean runInJenkins() {
        return System.getenv("JENKINS_NAME") != null;
    }


    @BeforeClass(alwaysRun = true)
    public void prepare() {
        final String testPropertiesFilename = System.getProperty("aia.test.properties", "test.properties");
        testMode = System.getProperty("aia.test.mode", "local");
        final TestContext testContext = PropertiesUtils.loadProperties("k8s/" + testPropertiesFilename);
        testContext.setBranchName(branchName);
        log("Working on branch: "+branchName);
        testContext.setVersion(version);
        testContext.setEnsureNamespace(isEnsureNamespace);
        String namespace = testContext.getNamespace() != null ? testContext.getNamespace()
                : this.getClass().getSimpleName().toLowerCase() + '-' + commitId;
        if (testMode.equals("remote")) {
            String suffix = branchName.length() > 7 ? branchName.substring(0, 7) : branchName;
            namespace = namespace + "-" + suffix;

        }
        log("Working with namespace : "+namespace);
        openShiftUtils = new OpenShiftUtils(testContext, namespace);
        RestAssured.useRelaxedHTTPSValidation("TLSv1.2");
    }

//    @AfterMethod(alwaysRun = false)
//    public void afterMethod(final Method m, final ITestResult tr) {
//        if (tr.getTestClass() == null) {
//            // this happens when test initialization fails
//            return;
//        }
//        if (testRunPath != null && testMode.equals("local")) {
//            try {
//                final Path testMethodPath = Paths.get(testRunPath.toString() + '/' + tr.getTestClass().getName() + '.' + m.getName());
//                Files.createDirectories(testMethodPath);
//                logTest(testMethodPath, m.getName());
//                logPods(testMethodPath);
//            } catch (final Exception e) {
//                log("Failed to report logs");
//            }
//        }
//    }

    protected void logTest(final Path path, final String methodName) {
        try {
            final Path testLogPath = Paths.get(path.toString() + '/' + methodName + ".log");
            Files.write(Paths.get(testLogPath.toUri()), getOutput().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reset();
        }
    }

    protected void logPods(final Path path) {
        openShiftUtils.getClient()
                .pods()
                .inNamespace(openShiftUtils.getNamespace())
                .list()
                .getItems().forEach(p -> logPod(p, path));
    }

    protected void logPod(Pod p, Path path) {
        try {
            String testPath = path.toString();
            final List<Container> containers = p.getSpec().getContainers();
            for (final Container container : containers) {
                final String containerName = container.getName();
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                        testPath + '/' + p.getMetadata().getName() + '-' + containerName + ".log"),
                        StandardCharsets.UTF_8))) {
                    writer.write(openShiftUtils.getClient().pods().inNamespace(openShiftUtils.getNamespace())
                            .withName(p.getMetadata().getName()).inContainer(containerName).getLog(true));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateStringFromResource(String path) throws IOException {
        File json = new File(path);
        return new String(Files.readAllBytes(Paths.get(json.getAbsolutePath())));
    }

    public void repetitiveAssertDeploymentsWereDeleted(Map<String, String> deployments, String msg,
                                                       long timeout, TimeUnit timeUnit) {
        repetitive(() -> assertTrue(deployments.isEmpty()), timeUnit.toMillis(timeout));
        assertTrue(deployments.isEmpty(), msg);
    }

    public void repetitiveAssertDeploymentsAreRunning(Map<String, String> deployments,
                                                      int expectedDeploymentsNum, String msg,
                                                      long timeout, TimeUnit timeUnit) {
        repetitive(() -> {
            long counter = deployments.values().stream().filter(v -> v.equals("Deployment")).count();
            assertEquals(counter, expectedDeploymentsNum);
        }, timeUnit.toMillis(timeout));
        assertEquals(deployments.values().stream().filter(v -> v.equals("Deployment")).count(),
                expectedDeploymentsNum, msg);
    }
}