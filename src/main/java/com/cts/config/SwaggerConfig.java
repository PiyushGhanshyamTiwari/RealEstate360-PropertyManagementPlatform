package com.cts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI customOpenAPI() {
	    return new OpenAPI()
	            .info(new Info()
	            .title("Property Management and operation System")
	            .version("1.0")
	            .description("It allows to manage property and help tenant to rent a property.")
	            .contact(new Contact().name("Pod4").email("pod4@cognizant.com")))
	            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
	            .components(new Components()
	                .addSecuritySchemes("bearerAuth",
	                    new SecurityScheme()
	                        .name("Authorization")
	                        .type(SecurityScheme.Type.HTTP)
	                        .scheme("bearer")
	                        .bearerFormat("JWT")));
	}

}
