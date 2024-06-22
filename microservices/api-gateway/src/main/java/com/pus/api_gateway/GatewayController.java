package com.pus.api_gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RefreshScope
public class GatewayController {
	@Value("${message: Hello Default}")
	String message;

	@RequestMapping("/fallback")
	public Mono<String> fallback() {
		return Mono.just("A problem has occured :( \nplease try again later. :)");
	}

	@RequestMapping("/message")
	public String getMessage() {
		return message;
	}

}
