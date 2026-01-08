package com.okayjam.web.req;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.Serializable;

/**
 * rms-controller
 *
 * @author JamChen
 * @date 2023/05/10 20:13
 **/
@Tag(name = "分页参数", description = "分页参数")
public class PageParam implements Serializable {

    @Schema(name = "pageNum", description = "当前页码", example = "1", defaultValue = "1")
    private int pageNum = 1;
    @Schema(name = "pageSize", description = "分页大小", example = "10", defaultValue = "10")
    private int pageSize = 10;

    public PageParam() {
    }

    public PageParam(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
