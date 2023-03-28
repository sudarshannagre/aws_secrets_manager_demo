package com.sud.aws_secrets_manager_demo.config;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sud.aws_secrets_manager_demo.model.SecretDetails;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Configuration
public class SecretConfig implements InitializingBean {

	@Autowired
	private Environment env;

	private static final Logger logger = LoggerFactory.getLogger(SecretConfig.class);

	private void getSecret() {
		logger.info("getSecret() : start at : {}", new Date());

		Region region = Region.of(env.getProperty("aws.region.name"));

		// Create a Secrets Manager client
		SecretsManagerClient client = SecretsManagerClient.builder().region(region).build();

		GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
				.secretId(env.getProperty("aws.secret.name")).build();

		GetSecretValueResponse getSecretValueResponse;

		try {
			getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
		} catch (Exception e) {
			throw e;
		}

		String secret = getSecretValueResponse.secretString();

		try {
			SecretDetails sd = new ObjectMapper().readValue(secret, SecretDetails.class);
			logger.info("Id : {}", sd.getId());
			logger.info("Secret : {}", sd.getSecret());
		} catch (JsonMappingException e) {
			logger.error(e.getMessage());
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		logger.info("getSecret() : End at : {}", new Date());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		getSecret();
	}
}
