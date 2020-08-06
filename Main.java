import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Main {

    public static final int CARS_COUNT = 4;

    public static void main(String[] args) {
        try {
            startRace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startRace() throws Exception{
        CyclicBarrier cb = new CyclicBarrier(CARS_COUNT+1);
        CountDownLatch cdl = new CountDownLatch(CARS_COUNT);

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10), cb, cdl);
        }

        //Threads
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }

        cb.await();

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

        cdl.await();

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");

    }
}
