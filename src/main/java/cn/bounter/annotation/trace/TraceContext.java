package cn.bounter.annotation.trace;


import java.util.List;

public class TraceContext {

    private static final ThreadLocal<List<OperateLogMsg>> multiLogHolder = new ThreadLocal<>();

    private static final ThreadLocal<OperateLogMsg> logHolder = new ThreadLocal<>();

    public static List<OperateLogMsg> get() {
        return multiLogHolder.get();
    }

    public static void set(List<OperateLogMsg> userList) {
        multiLogHolder.set(userList);
    }

    private static OperateLogMsg getOperateLogMsg() {
        OperateLogMsg operateLogMsg = null;
        if (logHolder.get() == null) {
            operateLogMsg = new OperateLogMsg();
            logHolder.set(operateLogMsg);
        } else {
            operateLogMsg = logHolder.get();
        }
        return operateLogMsg;
    }

    public static void setRemark(String remark) {
        getOperateLogMsg().setRemark(remark);
    }

    public static String getRemark() {
        return logHolder.get() == null ? null : logHolder.get().getRemark();
    }

    public static void setCreateId(String createId) {
        getOperateLogMsg().setCreatorId(createId);
    }

    public static String getCreateId() {
        return logHolder.get() == null ? null : logHolder.get().getCreatorId();
    }

    public static void setCreateName(String createName) {
        getOperateLogMsg().setCreatorName(createName);
    }

    public static String getCreateName() {
        return logHolder.get() == null ? null : logHolder.get().getCreatorName();
    }

    public static void clear() {
        multiLogHolder.remove();
        logHolder.remove();
    }
}
