package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.CreateInvoiceRequest;
import ra.edu.dto.request.UpdateInvoiceStatusRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.InvoiceResponse;
import ra.edu.dto.response.PagedData;
import ra.edu.entity.Invoice;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.InvoiceMapper;
import ra.edu.service.InvoiceService;

import java.time.LocalDateTime;

import static ra.edu.util.ResponseUtil.convertToPagedData;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<BaseResponse<PagedData<InvoiceResponse>>> getAllInvoices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<InvoiceResponse> invoices = invoiceService.getAllInvoices(pageable);

        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy danh sách hóa đơn thành công",
                convertToPagedData(invoices),
                null,
                LocalDateTime.now()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<InvoiceResponse>> getInvoiceById(@PathVariable Integer id) {
        InvoiceResponse response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy chi tiết hóa đơn thành công",
                response,
                null,
                LocalDateTime.now()
        ));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<InvoiceResponse>> createInvoice(
            @RequestBody @Valid CreateInvoiceRequest request
    ) {
        InvoiceResponse response = invoiceService.createInvoiceFromOrder(request.getOrderId());
        return new ResponseEntity<>(new BaseResponse<>(
                true,
                "Tạo hóa đơn thành công",
                response,
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BaseResponse<InvoiceResponse>> updateInvoiceStatus(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateInvoiceStatusRequest request
    ) {
        InvoiceResponse response = invoiceService.updateInvoiceStatus(id, request);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Cập nhật trạng thái hóa đơn thành công",
                response,
                null,
                LocalDateTime.now()
        ));
    }
}
