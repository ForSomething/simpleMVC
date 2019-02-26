package common.pool;

import com.sun.jna.platform.win32.COM.TypeLibUtil;

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

    public Object ExecuteWithFreeInstance(TheTask<T> task) throws Exception{
        Object result = null;
        T freeInstance = null;
        try{
            freeInstance = GetFreeInstance();
            result = task.Execute(freeInstance);
        } finally {
            if(freeInstance != null){
                GiveBackFreeInstance(freeInstance);
            }
        }
        return result;
    }

    private synchronized T GetFreeInstance(){
        try{
            while (freeInstanceStackPoint == -1){
                //如果栈顶指针没有指向任何栈内元素，说明栈内所有元素都处于忙的状态，此时调用wait方法
                this.wait();
            }
        }catch (InterruptedException e){
            //理论上不会出现这个异常
        }
        return instancePool[freeInstanceStackPoint--];
    }

    private synchronized void GiveBackFreeInstance(T instance){
        instancePool[++freeInstanceStackPoint] = instance;
        //如果有元素被还回来了，就通知一条wait的线程
        this.notify();
    }

    protected abstract T NewInstance();

    @FunctionalInterface
    public interface TheTask<T>{
        Object Execute(T freeItem) throws Exception;
    }
}
