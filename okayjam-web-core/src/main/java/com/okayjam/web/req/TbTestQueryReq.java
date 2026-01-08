package com.okayjam.web.req;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * com.okayjam.web.req
 *
 * @author JamChen
 * @date 2023/05/15 11:08
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TbTestQueryReq extends BaseReq {

    private String key;
}
