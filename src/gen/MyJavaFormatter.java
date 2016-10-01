package gen;

import java.util.List;

import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.TableConfiguration;

import com.mysql.jdbc.StringUtils;
/**
 * 功能（API）表对应的实体
 *
 * @author yangw
 * @since 1.0.0
 */
public class MyJavaFormatter implements JavaFormatter {
    protected Context context;
 
    public String getFormattedContent(CompilationUnit compilationUnit) {
    	if(compilationUnit instanceof TopLevelClass){
    		TopLevelClass top = (TopLevelClass) compilationUnit;
    		
//    		List<Method> methods = top.getMethods();
    		//同理对方法或属性添加注解
//    		methods.get(0).addAnnotation("");
//    		fields.get(0).addAnnotation("");
    		
    		//添加类注解
    		genTopClassAnnotation(top);
    		
    		//实现可序列化
    		genTopClassSerializable(top);
    		
    		//添加类注解
    		top.addFileCommentLine("/**");
    		top.addFileCommentLine(" * 功能:");
    		top.addFileCommentLine(" * ");
    		top.addFileCommentLine(" * @author zhouhb ");
    		top.addFileCommentLine(" * @since 1.0.0");
    		top.addFileCommentLine(" */");
    	} 
        return compilationUnit.getFormattedContent();
    }

    public void setContext(Context context) {
        this.context = context;
    }
    
    private void genTopClassAnnotation(TopLevelClass top){
    	for (TableConfiguration config : context.getTableConfigurations()) {
			if(!top.getType().getShortName().equals(config.getDomainObjectName())){
				continue;
			}
    		String alias = config.getAlias();
    		String schema = config.getSchema();
    		String tableName = config.getTableName();
    		StringBuilder sb = new StringBuilder("@Table(");
    		boolean hasTable = false;
    		if(!StringUtils.isNullOrEmpty(schema)){
    			sb.append("schema=\"" + schema + "\"");
    			hasTable = true;
    		}
    		if(!StringUtils.isNullOrEmpty(tableName)){
    			if(hasTable){
    				sb.append(", ");
    			}else{
    				hasTable = true;
    			}
    			sb.append("name=\"" + tableName.toLowerCase() + "\"");
    		}
    		
    		if(hasTable){
    			sb.append(")");
    			top.addAnnotation(sb.toString());
    			top.addImportedType(new FullyQualifiedJavaType("javax.persistence.Table"));
    		}
    		if(!StringUtils.isNullOrEmpty(alias)){
    			top.addAnnotation("@Alias(\"" + alias + "\")");
    			top.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.type.Alias"));
    		}

		}
    }
    
    private void genTopClassSerializable(TopLevelClass top){
    	List<Field> fields = top.getFields();
    	top.addImportedType(new FullyQualifiedJavaType("java.io.Serializable"));
		top.addSuperInterface(new FullyQualifiedJavaType("java.io.Serializable"));
		Field field = new Field("serialVersionUID", new FullyQualifiedJavaType("java.lang.long"));
		field.setStatic(true);
		field.setFinal(true);
		field.addJavaDocLine("/*静态字段自动排除，不予表字段映射*/");
		field.setInitializationString("1L");
		field.setVisibility(JavaVisibility.PRIVATE);
		fields.add(0, field);
    }
}
