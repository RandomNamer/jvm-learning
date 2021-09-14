package memory;

import java.util.ArrayList;

public class SampleClass {
    public int primitiveInt;
    public long primitiveLong;
    public Integer boxedInt;
    public Long boxedLong = 0xFFFFFFFFL;
    public int[] array;
    public String string = "123456789";
    public ArrayList<String> collection;

    public SampleClass(int primitiveInt, long primitiveLong, Integer boxedInt, String string, int[] arr, ArrayList<String> l) {
        this.primitiveInt = primitiveInt;
        this.primitiveLong = primitiveLong;
        this.boxedInt = boxedInt;
        this.string = string;
        this.array = arr;
        this.collection = l;
        System.out.println("Constructed");
    }
}
