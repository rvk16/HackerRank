package com.amdocs.aia.il.integration;

import com.amdocs.aia.common.model.repo.*;
import com.amdocs.aia.il.AbstractTestObj;
import com.amdocs.aia.il.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.*;
import static com.amdocs.aia.il.utils.LogUtils.log;

public abstract class BaseIntegrationTest extends AbstractTestObj {
    protected static final String PROJECT_KEY = "aia";

    public Properties prop;
    public String aiaRepoURL;
    public String sharedConfigurationURL;
    public String configurationServiceUrl;
    public String notificationsServiceUrl;

    private static final String APP = "app";
    private static final String AIA_REPO_URL = "%s://%s/aia/api/v1/repo/";
    private static final String AIA_SHARED_SERVICES_URL = "%s://%s/aia/api/v1/shrd";

    /*Define IL config URL based on the definition provided in swagger*/
    private static final String IL_CONFIGRATION_URL = "%s://%s/aia/api/v1/integration-layer";
    public static final String configServiceAdditionalUrl = "/projects/" + PROJECT_KEY+"/configuration";
    private static final String NOTIFICATIONS_URL = "%s://%s/aia/api/v1/notifications/projects/aia/notifications";

    private static final String REPO_SERVICES_APP_LABEL = "REPO_SERVICES_APP_LABEL";
    private static final String SHARED_SERVICES_APP_LABEL = "SHARED_SERVICES_APP_LABEL";
    private static final String IL_CONFIGURATION_APP_LABEL = "IL_CONFIGURATION_APP_LABEL";
    private static final String NOTIFICATIONS_APP_LABEL = "NOTIFICATIONS_APP_LABEL";


    private boolean apigwEnabled;
    private String apigwRoute;
    private String httpProtocol;

    @BeforeClass(alwaysRun = true)
    public void setUpIntegrationTest() {
        readPropertiesFile();
        aiaRepoURL = getUrl(REPO_SERVICES_APP_LABEL, AIA_REPO_URL, getPortByName(prop.getProperty(REPO_SERVICES_APP_LABEL), "reposervices"));
        sharedConfigurationURL = getUrl(SHARED_SERVICES_APP_LABEL, AIA_SHARED_SERVICES_URL,getPortByName(prop.getProperty(SHARED_SERVICES_APP_LABEL), "home"));
        configurationServiceUrl = getUrl(IL_CONFIGURATION_APP_LABEL, IL_CONFIGRATION_URL, getPortByName(prop.getProperty(IL_CONFIGURATION_APP_LABEL), "config-port"));
        notificationsServiceUrl = getUrl(NOTIFICATIONS_APP_LABEL, NOTIFICATIONS_URL, getPortByName(prop.getProperty(NOTIFICATIONS_APP_LABEL), "config-port"));
        loadInitialConfiguration();
    }

    protected  File getConfigurationZip(){
        return  new File("src/test/resources/data/aia_initial_data.zip");
    }

    private void loadInitialConfiguration() {
        Integer crId = createChangeRequest("Load initial configuration");

        final String url = aiaRepoURL + "elements/import?projectKey=aia&fullImport=true";
        File configurationZip = getConfigurationZip();

        Response response = assertGetResponsePostWithFileAndContentType(url, HttpStatus.SC_OK, configurationZip, "application/zip" );

        // TODO the response is in wrong project
        //ImportElementsResponse importElementsResponse = response.as(ImportElementsResponse.class);

        UserRepositoryStatusDTO userStatus = assertGetResponseGet( aiaRepoURL + "repository/user/status", HttpStatus.SC_OK).as(UserRepositoryStatusDTO.class);
        if (userStatus.isHasLocalChanges()) {
            commitChangeRequest();
        } else {
            deleteChangeRequest(crId);
        }
    }

    protected Integer createChangeRequest(final String title) {
        final String crName = "Platform Sanity " + UUID.randomUUID().toString() + " - " + title;
        final ChangeRequestDTO cr = new ChangeRequestDTO();
        cr.setCrName(crName);
        cr.setProjectName("aia");
        cr.setWorkStream("Main");
        final String url = aiaRepoURL + "repository/CRs";
        final String json;
        try {
            json = new ObjectMapper().writeValueAsString(cr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log("About to create change request");
        log("Request URL: " + url);
        log("Request Body:");
        log(json);
        setOpenCrId(null);
        final Response response = assertGetResponsePost(url, HttpStatus.SC_CREATED, cr);
        final Integer changeRequestId = response.as(ChangeRequestDTO.class).getCrId();
        setOpenCrId(changeRequestId);
        log("Created change request " + changeRequestId + " (" + crName + ')');
        return changeRequestId;
    }

    protected void commitChangeRequest() {
        final String url = aiaRepoURL + "releases";
        log("About to publish change request");
        ReleaseRequestDTO releaseRequest = new ReleaseRequestDTO();
        releaseRequest.setShouldBuildProject(false);
        releaseRequest.setShouldUpdateInstallation(false);
        releaseRequest.setIgnoreWarnings(true);
        final Response response = assertGetResponsePost(url, HttpStatus.SC_OK, releaseRequest);
        ReleaseResponseDTO releaseResponse = response.as(ReleaseResponseDTO.class);
        setOpenCrId(null);
        log("Published change request status "+ releaseResponse.getState());
    }

    protected ValidationResponseDTO validateChangeRequest(int crId) {

        final String url = aiaRepoURL + "repository/CRs/" + crId+"/validate";
        final Response response = assertGetResponsePostWithoutContent(url,  HttpStatus.SC_OK,"aiauser");
        return  response.as(ValidationResponseDTO.class);
    }

    protected void deleteChangeRequest(Integer crId) {
        final String url = aiaRepoURL + "repository/CRs/" + crId;
        log("About to delete change request" + crId);
        assertGetResponseDelete(url, 200);
        setOpenCrId(null);
        log("Deleted change request status "+ crId);
    }

    private void readPropertiesFile() {
        prop = new Properties();
        final String envPropertiesFilename = System.getProperty("aia.env.properties", "env.properties");
        final File fileFromResourceDir = FileUtils.getFileFromResourceDir(envPropertiesFilename);
        try {
            prop.load(new FileInputStream(fileFromResourceDir));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        httpProtocol = Boolean.parseBoolean(prop.getProperty("SERVICE_TLS")) ? "https" : "http";
    }

    public String getUrl(final String label, final String urlTemplate, final int port) {
        final String baseUrl;
        if (apigwEnabled) {
            baseUrl = apigwRoute;
        } else {
            final String host = openShiftUtils.getPodHostByLabel(APP, prop.getProperty(label));
            baseUrl = new StringBuilder(host).append(':').append(port).toString();
        }
        final String url = String.format(urlTemplate, httpProtocol, baseUrl);
        log("Initializing " + label + " service url to: " + url);
        return url;
    }

    public Integer getPortByName(final String serviceName, final String portName) {
        for (final ServicePort port : openShiftUtils.getClient().services().inNamespace(openShiftUtils.getNamespace()).withName(serviceName).get().getSpec().getPorts()) {
            if (portName.equals(port.getName())) {
                return port.getNodePort();
            }
        }
        return null;
    }
}