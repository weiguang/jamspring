package com.okayjam.web.req;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * rms-controller
 *
 * @author JamChen
 * @date 2023/05/15 11:08
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TbTestQueryReq extends BaseReq {

    private String key;
}
