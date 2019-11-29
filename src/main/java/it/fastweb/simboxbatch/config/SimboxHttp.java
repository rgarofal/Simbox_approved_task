package it.fastweb.simboxbatch.config;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimboxHttp {

    private static final String POST_URL = "https://webapp-test.fastweb.it/tmt/api/ticketSimboxApproved?timestamp=1559779200&auth=2ba3b7d0f9381da4187c304483af4e88";
    private static final Logger log = LoggerFactory.getLogger(SimboxHttp.class);

    public String sendTicket(String fileTmt) throws Exception {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpUriRequest request = RequestBuilder
                    .post(POST_URL)
                    .setEntity(new StringEntity(fileTmt, ContentType.TEXT_PLAIN))
                    .build();

            log.info("Executing request " + request.getRequestLine());

            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(request, responseHandler);

            return responseBody;
        }
    }
}
