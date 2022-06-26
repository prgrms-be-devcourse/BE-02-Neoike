package prgrms.neoike.common.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import prgrms.neoike.service.DrawService;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


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
        assertThat(integers.size()).isEqualTo(size);
    }

    @Test
    @DisplayName("size > max 경우 random 값을 만든다")
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
        assertThat(integers.size()).isEqualTo(size);
    }
}