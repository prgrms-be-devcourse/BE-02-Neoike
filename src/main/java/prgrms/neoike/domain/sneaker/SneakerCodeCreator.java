package prgrms.neoike.domain.sneaker;

import lombok.NoArgsConstructor;

import static java.text.MessageFormat.format;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@NoArgsConstructor(access = PRIVATE)
public class SneakerCodeCreator {

    public static String createSneakerCode(Category category) {
        String preFix = category.name().substring(0, 2);
        String postFix = randomNumeric(7);

        return format("{0}-{1}", preFix, postFix);
    }
}
