package org.xpen.hello.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.cjstehno.ersatz.ErsatzServer;
import io.github.cjstehno.ersatz.cfg.ContentType;
import io.github.cjstehno.ersatz.junit.ErsatzServerExtension;

/**
 * HTTP mock测试演示
 */
@ExtendWith(ErsatzServerExtension.class)
public class ErsatzTest {
    
    @Test
    public void testSayHello(final ErsatzServer server) throws Exception {
        server.expectations(expect -> {
            expect.GET("/say/hello", req -> {
                req.called(1);
                req.query("name", "Ersatz");
                req.responder(res -> {
                    res.body("Hello, Ersatz", ContentType.TEXT_PLAIN);
                });
            });
        });

        HttpRequest request = HttpRequest
            .newBuilder(new URI(server.httpUrl("/say/hello?name=Ersatz")))
            .GET()
            .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Hello, Ersatz", response.body());
        assertTrue(server.verify());
    }
}
