import java.util.ArrayList;

public class Box <F extends Fruit> {
    private F fruit = null;
    private ArrayList<F> box;

    public Box(){
        box = new ArrayList<>();
    }

    public void addFruitInBox(F fruit){
        box.add(fruit);
        if(this.fruit == null)this.fruit = fruit;
    }

    public F removeFruitFromFox(){
        if(box.size() != 0) return box.remove(box.size()-1);
        else return null;
    }

    public float getWeight(){
        if(fruit == null) return 0;
        return fruit.getWeight() * box.size();
    }

    public boolean compare(Box<?> anotherBox){
        return this.getWeight() == anotherBox.getWeight();
    }

    public int getSizeOfBox(){
        return box.size();
    }

    public void replaceFruit(Box<F> anotherBox, int fruitQuantity){
        if(fruitQuantity > box.size()) fruitQuantity = box.size();
        for(int i = 0; i < fruitQuantity; i++){
            anotherBox.addFruitInBox(this.removeFruitFromFox());
        }
    }

    public String toString(){
        return box.toString();
    }

}
