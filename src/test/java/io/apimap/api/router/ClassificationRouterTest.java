package io.apimap.api.router;

import io.apimap.api.service.ClassificationResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ClassificationRouterTest {

    @Mock
    private ClassificationResourceService classificationResourceService;

    private RouterFunction<?> routerFunction;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        routerFunction = new ClassificationRouter().classificationRoutes(classificationResourceService);

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testAllClassifications() {
        when(classificationResourceService.allClassifications(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.get().uri("/classification")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void testGetClassification() {
        when(classificationResourceService.getClassification(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.get().uri("/classification/urn:apimap:89")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}