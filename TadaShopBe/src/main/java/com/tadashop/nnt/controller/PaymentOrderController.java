package com.tadashop.nnt.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tadashop.nnt.dto.OrderDetailResp;
import com.tadashop.nnt.exception.AppException;
import com.tadashop.nnt.model.Order;
import com.tadashop.nnt.model.Product;
import com.tadashop.nnt.model.Size;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.model.Voucher;
import com.tadashop.nnt.paymentConfig.VnpayConfig;
import com.tadashop.nnt.repository.OrderRepo;
import com.tadashop.nnt.repository.ProductRepo;
import com.tadashop.nnt.repository.SizeRepo;
import com.tadashop.nnt.repository.UserRepo;
import com.tadashop.nnt.repository.VoucherRepo;
import com.tadashop.nnt.service.OrderService;
import com.tadashop.nnt.service.email.EmailSenderService;
import com.tadashop.nnt.utils.Utils;
import com.tadashop.nnt.utils.constant.EmailType;

import freemarker.template.TemplateException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "AUTHORIZATION")
public class PaymentOrderController {
	@Autowired
	private OrderRepo orderRepo;
	@Autowired
	private ProductRepo productRepo;
	@Autowired
	private SizeRepo sizeRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private VoucherRepo voucherRepo;
	@Autowired
	private EmailSenderService emailSenderService;
	@Autowired
	private OrderService orderService;

	@GetMapping("/payment-callback")
	public void paymentCallback(@RequestParam Map<String, String> queryParams, HttpServletResponse response)
			throws MessagingException, TemplateException, IOException {
		String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
		String contractId = queryParams.get("contractId");
		AtomicBoolean mail = new AtomicBoolean(false);
		if (contractId != null && !contractId.equals("")) {
			if ("00".equals(vnp_ResponseCode)) {
				// Giao dịch thành công
				// Thực hiện các xử lý cần thiết, ví dụ: cập nhật CSDL
				int Id = Integer.parseInt(queryParams.get("contractId"));
				Long contractIdLong = Long.valueOf(Id);
				Order order = orderRepo.findById(contractIdLong)
						.orElseThrow(() -> new AppException("Không tồn tại đơn hàng này"));
				order.setState(5);
				final boolean[] isFirstIteration = { true };
				order.getOrderDetails().stream().forEach(orderDetail -> {
					Size sizeProduct = orderDetail.getSize();

					Product product = sizeProduct.getProduct();
					int quantityInOrder = orderDetail.getQuantity();

					// Kiểm tra và trừ số lượng sản phẩm trong kho
					if (sizeProduct.getQuantity() >= quantityInOrder) {
						sizeProduct.setQuantity(sizeProduct.getQuantity() - quantityInOrder);
						product.setTotalQuantity(product.getTotalQuantity() - quantityInOrder);
						// Cập nhật thông tin sản phẩm trong kho
						productRepo.save(product);
						sizeRepo.save(sizeProduct);

						if (isFirstIteration[0]) {
							// set total amount user
							User user = order.getOrderUser();
							user.setAmountPaid(order.getTotalPrice() + user.getAmountPaid());
							userRepo.save(user);

							List<Voucher> vouchers = user.getVouchers();
							int numberOfVouchers = vouchers.size();
							double amountPaid = user.getAmountPaid();
							int temp = (int) (amountPaid / 500000);
							if (temp > numberOfVouchers) {
								int loop = temp - numberOfVouchers;
								for (int i = 1; i <= loop; i++) {
									Voucher voucher = new Voucher();
									voucher.setUser(user);
									voucher.setPriceOffPercent(10);
									voucher.setStatus(1);
									String randomPart = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
									String code = "TADA" + randomPart;
									voucher.setVoucher(code);
									voucherRepo.save(voucher);
								}
							}
							// Set the flag to false after the first iteration
							isFirstIteration[0] = false;
						}
						mail.set(true);
					} else {
						mail.set(false);
						throw new AppException("Not enough stock for product: " + sizeProduct.getId());
					}
				});

				orderRepo.save(order);
				if (mail.get() == true) {
					OrderDetailResp order1 = orderService.findByIdOrder(order.getId());
					Map<String, Object> model = new HashMap<>();
					model.put("title", "Mua hàng thành công");
					model.put("orderId", order1.getOrder().getId());
					model.put("payment", order1.getOrder().getPayment().getName());
					Map<String, String> items = new HashMap<>();

					order1.getItems().stream().forEach(item -> {
						items.put(String.format("%s <b>(x%s)</br>", item.getItemName(), item.getQuantity()),
								String.valueOf(item.getTotalPrice()));
					});
					model.put("items", items);
					model.put("total", order1.getOrder().getTotalPrice());
					model.put("deliveryAddress", order1.getOrder().getDeliveryAddress());
					model.put("subject", "Cảm ơn bạn đã mua hàng!");
					emailSenderService.sendEmail(order1.getOrder().getOrderUser().getEmail(), model, EmailType.ORDER);
				}
				response.sendRedirect(String.format("http://localhost:3002/checkout/%d/thankyou", contractIdLong));
			} else {
				// Giao dịch thất bại
				int Id = Integer.parseInt(queryParams.get("contractId"));
				Long contractIdLong = Long.valueOf(Id);
				Order order = orderRepo.findById(contractIdLong)
						.orElseThrow(() -> new AppException("Không tồn tại đơn hàng này"));
				order.setState(3);
				orderRepo.save(order);
				// Thực hiện các xử lý cần thiết, ví dụ: không cập nhật CSDL\
				response.sendRedirect("http://localhost:3002/payment-fail");

			}
		}

	}

	@GetMapping("/pay")
	public String getPay(@RequestParam("price") long price, @RequestParam("id") Integer contractId)
			throws UnsupportedEncodingException {

		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String orderType = "other";
		long amount = price * 100;
		String bankCode = "NCB";

		String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
		String vnp_IpAddr = "127.0.0.1";

		String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(amount));
		vnp_Params.put("vnp_CurrCode", "VND");

		vnp_Params.put("vnp_BankCode", bankCode);
		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
		vnp_Params.put("vnp_OrderType", orderType);

		vnp_Params.put("vnp_Locale", "vn");
		vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl + "?contractId=" + contractId);
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

		cld.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				// Build query
				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.secretKey, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;

		return paymentUrl;
	}
}
