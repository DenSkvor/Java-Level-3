import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Task1Test {

    private static Tasks tasks;

    @BeforeAll
    public static void initGlobal(){
        tasks = new Tasks();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void tusk1Test(int[] a, int [] b){
        Assertions.assertArrayEquals(b, tasks.doTask1(a));
    }

    public static Stream<Arguments> data() {
        List<Arguments> out = new ArrayList<>();

        out.add(Arguments.arguments(new int[]{0,1,2,3,4,5,6,7,8,9}, new int[]{5,6,7,8,9}));
        out.add(Arguments.arguments(new int[]{0,1,2,3,4,5,4,7,8,9}, new int[]{7,8,9}));
        out.add(Arguments.arguments(new int[]{0,1,2,3,4,5,6,4,8,9}, new int[]{8,9}));

        return out.stream();
    }

    @Test
    public void tusk1ExceptionTest(){
        try{
            tasks.doTask1(new int[]{1,2,3,5});
        } catch (RuntimeException e) {
            Assertions.fail(e);
        }
    }

}
