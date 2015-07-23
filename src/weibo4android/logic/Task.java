
package weibo4android.logic;

import java.util.Map;

public class Task {

    private int mTaskID;// 任务ID
    @SuppressWarnings("rawtypes")
    private Map mTaskParam;// 内容参数
    public static final int TASK_USER_LOGIN = 0;// 用户登录任务
    public static final int TASK_GET_USER_HOMETIMEINLINE = 1;// 获取用户首页信息
    public static final int TASK_SEARCH_WEIBO = 2;// 收索微博

    @SuppressWarnings("rawtypes")
    public Task(int id, Map param)
    {
        this.mTaskID = id;
        this.mTaskParam = param;
    }

    public int getTaskID() {
        return mTaskID;
    }

    public void setTaskID(int taskID) {
        this.mTaskID = taskID;
    }

    @SuppressWarnings("rawtypes")
    public Map getTaskParam() {
        return mTaskParam;
    }

    @SuppressWarnings("rawtypes")
    public void setTaskParam(Map taskParam) {
        this.mTaskParam = taskParam;
    }
}
