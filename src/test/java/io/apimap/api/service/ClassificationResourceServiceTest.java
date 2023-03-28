package io.apimap.api.service;

import io.apimap.api.configuration.ApimapConfiguration;
import io.apimap.api.repository.IRESTConverter;
import io.apimap.api.repository.SearchRepository;
import io.apimap.api.repository.interfaces.IApi;
import io.apimap.api.repository.interfaces.IApiClassification;
import io.apimap.api.repository.interfaces.IMetadata;
import io.apimap.api.repository.repository.IApiRepository;
import io.apimap.api.repository.repository.IClassificationRepository;
import io.apimap.api.repository.repository.IMetadataRepository;
import io.apimap.api.repository.repository.ITaxonomyRepository;
import io.apimap.api.rest.jsonapi.JsonApiRestResponseWrapper;
import io.apimap.api.service.context.ClassificationContext;
import io.apimap.api.service.response.ResponseBuilder;
import io.apimap.api.utils.ClassificationTreeBuilder;
import io.apimap.api.utils.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ClassificationResourceServiceTest {

    @Mock
    private IClassificationRepository classificationRepository;

    @Mock
    private IApiRepository apiRepository;

    @Mock
    private IMetadataRepository metadataRepository;

    @Mock
    private ITaxonomyRepository taxonomyRepository;

    @Mock
    private ApimapConfiguration apimapConfiguration;

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private IRESTConverter entityMapper;

    @Mock
    private ServerRequest serverRequest;

    private ClassificationResourceService classificationResourceService;

    private URI uri;

    private ClassificationContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        classificationResourceService = new ClassificationResourceService(classificationRepository,
                apiRepository,
                metadataRepository,
                taxonomyRepository,
                apimapConfiguration,
                searchRepository,
                entityMapper);

        uri = URI.create("/classification");

        context = new ClassificationContext(Collections.singletonMap("metadata", Collections.singletonMap("visibility", "Public")),
                Collections.singletonMap("apimap", "urn:apimap:89"),
                Arrays.asList("name", "description", "system identifier"),
                "Hello World",
                "urn:apimap:89");

        when(serverRequest.uri()).thenReturn(uri);
        when(RequestUtil.classificationContextFromRequest(serverRequest)).thenReturn(context);
    }

    @Test
    void testAllClassifications() {
        when(searchRepository.find(any(), any())).thenReturn(Flux.empty());
        when(classificationRepository.allByURN(any())).thenReturn(Flux.empty());
        when(entityMapper.encodeClassifications(any(), any())).thenReturn(Mono.empty());

        Mono<ServerResponse> responseMono = classificationResourceService.allClassifications(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> {
                    HttpStatus status = serverResponse.statusCode();
                    return status.is2xxSuccessful();
                })
                .verifyComplete();
    }

    @Test
    void testGetClassification() {
        IApiClassification apiClassification = new IApiClassification() {
            @Override
            public String getApiId() {
                return "api-1";
            }

            @Override
            public String getApiVersion() {
                return "v1";
            }

            @Override
            public String getClassificationUrn() {
                return "urn:apimap:89";
            }
        };
        IMetadata metadata = new IMetadata() {
            @Override
            public String getApiId() {
                return "api-1";
            }

            @Override
            public String getApiVersion() {
                return "v1";
            }

            @Override
            public String getName() {
                return "metadata-1";
            }
        };
        IApi api = new IApi() {
            @Override
            public String getId() {
                return "api-1";
            }

            @Override
            public String getName() {
                return "api-1";
            }

            @Override
            public String getDescription() {
                return "description-1";
            }
        };
        when(classificationRepository.allByURN(any())).thenReturn(Flux.just(apiClassification));
        when(metadataRepository.get(any(), any())).thenReturn(Mono.just(metadata));
        when(apiRepository.getById(any())).thenReturn(Mono.just(api));
        when(apiRepository.getApiVersion(any(), any())).thenReturn(Mono.just(api));

        when(entityMapper.encodeClassifications(any(), any())).thenReturn(Mono.empty());

        Mono<ServerResponse> responseMono = classificationResourceService.getClassification(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> {
                    HttpStatus status = serverResponse.statusCode();
                    return status.is2xxSuccessful();
                })
                .verifyComplete();
    }

    @Test
    void testAllClassificationsWhenNoContent() {
        when(searchRepository.find(any(), any())).thenReturn(Flux.empty());

        Mono<ServerResponse> responseMono = classificationResourceService.allClassifications(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> {
                    HttpStatus status = serverResponse.statusCode();
                    return status == HttpStatus.NO_CONTENT;
                })
                .verifyComplete();
    }

    @Test
    void testGetClassificationWhenNoContent() {
        when(classificationRepository.allByURN(any())).thenReturn(Flux.empty());

        Mono<ServerResponse> responseMono = classificationResourceService.getClassification(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> {
                    HttpStatus status = serverResponse.statusCode();
                    return status == HttpStatus.NO_CONTENT;
                })
                .verifyComplete();
    }
}