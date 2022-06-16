package prgrms.neoike.domain.draw;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.domain.sneaker.SneakerStock;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class DrawTicket extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sneaker_stock_id")
    private SneakerStock sneakerStock;

    @Enumerated
    @Column(name = "draw_status")
    private DrawStatus drawStatus;

    public DrawTicket(Member member, SneakerStock sneakerStock, DrawStatus drawStatus) {
        this.member = member;
        this.sneakerStock = sneakerStock;
        this.drawStatus = drawStatus;
    }

    @Builder
    public DrawTicket(Member member, SneakerStock sneakerStock) {
        this.member = member;
        this.sneakerStock = sneakerStock;
        this.drawStatus = DrawStatus.WAITING;
    }
}