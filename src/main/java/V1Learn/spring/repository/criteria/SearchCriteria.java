package V1Learn.spring.repository.criteria;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String key; // ex: fullname, email, ...
    private String operation; // toan tu: >, <, =, :
    private Object value; // giá trị cần search
}