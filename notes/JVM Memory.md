# JVM Memory 

## Java Memory Model

### Runtime Memory

JVM manages memory when Java app is running. Java runtime memory contains several individual areas, each have its own purpose and structure.

- **Program Counter**: Holds the address of the bytecode instruction that's currently running. When native code is running currently (and there is no java code running now), the PC will be set to undefined. Native code will not generate OOM.

- **VM Stack: ** JVM stack is thread-private, each method call will create a Stack Frame, which contains **local variable table**, operand stack, dynamic linkage... etc.  A local variable table contains primitive types, object reference and return address. It's possible to throw OOM when allocating new VM stack in thoery, yet in HotSpot implementation stack depth is fixed, only `StackOverFlowError` will occur.

- **Native Method Stacks: ** In standard, no further specifications for Native Method Stacks. Individual NMS is optional and not present in HotSpot.

- **Heap: ** Heap's sole purpose is to store object instances. Most of the object instances will stay in heaps besides few resides on stack ( thread-private only and is an optional optimization). Heap is where GC maily works with, and by garbage collectors' design, its structure differs. Most modern GC uses [Generational garbage collection](https://en.wikipedia.org/wiki/Tracing_garbage_collection#Generational_GC_(ephemeral_GC)) , thus heap normally has parts like young, old, eden, survivor. 

  Heap is basically shared between threads. In order to improve object-allocation efficiency, threre are multiple Thread-Local Allocation Buffer (TLAB) in heap.

  "Heaps can exist in incontiguous segements in memory", yet in most modern OSs, virtual memory will render this meaningless. Java Heap can be set as either fixed-size or extensive.

- **Method Area: ** Method Area contains *loaded* type (class) info, const value, static value, JIT cache. Its a part if heap, and usually called Permanent Generation (Not completely true). Method Area can be implemented as GC-comopatible or needs no GC. GC of this area involves constant pool and uninstall of loaded classes.

<img src="./images/mem-1.png" alt="image-20210913155833692" style="zoom:33%;" />

### Memory for Objects

#### When JVM is newing objects, what is it doing?

When the class is loaded,  size of the object will be determined. Then a corresponding size of memory will be allocated for the object. 

The allocating may not be thread-safe. 