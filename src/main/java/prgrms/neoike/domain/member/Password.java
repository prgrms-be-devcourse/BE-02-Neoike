package prgrms.neoike.domain.member;

import javax.persistence.Embeddable;

@Embeddable
public class Password {

    private static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,16}$";

    private String password;
}