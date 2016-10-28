package gen;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import com.mysql.jdbc.StringUtils;

public class MyCommentGenerator extends DefaultCommentGenerator {

	@Override 
	public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) { 
		super.addFieldComment(field, introspectedTable, introspectedColumn);
		// 添加字段注释 
		if (!StringUtils.isNullOrEmpty(introspectedColumn.getRemarks())){
			//修复多行注释BUG
			field.addJavaDocLine("//" + introspectedColumn.getRemarks().replaceAll("\\n", "\n//")); 
		}
		if (introspectedColumn.isIdentity()) {
            if (introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement().equals("JDBC")) {
                field.addAnnotation("@GeneratedValue(generator = \"JDBC\")");
            } else {
                field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
            }
        } else if (introspectedColumn.isSequenceColumn()) {
            field.addAnnotation("@SequenceGenerator(name=\"\",sequenceName=\"" + introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement() + "\")");
        }
	} 
	
}
