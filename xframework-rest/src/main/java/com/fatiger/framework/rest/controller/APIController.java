package com.fatiger.framework.rest.controller;

import com.fatiger.framework.common.beans.DataResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * Created by wengjiayu on 01/11/2017.
 * E-mail wengjiayu521@163.com
 */
@RestController
@RequestMapping("/v1")
@Api(description = "接口", value = "测试接口")
@Slf4j
public class APIController {


    @ApiOperation(value = "查询", httpMethod = "GET", notes = "查询")
    @RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
    public DataResult query(@PathVariable String id) {

        log.info("=== {}  ", id);

        return DataResult.ok("查到了");

    }

    @ApiOperation(value = "添加", httpMethod = "POST", notes = "添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public DataResult query(@RequestBody Map body) {

        log.info("=== {}  ", body);

        return DataResult.ok("添加了");

    }
}
