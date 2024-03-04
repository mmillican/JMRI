package jmri.jmrit.railops.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jmri.InstanceManager;
import jmri.InstanceManagerAutoDefault;
import jmri.InstanceManagerAutoInitialize;
import jmri.jmrit.railops.config.ApiSettings;
import jmri.jmrit.railops.config.RailOpsXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RailOpsApiService implements InstanceManagerAutoDefault, InstanceManagerAutoInitialize {
    private final static String DEFAULT_CONTENT_TYPE = "application/json";

    private final ObjectMapper _mapper;
    private final HttpClient _httpClient;

    public RailOpsApiService() {
        _mapper = new ObjectMapper();
        _httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void initialize() {
        InstanceManager.getDefault(RailOpsXml.class); // load configuration
    }

    public static RailOpsApiService getDefault() {
        return InstanceManager.getDefault(RailOpsApiService.class);
    }

    public <TResponseType> TResponseType getSingle(String url, Class<TResponseType> responseTypeClass) throws Exception {
        HttpResponse<String> httpResponse = get(url);

        JavaType type = _mapper.getTypeFactory().constructType(responseTypeClass);
        return _mapper.readValue(httpResponse.body(), type);
    }

    public <TResponseType> List<TResponseType> getList(String url, Class<TResponseType> responseTypeClass) throws Exception {
        HttpResponse<String> httpResponse = get(url);

        CollectionType collectionType = _mapper.getTypeFactory().constructCollectionType(List.class, responseTypeClass);
        return _mapper.readValue(httpResponse.body(), collectionType);
    }

    private HttpResponse<String> get(String url) throws Exception {
        url = String.format("%s%s", ApiSettings.getApiUrl(), url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("ApiKey", ApiSettings.getApiKey())
                .setHeader("Accept", DEFAULT_CONTENT_TYPE)
                .setHeader("Content-Type", DEFAULT_CONTENT_TYPE)
                .GET()
                .build();

        return _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public <TRequestModel> HttpResponse<String> post(String url, TRequestModel requestModel) throws Exception {
        url = String.format("%s%s", ApiSettings.getApiUrl(), url);

        String requestBody = _mapper.writeValueAsString(requestModel);

        log.debug("POST request body: {}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .setHeader("ApiKey", ApiSettings.getApiKey())
                .setHeader("Accept", DEFAULT_CONTENT_TYPE)
                .setHeader("Content-Type", DEFAULT_CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // TODO: It would be ideal to de-serialize the response here before returning
        // But, having issues with generic types and the object mapper
        return _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static final Logger log = LoggerFactory.getLogger(RailOpsApiService.class);
}
