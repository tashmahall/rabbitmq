package com.veritech.rabbitmq.controllers;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.veritech.rabbitmq.dto.Order;
import com.veritech.rabbitmq.dto.OrderStatus;
import com.veritech.rabbitmq.producer.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
	@Autowired
	private OrderService service;
	
	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping(path="/{restaurantName}",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderStatus> addOneOrder(@RequestBody Order form, @PathVariable("restaurantName") String restaurantName) {
		OrderStatus os = service.processOrder(form, restaurantName);
		
		return ResponseEntity.ok(os);
	}
	@ResponseStatus(code = HttpStatus.OK)
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getOneOrder() {
		
		
		return ResponseEntity.ok("{\"status\":\"OK\"}");
	}
}
