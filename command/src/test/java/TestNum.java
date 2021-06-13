import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TestNum {
    @Test
    public void test1arrayAfter4() {
        int[] in = new int[]{1, 3, 4, 5, 5, 6, 8};
        int[] out = new int[]{5, 5, 6, 8};
        Assertions.assertArrayEquals(out, TestHW.Test.copyArrayAfter4(in));
    }

    @Test
    public void test2arrayAfter4() {
        int[] in = new int[]{1, 3, 5, 5, 6, 8};
        Assertions.assertThrows(RuntimeException.class, () -> TestHW.Test.copyArrayAfter4(in));
    }

    @Test
    public void test1arrayContainsOnly1and4() {
        int[] in = new int[]{1, 3, 4, 5};
        Assertions.assertFalse(TestHW.Test.arrayWith1and4(in));
    }

    @Test
    public void test2arrayContainsOnly1and4() {
        int[] in = new int[]{1, 4, 1};
        Assertions.assertTrue(TestHW.Test.arrayWith1and4(in));
    }

    public static Stream<Arguments> dataForAfter4() {
        List<Arguments> list = new ArrayList<>();
        list.add(Arguments.arguments(new int[]{2, 5, 4, 7, 8}, new int[]{7, 8}));
        list.add(Arguments.arguments(new int[]{2, 5, 4}, new int[]{}));
        list.add(Arguments.arguments(new int[]{2, 5, 4, 7, 8, 4}, new int[]{}));
        list.add(Arguments.arguments(new int[]{2, 5, 4, 7, 4, 8}, new int[]{8}));
        return list.stream();
    }

    @ParameterizedTest
    @MethodSource("dataForAfter4")
    public void massTestArrayAfter4(int[] in, int[] out) {
        Assertions.assertArrayEquals(out, TestHW.Test.copyArrayAfter4(in));
    }
}
