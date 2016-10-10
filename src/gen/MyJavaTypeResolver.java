package gen;

import java.sql.Types;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

public class MyJavaTypeResolver extends JavaTypeResolverDefaultImpl {

	@Override
	public void addConfigurationProperties(Properties arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public FullyQualifiedJavaType calculateJavaType(
			IntrospectedColumn introspectedColumn) {
         JdbcTypeInformation jdbcTypeInformation = typeMap  
                 .get(introspectedColumn.getJdbcType());  
		switch (introspectedColumn.getJdbcType()) {
		// mysql tinyint对应java.lang.Integer
		case Types.TINYINT:
			return new FullyQualifiedJavaType("java.lang.Integer");
		case Types.DECIMAL:
			return new FullyQualifiedJavaType("java.math.BigDecimal"); 
		default:
			return jdbcTypeInformation.getFullyQualifiedJavaType();
		}
	}

	@Override
	public String calculateJdbcTypeName(IntrospectedColumn arg0) {
		return arg0.getJdbcTypeName();
	}

	@Override
	public void setContext(Context arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWarnings(List<String> arg0) {
		// TODO Auto-generated method stub

	}

}
