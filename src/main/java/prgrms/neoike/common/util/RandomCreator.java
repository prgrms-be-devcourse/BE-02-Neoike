package prgrms.neoike.common.util;

import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class RandomCreator {
    public static Set<Integer> noDuplication(int size, int maximumNumber) {
        if(size > maximumNumber){
            throw new RuntimeException();
        }

        Set<Integer> duplicationRemoved = new HashSet<>();

        while (duplicationRemoved.size() < size) {
            duplicationRemoved.add((int) (Math.random() * maximumNumber));
        }

        return duplicationRemoved;
    }

}
