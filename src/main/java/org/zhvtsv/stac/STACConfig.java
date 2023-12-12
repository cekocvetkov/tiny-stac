package org.zhvtsv.stac;

import io.smallrye.config.ConfigMapping;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigMapping(prefix = "stac-client")
public interface STACConfig {
    String url();
}
