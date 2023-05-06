package cn.bounter.annotation.trace;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class OperateLogMsg implements Serializable {

    /**
     * 日志备注
     */
    private String remark;

    /**
     * 操作人
     */
    private String creatorId;

    /**
     * 操作人姓名
     */
    private String creatorName;

}
