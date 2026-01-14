package V1Learn.spring.entity;


import V1Learn.spring.enums.CheckoutState;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@Entity
@Table(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart extends AbstractEntity  {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    List<CartItem> items = new ArrayList<>();

}
