package org.xpen.hello.search.elasticsearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BinaryResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import junit.framework.Assert;

/**
 * ElasticSearch演示
 */
public class ElasticSearchTest {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchTest.class);
	private static ElasticsearchClient esClient;

    @BeforeAll
    public static void setUp() throws Exception {
		esClient = buildEsClient("https://host:9200", "user", "pass");
	}
	
    /**
     * 索引,查询
     */
    @Test
    public void testSimple() throws Exception {
		Person person = new Person(20, "Mark Doe", new Date(1471466076564L));
		IndexResponse response = esClient.index(i -> i
		  .index("person")
		  .id(person.fullName)
		  .document(person));
		LOGGER.info("Indexed with version " + response.version());
		MatcherAssert.assertThat("Unexpected response result", response.result(), Matchers.anyOf(Matchers.equalTo(Result.Created), Matchers.equalTo(Result.Updated)));
		Assert.assertEquals("person", response.index());
		Assert.assertEquals("Mark Doe", response.id());
		
		String jsonString = "{\"age\":10,\"dateOfBirth\":1471466076564,\"fullName\":\"John Doe\"}";
		StringReader stringReader = new StringReader(jsonString);
		response = esClient.index(i -> i
		  .index("person")
		  .id("John Doe")
		  .withJson(stringReader));
		
		String searchText = "John";
		SearchResponse<Person> searchResponse = esClient.search(s -> s
		  .index("person")
		  .query(q -> q
		    .match(t -> t
		      .field("fullName")
		      .query(searchText))), Person.class);

		List<Hit<Person>> hits = searchResponse.hits().hits();
		Assert.assertEquals(1, hits.size());
		Assert.assertEquals("John Doe", hits.get(0).source().fullName);
    }
    
    /**
     * ESQL
     */
    @Test
    public void testEsql() throws Exception {
		//演示person数据获取
		String query ="from person" + 
			        "| where fullName == \"Mark Doe\"";
		BinaryResponse response = esClient.esql().query(q -> q
			    .format("csv")
			    .delimiter(",")
			    .query(query));
		String csv = new BufferedReader(new InputStreamReader(response.content()))
	            .lines().collect(Collectors.joining("\n"));
		LOGGER.info(csv);
		
		
		//演示metricbeat数据获取
		String query2 ="FROM metricbeat-*"
				+ "| WHERE event.dataset == \"system.cpu\""
				+ "| SORT @timestamp desc"
				+ "| KEEP @timestamp, system.cpu.idle.norm.pct"
				+ "| LIMIT 10"
				+ "| STATS AVG(system.cpu.idle.norm.pct)";
		response = esClient.esql().query(q -> q
			    .format("csv")
			    .delimiter(",")
			    .query(query2));
		csv = new BufferedReader(new InputStreamReader(response.content()))
	            .lines().collect(Collectors.joining("\n"));
		LOGGER.info(csv);
    }
    
    private static ElasticsearchClient buildEsClient(String host, String user, String pass) throws Exception {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass));
        SSLContext sslContext = SSLContexts.custom()
            .loadTrustMaterial(null, new TrustSelfSignedStrategy())
            .build();

        RestClientBuilder builder = RestClient.builder(HttpHost.create(host))
            .setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                            .setSSLContext(sslContext)
                            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE))
            .setRequestConfigCallback(requestConfigBuilder ->
            		requestConfigBuilder.setConnectTimeout(30000)
                            .setConnectionRequestTimeout(30000)
            );
        RestClient restClient = builder.build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

	public static class Person {
		public int age;
		public String fullName;
		public Date dateOfBirth;
		
		public Person() {
		}

		public Person(int age, String fullName, Date dateOfBirth) {
			super();
			this.age = age;
			this.fullName = fullName;
			this.dateOfBirth = dateOfBirth;
		}
		
	}

}
