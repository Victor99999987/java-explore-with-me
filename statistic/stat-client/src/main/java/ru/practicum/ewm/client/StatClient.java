package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.EndpointHitDto;

@Service
public class StatClient {
    private final RestTemplate rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    //private final RestTemplate rest = new RestTemplate();

    public StatClient(@Value("${stat-server-uri}") String statServerUrl) {
        rest.setUriTemplateHandler(new DefaultUriBuilderFactory(statServerUrl));
    }

    public ResponseEntity<Object> sendHit(EndpointHitDto endpointHitDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);

        ResponseEntity<Object> serverResponse;
        try {
            serverResponse = rest.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
        return serverResponse;
    }

}
