package prgrms.neoike.domain.draw;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;
import prgrms.neoike.domain.member.Member;

@Getter
@Entity
@Table(name = "draw_ticket")
@NoArgsConstructor(access = PROTECTED)
public class DrawTicket extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "draw_id")
    private Draw draw;

    @Enumerated
    @Column(name = "draw_status")
    @NotNull
    private DrawStatus drawStatus;

    public DrawTicket(Member member, Draw draw, DrawStatus drawStatus) {
        this.member = member;
        this.draw = draw;
        this.drawStatus = drawStatus;
    }

    @Builder
    public DrawTicket(Member member, Draw draw) {
        this.member = member;
        this.draw = draw;
        this.drawStatus = DrawStatus.WAITING;
    }

    public void changeToWinner() {
        this.drawStatus = DrawStatus.WINNING;
    }

    public void drawQuit() {
        if (this.drawStatus == DrawStatus.WAITING) {
            this.drawStatus = DrawStatus.BANG;
        }
    }
}