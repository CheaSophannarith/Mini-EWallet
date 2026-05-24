package com.fii.ewallet.data.controller;

import com.fii.ewallet.data.dto.UsernameResponse;
import com.fii.ewallet.data.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data")
@AllArgsConstructor
public class DataController {

    private final DataService dataService;

    @GetMapping("/username")
    public ResponseEntity<UsernameResponse> getWalletUsername(@RequestParam String walletId){

        return ResponseEntity.ok().body(dataService.getWalletName(walletId));

    }

}
