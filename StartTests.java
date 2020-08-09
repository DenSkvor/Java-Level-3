import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class StartTests {

    public static void main(String[] args) {
        try {
            start(Test.class);
        } catch (InvocationTargetException | RuntimeException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void start(Class testClass) throws InvocationTargetException, IllegalAccessException {
        Tests tests = new Tests();
        ArrayList<Method> methodsTest = new ArrayList<>();
        Method doBeforeSuite = null;
        Method doAfterSuite = null;

        int beforeSuitMethodCount = 0;
        int afterSuitMethodCount = 0;

        int maxTestPriority = 1;

        Method[] methods = Tests.class.getDeclaredMethods();
        for (Method m : methods) {
            if(m.getAnnotation(BeforeSuite.class) != null) {
                doBeforeSuite = m;
                beforeSuitMethodCount++;
            }
            else if(m.getAnnotation(AfterSuite.class) != null) {
                doAfterSuite = m;
                afterSuitMethodCount++;
            }
            else if(m.getAnnotation(Test.class) != null) {
                methodsTest.add(m);
                if(m.getAnnotation(Test.class).priority() > maxTestPriority) maxTestPriority = m.getAnnotation(Test.class).priority();
            }
        }

        if(beforeSuitMethodCount != 1 || afterSuitMethodCount != 1)
            throw new RuntimeException("Методы @BeforeSuite и @AfterSuite должны присутствовать в единственном экземпляре.");

        doBeforeSuite.invoke(tests);

        for (int i = maxTestPriority; i != 0 ; i--){
            for(int j = 0; j < methodsTest.size(); j++){
                if(methodsTest.get(j).getAnnotation(Test.class).priority() == i) methodsTest.get(j).invoke(tests);
            }
        }

        doAfterSuite.invoke(tests);
    }
}
