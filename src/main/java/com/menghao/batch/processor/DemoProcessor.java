package com.menghao.batch.processor;

import com.menghao.batch.entity.file.CsvUser;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


/**
 * @author Yang
 */
@Component
public class DemoProcessor implements ItemProcessor<CsvUser,CsvUser> {
    @Override
    public CsvUser process(CsvUser csvUser) throws Exception {
        int age=csvUser.getAge();
        csvUser.setAge(age+10);
        return csvUser;
    }
}
