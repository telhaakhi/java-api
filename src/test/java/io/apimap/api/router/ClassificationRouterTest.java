package io.apimap.api.router;

import io.apimap.api.rest.jsonapi.JsonApiRestResponseWrapper;
import io.apimap.api.service.ClassificationResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(ClassificationRouter.class)
class ClassificationRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ClassificationResourceService classificationResourceService;

    @BeforeEach
    void setUp() {
        // Set up some mock behavior for the service
        JsonApiRestResponseWrapper<?> mockResponse = new JsonApiRestResponseWrapper<>();

        when(classificationResourceService.allClassifications(any()))
                .thenReturn(Mono.just(mockResponse));

        when(classificationResourceService.getClassification(any()))
                .thenReturn(Mono.just(mockResponse));
    }

    @Test
    void allClassifications() {
        webTestClient.get()
                .uri("/classification")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getClassification() {
        webTestClient.get()
                .uri("/classification/someURN")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}