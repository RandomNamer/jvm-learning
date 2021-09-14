# Inspection of Object Memory Layout

To inspect memory layout of class or object, Simply applying this library:

```groovy
implementation "org.openjdk.jol:jol-core:0.9"
```

And use its `ClassLayout` class's static methods.

```java
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
```

And it will output as follows:

![image-20210914131219731](/Users/zzy/Documents/GitHub/jvm-learning/notes/images/mem-2.png)

We may find something familiar:

- Object size can be calculated from its Class definition, which is a basis of memory allocation. 

- **Object header** is 96-bit wide, which is a result of point compaction. 

- We can find that in **Instance data**,  except for primitive types, all object reference of the class weill share a common 4-Byte space. 

- JVM Manages its memory by the basis of 8-Byte, we can find a 4-Byte **padding** in the latter due to next object alignment.

## How ClassLayout Parse works?

We may check how these `parse` methods implemented by viewing its source. These parse methods will finally invoke `ClassData.parse(Object, Class)` method.

```java
private static ClassData parse(Object o, Class klass) {
        // If this is an array, do the array parsing, instead of ordinary class.
        if (klass.isArray()) {
            return new ClassData(o, klass.getName(), klass.getComponentType().getName(), arrayLength(o));
        }

        ClassData cd = new ClassData(o, klass.getName());
        Class superKlass = klass.getSuperclass();

        // TODO: Move to an appropriate constructor
        cd.isContended = ContendedSupport.isContended(klass);

        if (superKlass != null) {
            cd.addSuperClassData(klass.getSuperclass());
        }

        do {
            for (Field f : klass.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    cd.addField(FieldData.parse(f));
                }
            }
            cd.addSuperClass(ClassUtils.getSafeName(klass));
        } while ((klass = klass.getSuperclass()) != null);

        return cd;
    }
```

This will only parse fields of current class, for methods will only reside in *Method Area*, not present in Object Instance storage. 

Parsing of fields uses reflection:

```java
    public static FieldData parse(Field field) {
        return new FieldData(
                field,
                VM.current().fieldOffset(field),
                ClassUtils.getSafeName(field.getDeclaringClass()),
                field.getName(),
                ClassUtils.getSafeName(field.getType()),
                ContendedSupport.isContended(field),
                ContendedSupport.contendedGroup(field)
        );
    }
```

