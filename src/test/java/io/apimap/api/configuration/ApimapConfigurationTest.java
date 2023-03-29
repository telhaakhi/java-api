package io.apimap.api.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ApimapConfigurationTest {
    private HashMap<String, String> metadata;
    private ApimapConfiguration.Enabled hostIdentifier;
    private ApimapConfiguration.Enabled openapi;
    private String version;
    private ApimapConfiguration.Limits limits;
    private ApimapConfiguration apimapConfiguration;

    @BeforeEach
    void setUp() {
        metadata = new HashMap<>();
        metadata.put("key", "value");
        hostIdentifier = new ApimapConfiguration.Enabled(true);
        openapi = new ApimapConfiguration.Enabled(false);
        version = "1.0.0";
        limits = new ApimapConfiguration.Limits(1000L);
        apimapConfiguration = new ApimapConfiguration(metadata, hostIdentifier, openapi, version, limits);
    }

    @Test
    void getVersion() {
        assertEquals(version, apimapConfiguration.getVersion());
    }

    @Test
    void enabledOpenapi() {
        assertFalse(apimapConfiguration.enabledOpenapi());

        ApimapConfiguration.Enabled openapiEnabled = new ApimapConfiguration.Enabled(true);
        ApimapConfiguration apimapConfigurationWithOpenapi = new ApimapConfiguration(metadata, hostIdentifier, openapiEnabled, version, limits);
        assertTrue(apimapConfigurationWithOpenapi.enabledOpenapi());
    }

    @Test
    void enabledHostIdentifier() {
        assertTrue(apimapConfiguration.enabledHostIdentifier());

        ApimapConfiguration.Enabled hostIdentifierDisabled = new ApimapConfiguration.Enabled(false);
        ApimapConfiguration apimapConfigurationWithHostIdentifier = new ApimapConfiguration(metadata, hostIdentifierDisabled, openapi, version, limits);
        assertFalse(apimapConfigurationWithHostIdentifier.enabledHostIdentifier());
    }

    @Test
    void getMetadata() {
        HashMap<String, String> expectedMetadata = new HashMap<>();
        expectedMetadata.put("key", "value");
        assertEquals(expectedMetadata, apimapConfiguration.getMetadata());
        assertNotSame(expectedMetadata, apimapConfiguration.getMetadata());
    }

    @Test
    void getLimits() {
        assertEquals(limits, apimapConfiguration.getLimits());
    }

    @Test
    void enabledClassConstructorAndGetters() {
        ApimapConfiguration.Enabled enabled = new ApimapConfiguration.Enabled(true);
        assertTrue(enabled.isEnabled());
    }

    @Test
    void limitsClassConstructorAndGetters() {
        ApimapConfiguration.Limits limits = new ApimapConfiguration.Limits(1000L);
        assertEquals(1000L, limits.getMaximumMetadataDocumentSize());
    }
}