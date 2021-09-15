package memory;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class DirectMemoryOOM {
    private static final int MB = 1024*1024;
    public static void testDirectMemoryOOM() throws IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe usf  = (Unsafe) unsafeField.get(null);
        while(true){
            usf.allocateMemory(1*MB);
        }
    }
}
