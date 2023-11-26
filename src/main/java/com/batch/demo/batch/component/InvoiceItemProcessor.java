package com.batch.demo.batch.component;

import com.batch.demo.batch.exception.AmountException;
import com.batch.demo.model.entity.Invoice;
import java.util.Objects;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class InvoiceItemProcessor implements ItemProcessor<Invoice, Invoice> {

  @Override
  public Invoice process(Invoice invoice) {
    if (Objects.isNull(invoice.getAmount()) || ObjectUtils.isEmpty(invoice.getAmount())
        || invoice.getAmount() == 0.0) {
      return null; // For Filtering
//      throw new AmountException("Invoice amount is null"); // For Skipping
    }
    Double discount = invoice.getAmount() * (invoice.getDiscount() / 100.0);
    Double finalAmount = invoice.getAmount() - discount;
    invoice.setFinalAmount(finalAmount);
    return invoice;
  }
}
