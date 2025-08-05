package ra.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.UpdateInvoiceStatusRequest;
import ra.edu.dto.response.InvoiceResponse;

public interface InvoiceService {
    Page<InvoiceResponse> getAllInvoices(Pageable pageable);
    InvoiceResponse getInvoiceById(Integer id);
    InvoiceResponse createInvoiceFromOrder(Integer orderId);
    InvoiceResponse updateInvoiceStatus(Integer invoiceId, UpdateInvoiceStatusRequest request);
}
