package ra.edu.util;

import org.springframework.data.domain.Page;
import ra.edu.dto.response.PagedData;

public class ResponseUtil {
    public static <T> PagedData<T> convertToPagedData(Page<T> page) {
        return new PagedData<>(
                page.getContent(),
                new PagedData.Pagination(
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalPages(),
                        page.getTotalElements()
                )
        );
    }
}
