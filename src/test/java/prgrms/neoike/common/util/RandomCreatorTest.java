package prgrms.neoike.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class RandomCreatorTest {

    @Test
    @DisplayName("size == max 경우 random 값을 만든다")
    void makeRandomNumber () {
        // given
        int size = 5;
        int max = 5;

        // when
        Set<Integer> integers = RandomCreator.noDuplication(size, max);
        integers.forEach(System.out::println);

        // then
        assertThat(integers).hasSize(size);
    }

    @Test
    @DisplayName("size > max 경우 예외를 발생시킨다.")
    void sizeIsBiggerThanMax () {
        // given
        int size = 10;
        int max = 5;

        // when // then
        assertThatThrownBy(() ->  RandomCreator.noDuplication(size, max)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("size < max 경우 random 값을 만든다")
    void MaxIsBiggerThanSize () {
        // given
        int size = 5;
        int max = 10;

        // when
        Set<Integer> integers = RandomCreator.noDuplication(size, max);
        integers.forEach(System.out::println);

        // then
        assertThat(integers).hasSize(size);
    }
}