package V1Learn.spring.entity;

import V1Learn.spring.enums.Gender;
import V1Learn.spring.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user")
public class User extends AbstractEntity implements Serializable {

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "password")
    String password;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "phone")
    String phone;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE) // chỉ lưu ngày, tháng, năm
    LocalDate dob;

    @Enumerated(EnumType.STRING) // Lưu tên enum dưới dạng chuỗi trong DB
    @Column(name = "gender")
    Gender gender;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles = new HashSet<>();

    @Column(name = "address")
    String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    UserStatus status;

    @OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user")
    Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user") // Khi serialize Enrollment, bỏ qua field user bên trong đó để tránh vòng lặp
    Set<Enrollment> enrollments;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Course> courses;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("recipient")
    Set<Notification> notifications;

    @Column(name = "avatar_url")
    String avatarUrl;

    @Column(name = "avatar_public_id")
    String avatarPublicId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user")
    Set<RegisterTeacher> registerTeachers;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
