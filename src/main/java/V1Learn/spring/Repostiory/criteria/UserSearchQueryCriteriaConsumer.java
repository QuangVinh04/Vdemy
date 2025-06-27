package V1Learn.spring.Repostiory.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {

    private Predicate predicate;                // Điều kiện lọc trong truy vấn (tương đương với WHERE trong SQL).
    private CriteriaBuilder criteriaBuilder;    // xây dựng truy vấn tiêu chí JPA.
    private Root root;                          //Đại diện cho bảng (User) trong truy vấn.

    // chuyển đổi SearchCriteria -> Predicate trong JPA Criteria API
    @Override
    public void accept(SearchCriteria param) {
        if(param.getOperation().equalsIgnoreCase(">")){ // equalsIgnoreCase so sánh không phân biệt chữ hoa, thường
            predicate = criteriaBuilder.and(predicate, criteriaBuilder   // and : kết hợp 2 điều kiện
                    .greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
        }

        else if (param.getOperation().equalsIgnoreCase("<")) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                    root.get(param.getKey()), param.getValue().toString()));
        }
        // xử lý điều kiện = hoặc LIKE
        else if (param.getOperation().equalsIgnoreCase(":")) {
            if (root.get(param.getKey()).getJavaType() == String.class) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(
                        root.get(param.getKey()), "%" + param.getValue() + "%"));
            } else {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
                        root.get(param.getKey()), param.getValue()));
            }
        }
    }
}
