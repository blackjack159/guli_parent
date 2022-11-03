package com.atguigu.demo;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.Test;

/**
 * @author
 * @since 2021/12/13
 */
public class CodeGenerator {

    @Test
    public void run() {

        // 1、Create Code Generator
        AutoGenerator mpg = new AutoGenerator();

        // 2、Global Config
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir("C:\\Users\\CKC\\IdeaProjects\\guli_parent\\service\\service_edu" + "/src/main/java");

        gc.setAuthor("testjava");
        gc.setOpen(false); //生成后是否打开资源管理器
        gc.setFileOverride(false); //重新生成时文件是否覆盖

        //UserService
        gc.setServiceName("%sService");	//去掉Service接口的首字母I

        gc.setIdType(IdType.ID_WORKER_STR); //主键策略
        gc.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型
        gc.setSwagger2(true);//开启Swagger2模式

        mpg.setGlobalConfig(gc);

        // 3、Database and Datasource config
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/guli?serverTimezone=GMT%2B8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("chun660717");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 4、Package Config
        PackageConfig pc = new PackageConfig();

        //package: com.example.eduservice
        pc.setParent("com.example");
        pc.setModuleName("eduservice"); //模块名

        //package com.example.eduservice.controller
        pc.setController("controller");
        pc.setEntity("entity");
        pc.setService("service");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);

        // 5、Strategy config
        StrategyConfig strategy = new StrategyConfig();

        strategy.setInclude("edu_course_collect");

        strategy.setNaming(NamingStrategy.underline_to_camel);//naming of entity
        strategy.setTablePrefix(pc.getModuleName() + "_"); //delete pre name

        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true); // lombok for chain programming

        strategy.setRestControllerStyle(true); //restful api controller
        strategy.setControllerMappingHyphenStyle(true); //url variable name for middle capital letter

        mpg.setStrategy(strategy);


        // 6、execute
        mpg.execute();
    }
}
