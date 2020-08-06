public class Lesson4_sync_task {

    private volatile char currentSymbol = 'A';

    public static void main(String[] args) {
	    new Lesson4_sync_task().printABC();
    }

    public void printABC (){

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 5; i++) printA();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 5; i++) printB();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 5; i++) printC();
            }
        }).start();

    }

    public synchronized void printA(){

        try {
            while (currentSymbol != 'A') {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.print("A");
        currentSymbol = 'B';
        notifyAll();
    }

    public synchronized void printB(){

        try {
            while (currentSymbol != 'B') {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.print("B");
        currentSymbol = 'C';
        notifyAll();
    }

    public synchronized void printC(){

        try {
            while (currentSymbol != 'C') {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.print("C ");
        currentSymbol = 'A';
        notifyAll();
    }

}
