package com.batch.demo.controller;

import com.batch.demo.dto.InvoiceResponseDto;
import com.batch.demo.service.InvoiceService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/invoice")
public class InvoiceController {

  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @GetMapping
  public ResponseEntity<InvoiceResponseDto> getImportInvoiceStatus() throws Exception {
    return ResponseEntity.ok(this.invoiceService.getImportInvoiceStatus());
  }
}
