package memory;

public class SaveSelf {
    public static SaveSelf REF = null;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println(System.currentTimeMillis() + " On finalize at object: " + this);
        REF = this;
    }

    public static void triggerGC() throws InterruptedException {
        REF = new SaveSelf();
        for(int i = 1; i<100; i++){
            REF = null;
            System.out.println("Escape run "+ i );
            System.gc();
            Thread.sleep(1000);
            if(REF != null) System.out.println("Alive");
            else {
                System.out.println("Dead");
                break;
            }
        }
    }
}
