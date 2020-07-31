package io.split.engine.sse;

import com.google.gson.JsonObject;
import io.split.client.utils.Json;
import io.split.engine.sse.dtos.AuthenticationResponse;
import io.split.engine.sse.dtos.RawAuthResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkNotNull;

public class AuthApiClientImp implements AuthApiClient {
    private static final Logger _log = LoggerFactory.getLogger(AuthApiClient.class);

    private final CloseableHttpClient _httpClient;
    private final String _target;

    public AuthApiClientImp(String url,
                                           CloseableHttpClient httpClient) {
        _httpClient = checkNotNull(httpClient);
        _target = checkNotNull(url);
    }

    @Override
    public AuthenticationResponse Authenticate() {
        try {
            URI uri = new URIBuilder(_target).build();
            HttpGet request = new HttpGet(uri);

            CloseableHttpResponse response = _httpClient.execute(request);
            Integer statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                _log.debug(String.format("Success connection to: %s", _target));

                String jsonContent = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return getSuccessResponse(jsonContent);
            } else if (statusCode >= HttpStatus.SC_BAD_REQUEST && statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                throw new Exception(String.format("Problem to connect to : %s. Response status: %s", _target, statusCode));
            }

            return new AuthenticationResponse(false,true);
        } catch (Exception ex) {
            _log.error(ex.getMessage());

            return new AuthenticationResponse(false,false);
        }
    }

    private AuthenticationResponse getSuccessResponse(String jsonContent) {
        JsonObject jsonObject = Json.fromJson(jsonContent, JsonObject.class);
        String token = jsonObject.get("token") != null ? jsonObject.get("token").getAsString() : "";
        RawAuthResponse response = new RawAuthResponse(jsonObject.get("pushEnabled").getAsBoolean(), token);
        String channels = "";
        long expiration = 0;

        if (response.isPushEnabled()) {
            channels = response.getChannels();
            expiration = response.getExpiration();
        }

        return new AuthenticationResponse(response.isPushEnabled(), response.getToken(), channels, 3000/*expiration*/, false);
    }
}
