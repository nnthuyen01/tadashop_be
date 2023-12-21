package com.tadashop.nnt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.dto.BrandDto;
import com.tadashop.nnt.dto.OrderDetailResp;
import com.tadashop.nnt.dto.OrderReq;
import com.tadashop.nnt.model.Brand;
import com.tadashop.nnt.model.Order;
import com.tadashop.nnt.service.OrderService;
import com.tadashop.nnt.service.email.EmailSenderService;
import com.tadashop.nnt.utils.constant.EmailType;

import ch.qos.logback.core.model.Model;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class OrderController {

	private final OrderService orderService;
	private final EmailSenderService emailSenderService;

	@PostMapping("/order")
	public ResponseEntity<?> create(@RequestBody OrderReq orderReq) {

		return new ResponseEntity<>(orderService.createOrder(orderReq), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/orders")
	public ResponseEntity<?> getAllOrders() {
		return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/orders/page")
	public ResponseEntity<?> getOrders(
			@PageableDefault(size = 5, sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<Order> list = orderService.findAllOrder(pageable);
		List<Order> newList = list.stream().map(item -> {
			Order dto = new Order();
			BeanUtils.copyProperties(item, dto);
			dto.setState(item.getStateValue());
			return dto;
		}).collect(Collectors.toList());

		Page<Order> newPage = new PageImpl<Order>(newList, pageable, list.getTotalElements());
		return new ResponseEntity<>(newPage, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/statusOrder")
	public ResponseEntity<?> getStatePageables(@RequestParam("state") String state,
			@PageableDefault(size = 5, sort = "state", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<Order> list = orderService.searchOrder(state, pageable);

////		List<Order> newList = list.getContent().stream().map(item -> {
////			Order dto = new Order();
////			BeanUtils.copyProperties(item, dto);
////			return dto;
////		}).collect(Collectors.toList());
////
////		Page<Order> newPage = new PageImpl<Order>(newList, pageable, list.getTotalElements());

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@GetMapping("/orderUser/orders")
	public ResponseEntity<?> getAllOrdersByUser() {
		return new ResponseEntity<>(orderService.getAllOrdersByUser(), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/orders/page/{userId}")
	public ResponseEntity<?> getOrderByUserIdPageables(@PathVariable final Long userId,
			@PageableDefault(size = 5, sort = "state", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<Order> list = orderService.getOrderHistory(userId, pageable);

		List<Order> newList = list.getContent().stream().map(item -> {
			Order dto = new Order();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).collect(Collectors.toList());

		Page<Order> newPage = new PageImpl<Order>(newList, pageable, list.getTotalElements());

		return new ResponseEntity<>(newPage, HttpStatus.OK);
	}

	@GetMapping("/orderDetail/{orderId}")
	public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
		return new ResponseEntity<>(orderService.findByIdOrder(orderId), HttpStatus.OK);
	}

	// Chua
	// Count order by day month year
	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/order/count-order")
	public ResponseEntity<?> getNumberOrderByDay(@RequestParam(defaultValue = "0") int day,
			@RequestParam(defaultValue = "0") int month, @RequestParam(defaultValue = "0") int year) {

		return new ResponseEntity<>(orderService.countOrderByDay(day, month, year), HttpStatus.OK);
	}

	// Chua
	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/order/count-revenue")
	public ResponseEntity<?> getRevenueByDay(@RequestParam(defaultValue = "0") int day,
			@RequestParam(defaultValue = "0") int month, @RequestParam(defaultValue = "0") int year) {
		return new ResponseEntity<>(orderService.countRevenueByDay(day, month, year), HttpStatus.OK);

	}

	// Chua
	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/order/by-status")
	public ResponseEntity<?> totalByStatus(@RequestParam("status") int status) {
		return new ResponseEntity<>(orderService.getOrderByStatus(status), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('admin:update')")
	@PutMapping("/admin/updateStatus/{id}")
	public ResponseEntity<?> updateStatusOrders(@PathVariable Long id, @RequestBody Map<String, Integer> request)
			throws MessagingException, TemplateException, IOException {
		int status = request.get("status");
		Order orderUpdate = orderService.updateStatusOrder(id, status);
		if (orderUpdate != null) {
			OrderDetailResp order = orderService.findByIdOrder(id);

			if (status == 5 || status == 2) {
				Map<String, Object> model = new HashMap<>();
				model.put("title", "Mua hàng thành công");
				model.put("orderId", order.getOrder().getId());
				model.put("payment", order.getOrder().getPayment().getName());
				Map<String, String> items = new HashMap<>();

				order.getItems().stream().forEach(item -> {
					items.put(String.format("%s <b>(x%s)</br>", item.getItemName(), item.getQuantity()),
							String.valueOf(item.getTotalPrice()));
				});
				model.put("items", items);
				model.put("total", order.getOrder().getTotalPrice());
				model.put("deliveryAddress", order.getOrder().getDeliveryAddress());
				model.put("subject", "Cảm ơn bạn đã mua hàng!");
				emailSenderService.sendEmail(order.getOrder().getOrderUser().getEmail(), model, EmailType.ORDER);
			}

			return new ResponseEntity<>("success", HttpStatus.OK);
		} else
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseEntity<>("ID order not FOUND", HttpStatus.OK));
	}

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/statistic")
	public ResponseEntity<?> getStatistic() {
		return new ResponseEntity<>(orderService.getStatistic(), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/chart")
	public ResponseEntity<?> getRevenueByDateInMonth(@RequestParam(defaultValue = "0") int month,
			@RequestParam(defaultValue = "0") int year) {
		return new ResponseEntity<>(orderService.getRevenueByDateInMonth(month, year), HttpStatus.OK);
	}

	//////

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/order/count-orderRange")
	public ResponseEntity<?> getNumberOrderByRangeDay(@RequestParam(defaultValue = "0") int sday,
			@RequestParam(defaultValue = "0") int smonth, @RequestParam(defaultValue = "0") int syear,
			@RequestParam(defaultValue = "0") int eday, @RequestParam(defaultValue = "0") int emonth,
			@RequestParam(defaultValue = "0") int eyear) {

		return new ResponseEntity<>(orderService.countOrderByRangeDay(sday, smonth, syear, eday, emonth, eyear),
				HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('admin:read')")
	@GetMapping("/admin/order/count-revenueRange")
	public ResponseEntity<?> getRevenueByRangeDay(@RequestParam(defaultValue = "0") int sday,
			@RequestParam(defaultValue = "0") int smonth, @RequestParam(defaultValue = "0") int syear,
			@RequestParam(defaultValue = "0") int eday, @RequestParam(defaultValue = "0") int emonth,
			@RequestParam(defaultValue = "0") int eyear) {
		return new ResponseEntity<>(orderService.countRevenueByRangeDay(sday, smonth, syear, eday, emonth, eyear),
				HttpStatus.OK);

	}
}