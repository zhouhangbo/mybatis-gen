package gen;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.MessageFormat;
import java.util.Iterator;

public class MyCommentGenerator extends DefaultCommentGenerator {

	@Override 
	public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) { 
		super.addFieldComment(field, introspectedTable, introspectedColumn);
		// 添加字段注释
//		if (!StringUtils.isNullOrEmpty(introspectedColumn.getRemarks())){
//			//修复多行注释BUG
//			field.addJavaDocLine("/**" + introspectedColumn.getRemarks().replaceAll("\\n+", "\n     * ") + "*/");
//		}

		if(StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
			field.addJavaDocLine("/**");
			StringBuilder sb = new StringBuilder();
			sb.append(" * ");
			sb.append(introspectedColumn.getRemarks().replaceAll("\\n+", "\n     * "));
			field.addJavaDocLine(sb.toString());
			field.addJavaDocLine(" */");
		}

		if(field.isTransient()) {
			field.addAnnotation("@Transient");
		}

		Iterator var7 = introspectedTable.getPrimaryKeyColumns().iterator();

		while(var7.hasNext()) {
			IntrospectedColumn column = (IntrospectedColumn)var7.next();
			if(introspectedColumn == column) {
				field.addAnnotation("@Id");
				break;
			}
		}

		String column = introspectedColumn.getActualColumnName();
		if(StringUtility.stringContainsSpace(column) || introspectedTable.getTableConfiguration().isAllColumnDelimitingEnabled()) {
			column = introspectedColumn.getContext().getBeginningDelimiter() + column + introspectedColumn.getContext().getEndingDelimiter();
		}

		if(introspectedColumn.isIdentity()) {
			if(introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement().equals("JDBC")) {
				field.addAnnotation("@GeneratedValue(generator = \"JDBC\")");
			} else {
				field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
			}
		} else if(introspectedColumn.isSequenceColumn()) {
			String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
			String sql = MessageFormat.format(introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement(), new Object[]{tableName, tableName.toUpperCase()});
			field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY, generator = \"" + sql + "\")");
		}
	} 
	
}
