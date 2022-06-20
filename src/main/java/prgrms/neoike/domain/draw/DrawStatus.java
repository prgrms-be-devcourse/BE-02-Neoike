package prgrms.neoike.domain.draw;

public enum DrawStatus {
    WAITING("응모 대기"),
    WINNING("당첨 성공"),
    BANG("당첨 실패");

    private final String descriptiion;

    DrawStatus(String descriptiion) {
        this.descriptiion = descriptiion;
    }
}
