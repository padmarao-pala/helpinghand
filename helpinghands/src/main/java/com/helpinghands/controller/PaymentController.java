package com.helpinghands.controller;

import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    private static final String KEY = "rzp_test_SaxoxJ9l3MJPId";
    private static final String SECRET = "nfuxz3CMAMD5QwsXjgOwCA6G";

    @PostMapping("/create-order")
    public String createOrder(@RequestParam int amount) throws Exception {

        RazorpayClient client = new RazorpayClient(KEY, SECRET);

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); // paisa
        options.put("currency", "INR");
        options.put("receipt", "txn_123");

        Order order = client.orders.create(options);

        return order.toJson().toString();    }
}