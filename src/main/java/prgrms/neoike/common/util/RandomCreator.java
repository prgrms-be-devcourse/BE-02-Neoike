package prgrms.neoike.common.util;

import java.util.HashSet;
import java.util.Set;

public class RandomCreator {
    public static Set<Integer> makeRandoms(int size, int maximumNumber) {
        Set<Integer> duplicationRemoved = new HashSet<>();

        while (duplicationRemoved.size() < size) {
            duplicationRemoved.add((int) (Math.random() * maximumNumber));
        }

        return duplicationRemoved;
    }
}
