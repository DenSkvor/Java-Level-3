import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        doHomeWorkTasks();

    }

    public static void doHomeWorkTasks(){
        //1
        System.out.println("Задача 1.");

        String[] arr1 = {"a","b","c","d"};

        System.out.println(Arrays.toString(arr1));
        System.out.println("Меняем местами 0 и 1 элементы: " + swapElements(arr1,0,1));
        System.out.println("Результат: " + Arrays.toString(arr1) + "\n");

        //2
        System.out.println("Задача 2.");

        String[] arr2 = {"a","b","c","d"};

        ArrayList<Object> arrayList;
        arrayList = doArrayAsList(arr2);

        System.out.println(arrayList);

        arrayList.add("e");

        System.out.println("Добавили элемент \"е\": " + arrayList + "\n");

        //3
        System.out.println("Задача 3.");

        Box<Apple> appleBox = new Box<>();
        Box<Orange> orangeBox = new Box<>();

        for (int i = 0; i < 5; i++) {
            appleBox.addFruitInBox(new Apple());
            orangeBox.addFruitInBox(new Orange());
        }

        System.out.println("Содержимое ящиков с фруктами.\n" +
                "Яблоки: " + appleBox + "\n" +
                "Апельсины: " + orangeBox + "\n");
        System.out.println("Сравниваем ящики с фруктами. Веса равны? - " + appleBox.compare(orangeBox));
        System.out.println("Вес ящика с яблоками: " + appleBox.getWeight() + "\n" +
                "Вес ящика с апельсинами: " + orangeBox.getWeight() + "\n");

        Box<Apple> appleBox2 = new Box<>();
        appleBox2.addFruitInBox(new Apple());
        System.out.println("Второй ящик с яблоками: " + appleBox2);
        appleBox.replaceFruit(appleBox2, 5);
        System.out.println("Пересыпали яблоки из первого ящика во второй.\n" +
                "Первый ящик: " + appleBox + "\n" +
                "Второй ящик: " + appleBox2);

    }

    public static boolean swapElements(Object[] arr, int swpCellNumber1, int swpCellNumber2){

        if(swpCellNumber1 < 0 || swpCellNumber1 >= arr.length
                || swpCellNumber2 < 0 || swpCellNumber2 >= arr.length) return false;

        Object tempCell;
        tempCell = arr[swpCellNumber1];
        arr[swpCellNumber1] = arr[swpCellNumber2];
        arr[swpCellNumber2] = tempCell;
        tempCell = null;
        return true;

    }

    public static ArrayList<Object> doArrayAsList(Object[] arr){
        return new ArrayList<>(Arrays.asList(arr));
    }
}
