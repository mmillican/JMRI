package jmri.jmrit.railops.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jmri.Disposable;
import jmri.InstanceManager;
import jmri.InstanceManagerAutoDefault;
import jmri.InstanceManagerAutoInitialize;
import jmri.jmrit.railops.models.roster.BulkUpsertRosterResponse;
import jmri.jmrit.railops.config.Auth;
import jmri.jmrit.railops.config.RailOpsXml;
import jmri.jmrit.railops.models.CarModel;
import jmri.jmrit.railops.models.LocomotiveModel;
import jmri.jmrit.railops.models.ModelCollection;
import jmri.jmrit.railops.models.roster.BulkUpsertRosterRequest;
import jmri.jmrit.railops.models.roster.UpsertCompareMode;
import jmri.jmrit.railops.models.roster.UpsertLocomotiveModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RosterSyncService implements InstanceManagerAutoDefault, InstanceManagerAutoInitialize, Disposable {
    private static String _apiBaseUrl = "http://localhost:5007/";

    public RosterSyncService() {
    }

    @Override
    public void initialize() {
        InstanceManager.getDefault(RailOpsXml.class);
    }
    public static RosterSyncService getDefault() {
        return InstanceManager.getDefault(RosterSyncService.class);
    }

    public List<ModelCollection> getCollections() throws URISyntaxException, IOException {
        URL url = new URI("http://localhost:5007/collections/mine").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("ApiKey", Auth.getApiKey());
        InputStream responseStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();

        List<ModelCollection> collections = mapper.readValue(responseStream, new TypeReference<>() {});

        log.debug("Retrieved {} collections", collections.size());

        return collections;
    }

    public List<LocomotiveModel> getLocomotives(int collectionId) throws Exception {
        String url = "locomotives?collectionId=%s".formatted(collectionId);
        List<LocomotiveModel> locomotives = GetList(url, LocomotiveModel.class);

        log.info("Retrieved {} locomotives from remote roster", locomotives.size());
        return locomotives;
    }

    public jmri.jmrit.railops.models.roster.BulkUpsertRosterResponse upsertLocomotives(int collectionId, List<UpsertLocomotiveModel> locomotives) throws Exception {
        String absoluteUrl = "%s%s".formatted(_apiBaseUrl, "locomotives/bulk");

        BulkUpsertRosterRequest<UpsertLocomotiveModel> requestModel = new BulkUpsertRosterRequest<>(
                collectionId,
                locomotives,
                UpsertCompareMode.ReportingMarksAndNumber
        );

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(requestModel);

        log.info("... request body :: {}", requestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(absoluteUrl))
                .setHeader("ApiKey", Auth.getApiKey())
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("... POST locomotive response: {} -- {}", httpResponse.statusCode(), httpResponse.body());

        jmri.jmrit.railops.models.roster.BulkUpsertRosterResponse response = mapper.readValue(httpResponse.body(), BulkUpsertRosterResponse.class);
        return response;
    }

    public LocomotiveModel createLocomotive(int collectionId, LocomotiveModel locomotiveModel) throws Exception {
        String url = "collections/%s/locomotives".formatted(collectionId);
        String absoluteUrl = "%s%s".formatted(_apiBaseUrl, url);

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(locomotiveModel);

        log.debug("... request body :: {}", requestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(absoluteUrl))
                .setHeader("ApiKey", Auth.getApiKey())
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        log.debug("... POST locomotive response: {} -- {}", httpResponse.statusCode(), httpResponse.body());

        LocomotiveModel response = mapper.readValue(httpResponse.body(), LocomotiveModel.class);
        return response;
    }

    public List<CarModel> getCars(int collectionId) throws Exception {
        String url = "cars?collectionId=%s".formatted(collectionId);
        List<CarModel> cars = GetList(url, CarModel.class);

        log.info("Retrieved {} cars from remote roster", cars.size());
        return cars;
    }

    public CarModel createCar(int collectionId, CarModel carModel) throws Exception {
        String url = "collections/%s/cars".formatted(collectionId);
        String absoluteUrl = "%s%s".formatted(_apiBaseUrl, url);

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(carModel);

        log.debug("... request body :: {}", requestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(absoluteUrl))
                .setHeader("ApiKey", Auth.getApiKey())
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        log.debug("... POST car response: {} -- {}", httpResponse.statusCode(), httpResponse.body());

        CarModel response = mapper.readValue(httpResponse.body(), CarModel.class);
        return response;
    }

    private <TResponseType> List<TResponseType> GetList(String url, Class<TResponseType> responseTypeClass) throws Exception {
        log.info("Submitting GET request to '{}' (API Key: {})", url, Auth.getApiKey());

        String absoluteUrl = "%s%s".formatted(_apiBaseUrl, url);

        ObjectMapper mapper = new ObjectMapper();

        log.info("requesting locos from '{}' (api key: {})", absoluteUrl, Auth.getApiKey());
        URL requestUrl = new URI(absoluteUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("ApiKey", Auth.getApiKey());
        connection.setRequestMethod("GET");
        InputStream responseStream = connection.getInputStream();

//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(absoluteUrl))
//                .setHeader("ApiKey", Auth.getApiKey())
//                .setHeader("Accept", "application/json")
//                .GET()
//                .build();
//
//        log.info("... HTTP Request >>> {}", request);
//
//        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        log.info("... response {}: {}", httpResponse.statusCode(), httpResponse.body());

        // TODO: Need to figure out how to make the response handling / deserialization dynamic per type
        // TODO: After the above, see if we can go back to the 'other' method of making the requests

        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, responseTypeClass);
//        TResponseType response = mapper.readValue(responseStream., new TypeReference<>() {});
        List<TResponseType> response = mapper.readValue(responseStream, collectionType);
        return response;
    }

    private <TRequestType, TResponseType> TResponseType Post(String url, TRequestType requestModel) throws Exception {
        log.info("Submitting POST request to '{}' (API Key: {})", url, Auth.getApiKey());

        String absoluteUrl = "%s%s".formatted(_apiBaseUrl, url);

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(requestModel);

        log.info("... request body :: {}", requestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(absoluteUrl))
                .setHeader("ApiKey", Auth.getApiKey())
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("POST request response: {} -- {}", httpResponse.statusCode(), httpResponse.body());
        TypeReference<TResponseType> responseTypeRef = new TypeReference<TResponseType>() { };
        TResponseType response = mapper.readValue(httpResponse.body(), responseTypeRef);
        return response;
//        TResponseType response = mapper.readValue(httpResponse.body(), new TypeReference<>() {});
//        return response;
    }

//    private static <TResponseType> TResponseType MakeRequest(String url, String method, Object body) throws Exception {
//        URL requestUrl = new URI(_apiBaseUrl + "/" + url).toURL();
//
//        log.info("Submitting {} request to '{}'", method, url);
//
//        HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
//        conn.setRequestProperty("Accept", "application/json");
//        conn.setRequestProperty("ApiKey", Auth.getApiKey()); // TODO: Make sure the auth key is loaded
//        conn.setRequestMethod(method);
//
//    }

    private static final Logger log = LoggerFactory.getLogger(RosterSyncService.class);

    @Override
    public void dispose() {

    }
}
