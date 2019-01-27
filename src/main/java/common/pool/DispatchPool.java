package common.pool;

import java.util.ArrayList;

public abstract class DispatchPool<T> {
    T[] instancePool;

    private int freeInstanceStackPoint;

    public DispatchPool(int poolSize){
        ArrayList<T> poolList = new ArrayList<>(poolSize);
        for(int index = 0;index < poolSize;index++){
            poolList.add(NewInstance());
        }
        instancePool = (T[])poolList.toArray();
        freeInstanceStackPoint = poolSize - 1;
    }

    public Object ExecuteWithFreeInstance(TheTask task,Object ...params) throws InterruptedException {
        T freeInstance = GetFreeInstance();
        Object result = task.Execute(freeInstance,params);
        GiveBackFreeInstance(freeInstance);
        return result;
    }

    private synchronized T GetFreeInstance() throws InterruptedException {
        if(freeInstanceStackPoint == -1){
            //如果栈顶指针没有指向任何栈内元素，说明栈内所有元素都处于忙的状态，此时调用wait方法
            this.wait();
        }
        return instancePool[freeInstanceStackPoint--];
    }

    private synchronized void GiveBackFreeInstance(T instance){
        instancePool[++freeInstanceStackPoint] = instance;
        //如果有元素被换回来了，就通知所有wait的线程
        this.notifyAll();
    }

    protected abstract T NewInstance();

    @FunctionalInterface
    public static interface TheTask{
        Object Execute(Object param1,Object ...otherParams);
    }
}
