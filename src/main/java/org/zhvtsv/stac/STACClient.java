package org.zhvtsv.stac;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.json.JSONObject;
import org.zhvtsv.stac.dto.STACItemPreview;
import org.zhvtsv.stac.dto.STACObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@ApplicationScoped
public class STACClient {
    private static final Logger LOG = Logger.getLogger(STACClient.class);

    private final HttpClient httpClient;
    private final STACConfig stacConfig;

    public STACClient(STACConfig stacConfig) {
        this.httpClient = HttpClient.newBuilder().build();
        this.stacConfig = stacConfig;
    }

    public List<STACItemPreview> getStacItems(String boundingBox){
        String responseBody = makeGetRequest(stacConfig.url() + "?bbox="+boundingBox+"&datetime=2021-06-01T09:59:31.293Z/2023-06-01T09:59:31.293Z&eo:cloud_cover=10");
        return STACObjectMapper.getStacItemsPreview(responseBody);
    }
    public STACItemPreview getStacItemById(String id){
        System.out.println(stacConfig.url() + "/"+id);
        String responseBody = makeGetRequest(stacConfig.url() + "/"+id);
        System.out.println(responseBody);
        return STACObjectMapper.getStacItemPreview(new JSONObject(responseBody));
    }

    private String makeGetRequest(String url)  {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new ServerErrorException("Gateway http request failed with status code: " + response.statusCode(), Response.Status.BAD_GATEWAY);
            }
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("STAC Request failed", Response.Status.BAD_GATEWAY, e);
        }

    }
}
