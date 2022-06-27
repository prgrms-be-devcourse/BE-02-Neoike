package prgrms.neoike.domain.draw;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class DrawTest {
    @Test
    @DisplayName("draw 를 생성한다")
    void creatDrawTest () {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime lastDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);
        int quantity = 50;

        // when
        Draw draw = Draw.builder()
                .startDate(fastDate)
                .endDate(middleDate)
                .winningDate(lastDate)
                .quantity(quantity)
                .build();

        // then
        assertAll(
                () -> Assertions.assertThat(draw.getStartDate()).isEqualTo(fastDate),
                () -> Assertions.assertThat(draw.getEndDate()).isEqualTo(middleDate),
                () -> Assertions.assertThat(draw.getWinningDate()).isEqualTo(lastDate),
                () -> Assertions.assertThat(draw.getQuantity()).isEqualTo(quantity)
        );
    }

    @Test
    @DisplayName("유효하지 않은 quantity 값을 입력한다 - 규칙 : quantity 는 양수여야 한다.")
    void quantityValidationTest() {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime lastDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);

        int invalidQuantity = -1;

        // when // then
        assertThatThrownBy(() -> Draw.builder()
                .startDate(fastDate)
                .endDate(middleDate)
                .winningDate(lastDate)
                .quantity(invalidQuantity)
                .build())
                .hasMessage("입력된 수량이 음수 입니다.")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("응모 시작, 종료, 추첨 날짜는 순서를 지켜야 한다 - 규칙 : (시작-종료-추첨) 순서")
    void dateOrderValidationTest() {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime lastDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);
        int quantity = 50;

        // when // then
        assertThatThrownBy(() -> Draw.builder()
                .startDate(middleDate)
                .endDate(fastDate)
                .winningDate(lastDate)
                .quantity(quantity)
                .build())
                .hasMessage(format("입력된 날짜의 순서가 맞지 않습니다. (startDate : {0} , endDate : {1})", middleDate, fastDate))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Draw.builder()
                .startDate(middleDate)
                .endDate(lastDate)
                .winningDate(fastDate)
                .quantity(quantity)
                .build())
                .hasMessage(format("입력된 날짜의 순서가 맞지 않습니다. (endDate : {0} , winningDate : {1})", lastDate, fastDate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("해당 응모 남은개수가 0일때 응모권을 생성할 수 없다")
    void invalidCreateDrawTicketTest () {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime lastDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);

        Draw draw = Draw.builder()
                .startDate(fastDate)
                .endDate(middleDate)
                .winningDate(lastDate)
                .quantity(0)
                .build();


        // when // then
        assertThatThrownBy(() -> draw.validateSpare())
                .isInstanceOf(IllegalStateException.class);
    }
}