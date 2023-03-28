package io.apimap.api.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class ApimapConfigurationTest {

    private ApimapConfiguration apimapConfiguration;

    @BeforeEach
    public void setup() {
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        ApimapConfiguration.Enabled hostIdentifier = new ApimapConfiguration.Enabled(true);
        ApimapConfiguration.Enabled openapi = new ApimapConfiguration.Enabled(false);
        String version = "1.0";
        ApimapConfiguration.Limits limits = new ApimapConfiguration.Limits(1024L);
        apimapConfiguration = new ApimapConfiguration(metadata, hostIdentifier, openapi, version, limits);
    }

    @Test
    public void testGetVersion() {
        Assertions.assertEquals("1.0", apimapConfiguration.getVersion());
    }

    @Test
    public void testEnabledOpenapi() {
        Assertions.assertFalse(apimapConfiguration.enabledOpenapi());
    }

    @Test
    public void testEnabledHostIdentifier() {
        Assertions.assertTrue(apimapConfiguration.enabledHostIdentifier());
    }

    @Test
    public void testGetMetadata() {
        Assertions.assertEquals("value1", apimapConfiguration.getMetadata().get("key1"));
    }

    @Test
    public void testGetLimits() {
        Assertions.assertEquals(1024L, apimapConfiguration.getLimits().getMaximumMetadataDocumentSize());
    }

}