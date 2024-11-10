package me.gt.snaptickets.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import ecpay.payment.integration.exception.EcpayException;
import me.gt.snaptickets.dto.OrderDto;
import me.gt.snaptickets.dto.PaymentSearchDTO;
import me.gt.snaptickets.mapper.PaymentMapper;
import me.gt.snaptickets.model.Order;
import me.gt.snaptickets.model.Payment;
import me.gt.snaptickets.model.Ticket;
import me.gt.snaptickets.service.OrderService;
import me.gt.snaptickets.service.PaymentService;
import me.gt.snaptickets.service.TicketService;
import me.gt.snaptickets.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Hashtable;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TicketService ticketService;

    @Value("${payment.ecpay.return-url}")
    private String ecpayReturnUrl;

    @Value("${payment.ecpay.client-back-url}")
    private String ecpayClientBackUrl;

    @Value("${payment.ecpay.order-result-url}")
    private String ecpayOrderResultUrl;

    @Override
    @Transactional
    public String createPayment(OrderDto orderDto) throws EcpayException {
        AllInOne aio = new AllInOne("");
        AioCheckOutALL obj = new AioCheckOutALL();
        Order order = orderDto.convertToOrder(); // 轉換為訂單資料
        orderService.createOrder(order); // 建立訂單
        Payment payment = Payment.builder()
                .orderId(order.getOrderId())
                .paymentMethod(orderDto.getPaymentMethod())
                .paymentAmount(order.getTotalPrice())
                .build();

        Ticket ticket = ticketService.getByTicketId(order.getTicketId());

        obj.setMerchantTradeNo(payment.getPaymentId());
        obj.setMerchantTradeDate(payment.getCreatedAt().format(DateUtil.standardDataFormat));
        obj.setTradeDesc(ticket.getDescription());
        obj.setItemName(String.format("%s x %d", ticket.getName(), order.getQuantity()));
        obj.setTotalAmount(String.valueOf(payment.getPaymentAmount().intValue()));

        obj.setCustomField1(order.getOrderId());
        obj.setCustomField2(order.getTicketId());
        obj.setCustomField3(order.getUsername());

        Payment.Method paymentMethod = payment.getPaymentMethod();
        if (paymentMethod != Payment.Method.ALL) {
            obj.setChoosePayment(paymentMethod.getId());
        }

        obj.setNeedExtraPaidInfo("N");
        obj.setReturnURL(ecpayReturnUrl);

        if (ecpayOrderResultUrl != null && !ecpayOrderResultUrl.isEmpty()) {
            obj.setOrderResultURL(ecpayOrderResultUrl);
        }
        if (ecpayClientBackUrl != null && !ecpayClientBackUrl.isEmpty()) {
            obj.setClientBackURL(ecpayClientBackUrl);
        }

        try {
            String form = aio.aioCheckOut(obj, null); // 產生付款表單
            paymentMapper.createPayment(payment); // 將付款資料存入資料庫
            return form;
        } catch (EcpayException e) {
            throw new EcpayException(e.getMessage());
        }
    }

    @Override
    public Payment getByPaymentId(String paymentId) {
        return paymentMapper.getByPaymentId(paymentId);
    }

    @Override
    public Payment getPaymentByOrderId(String orderId) {
        return paymentMapper.getByOrderId(orderId);
    }

    @Override
    public List<Payment> getAll() {
        return paymentMapper.getAllPayments();
    }

    @Override
    public List<Payment> getAllByPaymentStatus(String status) {
        return paymentMapper.getByPaymentStatus(status);
    }

    @Override
    public List<Payment> getAllByPaymentMethod(String type) {
        return paymentMapper.getByPaymentMethod(type);
    }

    @Override
    public void updatePaymentStatus(String paymentId, Payment.Status status) {
        updatePaymentStatus(paymentId, status.name());
    }

    @Override
    public void updatePaymentStatus(String paymentId, String status) {
        paymentMapper.updatePayment(paymentId, status);
    }

    @Override
    public String tradeInfoFormatJson(String tradeInfo) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        String[] pairs = tradeInfo.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";
            node.put(key, value);
        }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
    }

    @Override
    public List<Payment> getPaymentsBySearch(PaymentSearchDTO searchDTO) {
        if (searchDTO.getPaymentId() != null) {
            return List.of(paymentMapper.getByPaymentId(searchDTO.getPaymentId()));
        }
        if (searchDTO.getOrderId() != null) {
            return List.of(paymentMapper.getByOrderId(searchDTO.getOrderId()));
        }
        if (searchDTO.getMethod() != null) {
            return paymentMapper.getByPaymentMethod(searchDTO.getMethod());
        }
        if (searchDTO.getStatus() != null) {
            return paymentMapper.getByPaymentStatus(searchDTO.getStatus().name());
        }
        return List.of();
    }

    @Override
    public void deletePayment(String id) {
        paymentMapper.deletePayment(id);
    }

    @Override
    public PaymentResult processPaymentResult(Hashtable<String, String> result) {
        AllInOne aio = new AllInOne("");
        boolean valid = aio.compareCheckMacValue(result); // 確認檢查碼是否正確
        if (valid) {
            boolean simulatePaid = false; // 是否為模擬付款
            if(result.containsKey("SimulatePaid")){
                simulatePaid = result.get("SimulatePaid").equals("1");
            }
            int rtnCode = 0; // 付款結果代碼
            if(result.containsKey("RtnCode")){
                rtnCode = Integer.parseInt(result.get("RtnCode"));
            }
            String rtnMsg = ""; // 付款結果訊息
            if(result.containsKey("RtnMsg")){
                rtnMsg = result.get("RtnMsg");
            }
            String merchantTradeNo = ""; // 特店交易編號
            if(result.containsKey("MerchantTradeNo")){
                merchantTradeNo = result.get("MerchantTradeNo");
            }
            switch (rtnCode) {
                case 1 -> {  // 付款成功
                    if (simulatePaid) {
                        updatePaymentStatus(merchantTradeNo, Payment.Status.SIMULATING);
                        return new PaymentResult(1, "模擬付款成功");
                    } else {
                        updatePaymentStatus(merchantTradeNo, Payment.Status.COMPLETED);
                        return new PaymentResult(2, "付款成功");
                    }
                }
                case 10300066 -> { // 付款待確認
                    updatePaymentStatus(merchantTradeNo, Payment.Status.CHECKING);
                    return new PaymentResult(3, "付款待確認");
                }
                default -> { // 付款失敗
                    updatePaymentStatus(merchantTradeNo, Payment.Status.FAILED);
                    return new PaymentResult(4, rtnMsg);
                }
            }
        }
        return new PaymentResult(5,"檢查碼驗證失敗");
    }

}
