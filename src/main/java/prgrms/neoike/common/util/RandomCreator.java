package prgrms.neoike.common.util;

public class RandomCreator {
    public static int[] makeRandomArray(int size, int maximumNumber) {
        int [] array = new int[size];
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            do {
                index = (int) (Math.random() * maximumNumber);
            } while (exists(array, index));
            array[i] = index;
        }
        return array;
    }

    private static boolean exists(int array[], int index) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == index)
                return true;
        }
        return false;
    }
}
