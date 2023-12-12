package org.zhvtsv.service;

import it.geosolutions.imageio.core.BasicAuthURI;
import it.geosolutions.imageio.plugins.cog.CogImageReadParam;
import it.geosolutions.imageioimpl.plugins.cog.*;
import org.geotools.api.data.DataSourceException;
import org.geotools.api.geometry.Bounds;
import org.geotools.api.parameter.ParameterValueGroup;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.processing.CoverageProcessor;
import org.geotools.coverage.processing.Operations;
import org.geotools.coverage.util.FeatureUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.gce.geotiff.GeoTiffReader;

import jakarta.enterprise.context.ApplicationScoped;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.jboss.logging.Logger;
import org.json.JSONObject;
import org.zhvtsv.StacResource;
import org.zhvtsv.stac.STACConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;

@ApplicationScoped
public class GeoTiffService {
    private static final Logger LOG = Logger.getLogger(GeoTiffService.class);

    private final STACConfig stacConfig;
    private final CropImageService cropImageService;

    public GeoTiffService(STACConfig stacConfig, CropImageService cropImageService){
        this.stacConfig = stacConfig;
        this.cropImageService = cropImageService;
    }
    public byte[] downloadStacItemGeoTiffRGB(String href, double [] extent){
        GeoTiffReader reader = null;
        try {
            reader = new GeoTiffReader(href);
            GridCoverage2D coverage = reader.read(null);

            LOG.info("Reading Geotiff file successful.");
            GridCoverage2D cropped = cropImageService.cropGeoTiff(coverage, extent);
            LOG.info("GeoTiff cropped");

            return coverageToBinary(cropped);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] coverageToBinary(GridCoverage2D coverage2D)  {
        long startTime = System.currentTimeMillis();
        LOG.info("Start converting to binary");

        var stream = new ByteArrayOutputStream();
        GeoTiffWriter writer = null;
        try {
            writer = new GeoTiffWriter(stream);
            writer.write(coverage2D, null);
        } catch (IOException e) {
            throw new RuntimeException("Could not export geotiff to blob", e);
        } finally {
            if (writer != null){
                writer.dispose();
            }
        }

        LOG.info("Converted");
        // Record end time
        long endTime = System.currentTimeMillis();

        // Calculate and print the elapsed time
        long elapsedTime = endTime - startTime;
        LOG.info("Elapsed Time: " + elapsedTime/1000.0 + " seconds");

        return stream.toByteArray();
    }
}
