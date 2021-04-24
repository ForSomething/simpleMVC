package sid.utils.timer;

import java.util.List;

public class CommonScheduler {
    //todo 目前的设计是定时任务依赖事件模型工作，任务的调度以触发事件的形式进行
    //这样就要定义事件和事件处理程序之间的对应关系。这个其实是变相的定义定时任务类型，还是无法摆脱多一种任务就要加一种类型的问题
    //如果无法摆脱这种现实，那么采用事件模型的形式，实际上的代码方便程度和直销的那种方式也没什么本质上的区别
    //并且这种设计无法支持lambda表达式的事件处理器

    //事件模型按订阅的模式来设计，也就是谁对某个事件感兴趣，则订阅他，那么既然要指定订阅的事件，则需要显示的规定事件的名称，这样一来，多一种事件就要多定义一种名称
    //我觉得以名称来区别事件不是一个好方式
    //首先名称无法详细的描述事件的所有信息，这也就带来了后面的问题，名称难以记忆
    //那么是否可以定义一套标识事件的规则，从各个维度来对事件进行描述，如果设计的话，我觉得可以按照下面的几个维度来
    //1、触发事件的class

//    List<Task>

    public static void addTask(Task task,TaskHandler taskHandler){

    }

    public static interface TaskHandler{
        public void execute(Task task) throws Exception;
    }

    public static class Task{

    }
}