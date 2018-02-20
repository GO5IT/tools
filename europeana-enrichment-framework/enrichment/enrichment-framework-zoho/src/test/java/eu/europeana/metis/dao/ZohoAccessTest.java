package eu.europeana.metis.dao;

//import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.fasterxml.jackson.databind.JsonNode;

import eu.europeana.metis.exception.BadContentException;
import eu.europeana.metis.exception.GenericMetisException;
import junit.framework.Assert;

/**
 * Unit tests for Zoho access 
 * @author roman.graf@ait.ac.at
 *
 */
@Configuration
@ComponentScan(basePackages = {"eu.europeana.metis"})
@PropertySource("classpath:authentication.properties")
//@EnableWebMvc
//@EnableScheduling
public class ZohoAccessTest { // extends WebMvcConfigurerAdapter {

	private EnrichmentZohoAccessClientDao zohoClient = null;
	
	@Value("${zoho.base.url}")
	private String zohoBaseUrl;
	@Value("${zoho.authentication.token}")
	private String zohoAuthenticationToken;	
	private final String TEST_ORGANIZATION_NAME = "Memini"; //"Media Deals"; 
	private final String TEST_ORGANIZATION_ID = "1482250000005593027";
	private final String COMPANY_FIELD = "Company"; 
	private final String ZOHO_BASE_URL = "https://crm.zoho.com/crm/private";
	private final String ZOHO_AUTHENTICATION_TOKEN = "a0fa7bf7a12c292d209773f29d02e656";
	private final String START_INDEX = "1"; 
	private final String END_INDEX = "10"; 

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}	
	
	/**
	 * Initialization of the Zoho client
	 */
	@Before
	@Bean
	public void mockUp() {
//		zohoClient = new EnrichmentZohoAccessClientDao();
//		zohoClient = new EnrichmentZohoAccessClientDao(zohoBaseUrl, zohoAuthenticationToken);
		zohoClient = new EnrichmentZohoAccessClientDao(ZOHO_BASE_URL, ZOHO_AUTHENTICATION_TOKEN);
	}

	/**
	 * Retrieval of organizations by start and end indexes test
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws GenericMetisException 
	 */
	@Test
	public void testGetOrganizationsFromZohoForIndexes() throws IOException, ParseException, GenericMetisException {
		JsonNode organizationsJson = zohoClient.getOrganizations(START_INDEX, END_INDEX);
		Map<String,String> organizationsMap = zohoClient.getOrganizationsMapFromListOfJsonNodes(organizationsJson);
		
		for (Map.Entry<String, String> entry : organizationsMap.entrySet()) {
		    System.out.println(entry.getKey() + "/" + entry.getValue());
		}		
		Assert.assertTrue(organizationsMap.size() > 0);
	}
	
	/**
	 * Get organization from Zoho by name test
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws GenericMetisException 
	 */
	@Test
	@Ignore
	public void testGetOrganizationFromZohoByName() throws IOException, ParseException, GenericMetisException {
		String organizationId = zohoClient.getOrganizationIdByOrganizationName(TEST_ORGANIZATION_NAME);
		Assert.assertEquals(organizationId, TEST_ORGANIZATION_ID);
	}
	
	/**
	 * All leads retrieval test
	 * @throws BadContentException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@Test
//	@Bean
	@Ignore
	public void testGetAllLeadsFromZoho() throws IOException, BadContentException, ParseException {
//		zohoClient = new EnrichmentZohoAccessClientDao(zohoBaseUrl, zohoAuthenticationToken);
		JsonNode leadsJson = zohoClient.getRecords();
		Map<String,String> leadsMap = zohoClient.getRecordsMapFromListOfJsonNodes(leadsJson);
		
		for (Map.Entry<String, String> entry : leadsMap.entrySet()) {
		    System.out.println(entry.getKey() + "/" + entry.getValue());
		}		
		Assert.assertTrue(leadsMap.size() > 0);
	}
	
	/**
	 * Organization retrieval test
	 * @throws BadContentException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@Test
	@Ignore
	public void testGetOrganizationFromZoho() throws IOException, BadContentException, ParseException {
		JsonNode leadsJson = zohoClient.getRecords();
		Map<String,String> leadsMap = zohoClient.getRecordsMapFromListOfJsonNodes(leadsJson);
		String organizationName = leadsMap.get(COMPANY_FIELD);
		Assert.assertEquals(TEST_ORGANIZATION_NAME, organizationName);
	}
}
