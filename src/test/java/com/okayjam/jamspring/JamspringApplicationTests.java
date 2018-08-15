package com.okayjam.jamspring;

import com.okayjam.jamspring.entity.Demo;
import com.okayjam.jamspring.service.DemoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JamspringApplicationTests {

    @Autowired
    private DemoService service;

    @Test
    public void contextLoads() {
        Demo model = new Demo();
        model.setKey("测试key1");
        model.setValue("测试value1");

        // 记录数
        List<Demo> all = service.selectAll();
        int size = all.size();

        // insert
        boolean result = service.insert(model);
        Assert.assertTrue(result);

        // select
        Demo selectModel = service.select(model.getId());
        Assert.assertNotNull(selectModel);

        // selectAll
        all = service.selectAll();
        Assert.assertEquals(size + 1, all.size());

        // updateValue
        selectModel.setValue("测试更改value1");
        result = service.updateValue(selectModel);
        Assert.assertTrue(result);

        // delete
        result = service.delete(selectModel.getId());
        Assert.assertTrue(result);

    }

}
