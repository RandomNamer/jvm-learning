package memory;

public class Main {
    public static void main(String[] args) {
//        ObjectMemoryLayout.printBoxedClassLayout();
//        ObjectMemoryLayout.printSampleClassLayout();
//        try{
//            DirectMemoryOOM.testDirectMemoryOOM();
//        } catch (IllegalAccessException e){
//            e.printStackTrace();
//        }
        try{
            SaveSelf.triggerGC();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
