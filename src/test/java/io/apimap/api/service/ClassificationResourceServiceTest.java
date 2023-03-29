package io.apimap.api.service;

import io.apimap.api.configuration.ApimapConfiguration;
import io.apimap.api.repository.IRESTConverter;
import io.apimap.api.repository.SearchRepository;
import io.apimap.api.repository.interfaces.IApi;
import io.apimap.api.repository.interfaces.IApiClassification;
import io.apimap.api.repository.interfaces.IMetadata;
import io.apimap.api.repository.interfaces.ITaxonomyCollectionVersionURN;
import io.apimap.api.repository.repository.IApiRepository;
import io.apimap.api.repository.repository.IClassificationRepository;
import io.apimap.api.repository.repository.IMetadataRepository;
import io.apimap.api.repository.repository.ITaxonomyRepository;
import io.apimap.api.rest.ClassificationTreeRootRestEntity;
import io.apimap.api.rest.jsonapi.JsonApiRestResponseWrapper;
import io.apimap.api.router.ClassificationRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    void setUp() {
        classificationResourceService = new ClassificationResourceService(classificationRepository,
                apiRepository,
                metadataRepository,
                taxonomyRepository,
                apimapConfiguration,
                searchRepository,
                entityMapper);

        uri = URI.create("/classification");

        when(serverRequest.uri()).thenReturn(uri);
        when(serverRequest.queryParams()).thenReturn(new MultiValueMapAdapter<>(Map.of()));
        when(serverRequest.pathVariable(ClassificationRouter.CLASSIFICATION_URN_KEY)).thenReturn("urn:apimap:89");
    }

    @Test
    void testAllClassifications() {
        when(searchRepository.find(any(), any())).thenReturn(Flux.empty());
        when(entityMapper.encodeClassifications(any(), any())).thenReturn(Mono.just(new JsonApiRestResponseWrapper<>(new ClassificationTreeRootRestEntity())));

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
        IApiClassification apiClassification = mock(IApiClassification.class);
        when(apiClassification.getApiId()).thenReturn("api-1");
        when(apiClassification.getApiVersion()).thenReturn("v1");
        when(apiClassification.getTaxonomyUrn()).thenReturn("urn:apimap:89");

        IMetadata metadata = mock(IMetadata.class);
        when(metadata.getApiId()).thenReturn("api-1");
        when(metadata.getApiVersion()).thenReturn("v1");

        IApi api = mock(IApi.class);
        when(api.getId()).thenReturn("api-1");

        ITaxonomyCollectionVersionURN taxonomyVersion = mock(ITaxonomyCollectionVersionURN.class);
        when(taxonomyVersion.getUrl()).thenReturn("urn:apimap:89#1");

        when(classificationRepository.allByURN(any())).thenReturn(Flux.just(apiClassification));
        when(classificationRepository.all(any())).thenReturn(Flux.just(apiClassification));
        when(metadataRepository.get(any(), any())).thenReturn(Mono.just(metadata));
        when(apiRepository.getById(any())).thenReturn(Mono.just(api));
        when(apiRepository.getApiVersion(any(), any())).thenReturn(Mono.just(api));
        when(taxonomyRepository.getTaxonomyCollectionVersionURN(any(), any(), any())).thenReturn(Mono.just(taxonomyVersion));

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
        when(entityMapper.encodeClassifications(any(), any())).thenReturn(Mono.just(new JsonApiRestResponseWrapper<>(new ClassificationTreeRootRestEntity())));

        Mono<ServerResponse> responseMono = classificationResourceService.allClassifications(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> {
                    HttpStatus status = serverResponse.statusCode();
                    return status == HttpStatus.OK;
                })
                .verifyComplete();
    }

    @Test
    void testGetClassificationWhenNoContent() {
        when(classificationRepository.allByURN(any())).thenReturn(Flux.empty());
        when(entityMapper.encodeClassifications(any(), any())).thenReturn(Mono.just(new JsonApiRestResponseWrapper<>(new ClassificationTreeRootRestEntity())));

        Mono<ServerResponse> responseMono = classificationResourceService.getClassification(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> {
                    HttpStatus status = serverResponse.statusCode();
                    return status == HttpStatus.OK;
                })
                .verifyComplete();
    }
}