public class Tests {

    @BeforeSuite
    public void init(){
        System.out.println("Подготовка.");
    }

    @Test (priority = 1)
    public void doTest1(){
        System.out.println("Тест 1.");
    }
    @Test (priority = 2)
    public void doTest2(){
        System.out.println("Тест 2.");
    }
    @Test (priority = 3)
    public void doTest3(){
        System.out.println("Тест 3.");
    }

    @Test (priority = 4)
    public void doTest4(){
        System.out.println("Тест 4.");
    }

    @AfterSuite
    public void close(){
        System.out.println("Завершение.");
    }
}
