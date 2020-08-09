import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Task2Test {

    private Tasks tasks;

    @BeforeEach
    public void init(){
        tasks = new Tasks();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tusk2Test(int[] arr){
        Assertions.assertTrue(tasks.doTask2(arr));
    }

    public static Stream<Arguments> data() {
        List<Arguments> out = new ArrayList<>();

        out.add(Arguments.arguments(new int[]{1,1,1,1,1}));
        out.add(Arguments.arguments(new int[]{4,4,4,4,4}));
        out.add(Arguments.arguments(new int[]{1,4,1,4,1}));
        out.add(Arguments.arguments(new int[]{1,4,1,4,3}));

        return out.stream();
    }

}
