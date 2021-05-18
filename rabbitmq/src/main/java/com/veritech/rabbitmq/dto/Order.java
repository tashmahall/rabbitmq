package com.veritech.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Order {
	private String orderId;
	private String description;
	private Double price;
	private Integer qtdItens;
	private String demandedRestaurantName;
	private Integer number;

}
