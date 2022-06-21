package prgrms.neoike.domain.member;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;

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
    private LocalDateTime birthDay;

    @Embedded
    private Email email;

    @Embedded
    private Address address;

    @Enumerated(value = STRING)
    @Column(name = "gender", length = 10, nullable = false)
    private Gender gender;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")}
    )
    private Set<Authority> authorities;

    @Builder
    public Member(
            String name,
            Password password,
            PhoneNumber phoneNumber,
            LocalDateTime birthDay,
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