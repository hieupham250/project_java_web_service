package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.UpdateInvoiceStatusRequest;
import ra.edu.dto.response.InvoiceResponse;
import ra.edu.entity.Invoice;
import ra.edu.entity.Order;
import ra.edu.enums.InvoiceStatus;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.InvoiceMapper;
import ra.edu.repository.InvoiceRepository;
import ra.edu.repository.OrderRepository;
import ra.edu.service.InvoiceService;

import java.time.LocalDate;

@Service
public class InvoiceServiceImp implements InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(InvoiceMapper::toResponse);
    }

    @Override
    public InvoiceResponse getInvoiceById(Integer id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hóa đơn không tồn tại"));
        return InvoiceMapper.toResponse(invoice);
    }

    @Override
    public InvoiceResponse createInvoiceFromOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        if (order.getInvoice() != null) {
            throw new NotFoundException("Hóa đơn cho đơn hàng này đã tồn tại");
        }

        Invoice invoice = Invoice.builder()
                .order(order)
                .totalAmount(order.getTotalPrice())
                .status(InvoiceStatus.UNPAID)
                .createdAt(LocalDate.now())
                .build();

        invoiceRepository.save(invoice);
        return InvoiceMapper.toResponse(invoice);
    }

    @Override
    public InvoiceResponse updateInvoiceStatus(Integer invoiceId, UpdateInvoiceStatusRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Hóa đơn không tồn tại"));

        invoice.setStatus(request.getStatus());
        invoice.setUpdatedAt(LocalDate.now());
        invoiceRepository.save(invoice);

        return InvoiceMapper.toResponse(invoice);
    }
}
