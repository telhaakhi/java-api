package io.apimap.api.service;

import io.apimap.api.repository.IRESTConverter;
import io.apimap.api.repository.SearchRepository;
import io.apimap.api.repository.generic.ClassificationCollection;
import io.apimap.api.repository.interfaces.IApi;
import io.apimap.api.repository.interfaces.IApiClassification;
import io.apimap.api.repository.interfaces.IApiVersion;
import io.apimap.api.repository.interfaces.IMetadata;
import io.apimap.api.repository.repository.IClassificationRepository;
import io.apimap.api.rest.ClassificationTreeRootRestEntity;
import io.apimap.api.rest.jsonapi.JsonApiRestResponseWrapper;
import io.apimap.api.service.context.ClassificationContext;
import io.apimap.api.utils.ClassificationTreeBuilder;
import io.apimap.api.utils.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple3;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

class ClassificationResourceServiceTest {

    @InjectMocks
    private ClassificationResourceService classificationResourceService;

    @Mock
    private IClassificationRepository classificationRepository;

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private IRESTConverter entityMapper;

    @Mock
    private ClassificationTreeBuilder classificationTreeBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAllClassifications() {
        MockServerRequest request = MockServerRequest.builder()
                .uri(URI.create("/classification"))
                .build();

        ClassificationContext context = RequestUtil.classificationContextFromRequest(request);
        List<Tuple3<IApi, IMetadata, IApiVersion>> searchResults = new ArrayList<>();
        List<ClassificationCollection> treeResults = new ArrayList<>();
        JsonApiRestResponseWrapper<ClassificationTreeRootRestEntity> responseWrapper = new JsonApiRestResponseWrapper<>();

        when(searchRepository.find(context.getFilters(), context.getQuery()))
                .thenReturn(Flux.fromIterable(searchResults));
        when(classificationTreeBuilder.build(context, searchResults))
                .thenReturn(Mono.just(treeResults));
        when(entityMapper.encodeClassifications(request.uri(), treeResults))
                .thenReturn(Mono.just(responseWrapper));

        Mono<ServerResponse> response = classificationResourceService.allClassifications(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void testGetClassification() {
        MockServerRequest request = MockServerRequest.builder()
                .uri(URI.create("/classification/someURN"))
                .build();

        ClassificationContext context = RequestUtil.classificationContextFromRequest(request);
        List<IApiClassification> apiData = new ArrayList<>();
        List<Tuple3<IApi, IMetadata, IApiVersion>> apis = new ArrayList<>();
        List<ClassificationCollection> treeResults = new ArrayList<>();
        JsonApiRestResponseWrapper<ClassificationTreeRootRestEntity> responseWrapper = new JsonApiRestResponseWrapper<>();

        when(classificationRepository.allByURN(context.getClassificationURN()))
                .thenReturn(Flux.fromIterable(apiData));
        when(classificationTreeBuilder.build(context, apis))
                .thenReturn(Mono.just(treeResults));
        when(entityMapper.encodeClassifications(request.uri(), treeResults))
                .thenReturn(Mono.just(responseWrapper));

        Mono<ServerResponse> response = classificationResourceService.getClassification(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }
}