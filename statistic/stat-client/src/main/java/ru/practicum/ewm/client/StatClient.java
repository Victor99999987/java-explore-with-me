package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatClient {
    private final RestTemplate rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris, boolean unique) {

        Map<String, Object> params = new HashMap<>();
        params.put("start", start.format(FORMATTER));
        params.put("end", end.format(FORMATTER));
        params.put("unique", unique);
        String urisPath = "";
        if (uris != null) {
            urisPath = "&uris={uris}";
            params.put("uris", String.join("&uris=", uris));
        }

        ResponseEntity<List<ViewStatsDto>> serverResponse;
        try {
            serverResponse = rest.exchange("/stats?start={start}&end={end}&unique={unique}" + urisPath,
                    HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    }, params);
        } catch (HttpStatusCodeException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
        return new ResponseEntity<>(serverResponse.getBody(), HttpStatus.OK);
    }

}
