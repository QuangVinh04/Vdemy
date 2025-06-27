package V1Learn.spring.Entity;


import V1Learn.spring.utils.PaymentProvider;
import V1Learn.spring.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payment")
public class Payment extends AbstractEntity {



    @OneToOne
    @JoinColumn(name = "enrollment_id")
    Enrollment enrollment;

    @Column(name = "amount", nullable = false)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_provider", nullable = false, length = 50)
    PaymentProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    PaymentStatus status;

}
