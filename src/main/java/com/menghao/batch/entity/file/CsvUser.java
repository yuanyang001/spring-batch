package com.menghao.batch.entity.file;

import lombok.Data;

/**
 * csv文件对应实体类
 * @author Yang
 */
@Data
public class CsvUser {

    private Integer id;

    private String name;

    private Integer age;

    private String description;
}
