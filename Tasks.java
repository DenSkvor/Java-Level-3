import java.util.Arrays;

public class Tasks {

    public static void main(String[] args) {
        //System.out.println(Arrays.toString(new Tasks().doTask1(new int[]{1,2,3,4,5,6,7,8,9})));
        //System.out.println(new Tasks().doTask2(new int[]{1,4,4,4,4}));
    }


//обрезает массив по четверке
    public int[] doTask1(int[] inputArr){

        int last4inArrNumber = 0;
        for (int i = 0; i < inputArr.length; i++) {
            if(inputArr[i] == 4) last4inArrNumber = i;
        }
        if(last4inArrNumber == 0) throw new RuntimeException("Входной массив должен содержать хотя бы одну цифру 4.");

        int[] outputArr = new int[inputArr.length - (last4inArrNumber + 1)];
        for(int i = 0; i < outputArr.length; i++){
            outputArr[i] = inputArr[(last4inArrNumber + 1) + i];
        }
        return outputArr;
    }

//проверяет массив на единицы и четверки
    public boolean doTask2(int[] inputArr){

        int number1Count = 0;
        int number4Count = 0;
        for (int i = 0; i < inputArr.length; i++) {
            if(inputArr[i] != 1 && inputArr[i] != 4) return false;
            else if(inputArr[i] == 1) number1Count++;
            else if (inputArr[i] == 4) number4Count++;
        }
        if (number1Count == 0 || number4Count == 0) return false;
        return true;
    }
}
