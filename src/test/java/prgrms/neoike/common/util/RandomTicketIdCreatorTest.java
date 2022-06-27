package prgrms.neoike.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class RandomTicketIdCreatorTest {

    @Test
    @DisplayName("size =< max 경우 random 값을 만든다")
    void makeRandomNumber () {
        // given
        int size = 5;
        int max1 = 5;
        int max2 = 10;

        // when
        Set<Integer> randoms1 = RandomTicketIdCreator.noDuplicationIdSet(size, max1);
        Set<Integer> randoms2 = RandomTicketIdCreator.noDuplicationIdSet(size, max2);

        // then
        assertThat(randoms1).hasSize(size);
        assertThat(randoms2).hasSize(size);
    }

    @Test
    @DisplayName("size > max 경우 예외를 발생시킨다.")
    void sizeIsBiggerThanMax () {
        // given
        int size = 10;
        int max = 5;

        // when // then
        assertThatThrownBy(() ->  RandomTicketIdCreator.noDuplicationIdSet(size, max))
                .isInstanceOf(RuntimeException.class);
    }
}