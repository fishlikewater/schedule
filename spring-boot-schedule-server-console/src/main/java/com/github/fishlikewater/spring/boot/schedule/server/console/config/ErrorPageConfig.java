package com.github.fishlikewater.spring.boot.schedule.server.console.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.List;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({DispatcherServlet.class})
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(ResourceProperties.class)
public class ErrorPageConfig {

	private final ServerProperties serverProperties;

	private final List<ErrorViewResolver> errorViewResolvers;

	public ErrorPageConfig(ServerProperties serverProperties,
									 ObjectProvider<List<ErrorViewResolver>> errorViewResolversProvider) {
		this.serverProperties = serverProperties;
		this.errorViewResolvers = errorViewResolversProvider.getIfAvailable();
	}

	@Bean
	public ExceptionController exceptionController(ErrorAttributes errorAttributes) {
		return new ExceptionController(errorAttributes, this.serverProperties.getError(), this.errorViewResolvers);
	}
}
