package memory;

import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.LinkedList;

public class ObjectMemoryLayout {
    static void printBoxedClassLayout() {
        Integer i = 12;
        Long l = 114514L;
        StringBuilder sb = new StringBuilder();
        sb.append(ClassLayout.parseInstance(i).toPrintable());
        sb.append(ClassLayout.parseInstance(l).toPrintable());
        System.out.println(sb.toString());
    }

    static void printSampleClassLayout() {
        SampleClass sc = new SampleClass(
                114514,
                1919810L,
                0x7fff,
                "123456789",
                new int[100],
                new ArrayList<String>(10));
        StringBuilder sb = new StringBuilder();
        sb.append("Class Layout:\n");
        sb.append(ClassLayout.parseClass(SampleClass.class));
        sb.append("Object Layout: \n");
        sb.append(ClassLayout.parseInstance(sc).toPrintable());
        System.out.println(sb);
    }
}
