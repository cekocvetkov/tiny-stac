package org.zhvtsv;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.json.JSONObject;
import org.zhvtsv.models.ExtentRequest;
import org.zhvtsv.service.GeoTiffService;
import org.zhvtsv.stac.STACClient;
import org.zhvtsv.stac.dto.STACItemPreview;

import java.util.Arrays;
import java.util.List;

@Path("/api/v1")
public class StacResource {
    private static final Logger LOG = Logger.getLogger(StacResource.class);

    STACClient stacClient;
    GeoTiffService geoTiffService;

    public StacResource(STACClient stacClient, GeoTiffService geoTiffService){
        this.stacClient = stacClient;
        this.geoTiffService = geoTiffService;
    }

    @POST
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems(ExtentRequest extentRequest) {
        LOG.info("Get items request for extent "+ Arrays.toString(extentRequest.getExtent()));
        List<STACItemPreview> stacItemPreviewList = this.stacClient.getStacItems(getBoundingBoxString(extentRequest.getExtent()));
        LOG.info("Found "+stacItemPreviewList.size()+" items.");
        return Response.ok(stacItemPreviewList).build();
    }

    @POST
    @Path("/geotiff")
    @Produces("image/tiff")
    public Response getGeoTiff(ExtentRequest extentRequest) {
        LOG.info("Load GeoTiff for item with id "+ extentRequest.getId() + " and extent "+Arrays.toString(extentRequest.getExtent()));
        STACItemPreview stacItemPreview = this.stacClient.getStacItemById(extentRequest.getId());
        byte [] geotiff = this.geoTiffService.downloadStacItemGeoTiffRGB(stacItemPreview.getDownloadUrl(), extentRequest.getExtent());
        return Response.ok(geotiff).build();
    }
    private static String getBoundingBoxString(double[] extent) {
        String array = Arrays.toString(extent);
        return array.substring(1, array.length() - 2).replaceAll("\\s+", "");
    }
}
