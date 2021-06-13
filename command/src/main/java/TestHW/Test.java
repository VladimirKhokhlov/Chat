package TestHW;

import java.util.Arrays;

public class Test {
    public static int[] copyArrayAfter4(int[] arr) {
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == 4) {
                return Arrays.copyOfRange(arr, i + 1, arr.length);
            }
        }
        throw new RuntimeException("В массиве отсутствует 4");
    }

    public static boolean arrayWith1and4(int[] arr) {
        boolean num1 = false;
        boolean num2 = false;
        for (int i : arr) {
            if (i != 1 && i != 4) {
                return false;
            }
            if (i == 1) {
                num1 = true;
            }
            if (i == 4) {
                num2 = true;
            }
        }
        return num1 && num2;
    }
}
