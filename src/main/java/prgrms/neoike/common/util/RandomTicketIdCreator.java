package prgrms.neoike.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomTicketIdCreator {
    public static Set<Integer> noDuplicationIdSet(int itemQuantity, int ticketsQuantity) {
        // sneaker item 의 응모용 재고 : quantity, 해당 아이템에 응모한 응모권 개수 : maximumNumber
        if(itemQuantity > ticketsQuantity){
            throw new IllegalArgumentException("응모용 재고보다 응모권 개수가 적어 추첨을 진행하지 않습니다");
        }

        Set<Integer> duplicationRemoved = new HashSet<>();

        while (duplicationRemoved.size() < itemQuantity) {
            duplicationRemoved.add((int) (Math.random() * ticketsQuantity));
        }

        return duplicationRemoved;
    }

}
