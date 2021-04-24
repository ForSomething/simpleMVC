package sid.utils.pool;



public abstract class ObjectPool<T> {
    private int currentItemCount;

    private int maxSize;

    private ObjectHolder currentObjectHolder;

    public ObjectPool(int maxPoolSize){
        maxSize = maxPoolSize;
        currentItemCount = 0;
    }

    public Object execute(TheTask<T> task) throws Exception{
        T freeInstance = null;
        try{
            freeInstance = blockingGet();
            return task.execute(freeInstance);
        } finally {
            if(freeInstance != null){
                returnInstance(freeInstance);
            }
        }
    }

    public synchronized T unblockingGet(){
        if(currentObjectHolder == null){
            return null;
        }
        return blockingGet();
    }

    public synchronized T blockingGet(){
        if(currentObjectHolder == null && currentItemCount < maxSize){
            returnInstance(newInstance());
        }
        try{
            // todo 这个地方是有对象被归还了才会notify的，所以应该不会存在唤醒之后currentObjectHolder还是null的情况，可以找个机会试试不用while如何
            while (currentObjectHolder == null){
                this.wait();
            }
        }catch (InterruptedException e){
            //理论上不会出现这个异常
            throw new RuntimeException(e);
        }
        T object = currentObjectHolder.object;
        currentObjectHolder = currentObjectHolder.preHolder;
        return object;
    }

    public synchronized void returnInstance(T instance){
        if(instance == null){
            return;
        }
        currentObjectHolder = new ObjectHolder(instance,currentObjectHolder);
        this.notify();
    }

    protected abstract T newInstance();

    @FunctionalInterface
    public interface TheTask<T>{
        Object execute(T freeItem) throws Exception;
    }

    private class ObjectHolder{
        public ObjectHolder preHolder;
        public T object;

        public ObjectHolder(T object,ObjectHolder preHolder){
            this.object = object;
            this.preHolder = preHolder;
        }
    }
}
