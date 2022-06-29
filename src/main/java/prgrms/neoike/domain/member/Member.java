package prgrms.neoike.domain.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Embedded
    private Password password;

    @Embedded
    private PhoneNumber phoneNumber;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthDay;

    @Embedded
    private Email email;

    @Embedded
    private Address address;

    @Enumerated(value = STRING)
    @Column(name = "gender", length = 10, nullable = false)
    private Gender gender;

    @Builder
    public Member(
        String name,
        Password password,
        PhoneNumber phoneNumber,
        LocalDate birthDay,
        Email email,
        Address address,
        Gender gender
    ) {
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.email = email;
        this.address = address;
        this.gender = gender;
    }
}