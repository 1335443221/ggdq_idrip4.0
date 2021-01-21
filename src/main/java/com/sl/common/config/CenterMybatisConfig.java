package com.sl.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

//@Configuration
@MapperScan(basePackages = {"mapper"}, sqlSessionTemplateRef = "centerSqlSessionTemplate")
public class CenterMybatisConfig {
	
	@Bean(name = "centerDataSource")
	@Primary // 必须加此注解，不然报错，下一个类则不需要添加
	@ConfigurationProperties(prefix = "idripapp.datasource") // prefix值必须是application.properteis中对应属性的前缀
	public DataSource centerDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public SqlSessionFactory centerSqlSessionFactory(@Qualifier("centerDataSource") DataSource dataSource)
			throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		// 添加XML目录
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			bean.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));
			return bean.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Bean
	public SqlSessionTemplate centerSqlSessionTemplate(
			@Qualifier("centerSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory);//  使用上面配置的Factory
		return template;
	}
}
