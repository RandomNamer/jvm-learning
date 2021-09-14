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

The allocating may not be thread-safe. Thus two strategies must be applied: synchronize memory allocation through locking, or ensure memory allocation happen thread-privately. In fact, two strategies will both take effect, and to implement the later, each thread have a Thread-Local Allocation Buffer (TLAB). You may check and enable this feature by `-xx +/-UseTLAB`. 

After memory allocation, JVM will do a bunch of work to ensure the object initialize properly, including finding its metadata, generate object header.

Then, JVM will invoke the class's `<init>()` to construct the specific object.

### Object Memory Layout

In HotSpot VM, an Object in memory consists of 3 parts: Header, Instance Data and Padding.

#### Header

A header is divided into two parts: Mark Word and Class Pointer.

Mark Word stores object's runtime data, including hash code, GC gnerational age, lock status. Lock status occupies lower 3-bits, and is defined as follow: 

```java
enum {  locked_value                 = 0, // 0 00 Lightweight locked
         unlocked_value           = 1,// 0 01 unlocked
         monitor_value            = 2,// 0 10 heavyweight locked
         marked_value             = 3,// 0 11 marked for gc
         biased_lock_pattern      = 5 // 1 01 biased locked
  };
```

Mark Word has a size of 32bit (In 32bit Systems) or 64bit (In 64bit Sysytems). 

In 64bit systems, Class Pointer can be either 32bit or 64bit by pointer compression settings. In Java 8 or later, pointer compression is on by default.

#### Instance Data

Including primitive types (1 Byte to 8 Bytes) and object pointers (4 Bytes if pointer compression is on, 8 Bytes off)

Object ordering will be combination of definition order and VM defualts: long/double, int, short/char, byte/boolean, Ordinary Object Pointers (OOPs).

#### Padding

JVM operates memory by 8 Bytes, thus if the object's size is not integral multiple of 8 Bytes, several bytes of padding will append to its end.

### Locating of Object 

From reference to object operations:

- Handle: Reference points to Handle pool in heap, whichi would point to Instance Pool and its Type Info respectively. Two pointer jumps in one access, lacks efficiency.

<img src="images/mem-3.png" alt="image-20210914153800776" style="zoom:50%;" />

- Pointer: Direct access to Instance Data. But when modifying instance data's location, reference has to be updated.

  <img src="images/mem-4.png" alt="image-20210914160313783" style="zoom:50%;" />

HotSpot mainly uses the latter.

### OOMs

- Heap OOM: Most common, possibly related to  memory leak
- StackOverFlow
- Method Area and Constant Pool: Method Area OOM can be triggered when new classes generated at runtime, like `Enhancer` in Spring framework. After Java 7, constant pool is moved to heap. We can no longer create constants (like Strings) to cause method area OOM.
- 