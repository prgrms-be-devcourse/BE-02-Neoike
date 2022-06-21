package prgrms.neoike.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;

    public Authority(String authorityName) {
        this.authorityName = authorityName;
    }
}
