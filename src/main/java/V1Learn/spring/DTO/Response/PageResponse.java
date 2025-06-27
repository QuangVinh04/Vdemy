package V1Learn.spring.DTO.Response;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PageResponse<T> {
    int pageNo;
    int pageSize;
    long totalPage;
    T items;
}