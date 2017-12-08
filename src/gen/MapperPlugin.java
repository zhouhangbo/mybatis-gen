package gen;

/**
 * @Author: zhouhangbo
 * @Date: 2017/9/14
 * @Since: v1.0.0
 */
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.*;

public class MapperPlugin extends PluginAdapter {
    private Set<String> mappers = new HashSet();
    private boolean caseSensitive = false;
    private String beginningDelimiter = "";
    private String endingDelimiter = "";
    private String schema;
    private CommentGeneratorConfiguration commentCfg;
    private boolean forceAnnotation;

    public MapperPlugin() {
    }

    public void setContext(Context context) {
        super.setContext(context);
        this.commentCfg = new CommentGeneratorConfiguration();
        this.commentCfg.setConfigurationType(MapperCommentGenerator.class.getCanonicalName());
        context.setCommentGeneratorConfiguration(this.commentCfg);
        context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
    }

    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String mappers = this.properties.getProperty("mappers");
        if(!StringUtility.stringHasValue(mappers)) {
            throw new RuntimeException("Mapper插件缺少必要的mappers属性!");
        } else {
            String[] var3 = mappers.split(",");
            int var4 = var3.length;

            String endingDelimiter;
            for(int var5 = 0; var5 < var4; ++var5) {
                endingDelimiter = var3[var5];
                this.mappers.add(endingDelimiter);
            }

            String caseSensitive = this.properties.getProperty("caseSensitive");
            if(StringUtility.stringHasValue(caseSensitive)) {
                this.caseSensitive = caseSensitive.equalsIgnoreCase("TRUE");
            }

            String forceAnnotation = this.properties.getProperty("forceAnnotation");
            if(StringUtility.stringHasValue(forceAnnotation)) {
                this.commentCfg.addProperty("forceAnnotation", forceAnnotation);
                this.forceAnnotation = forceAnnotation.equalsIgnoreCase("TRUE");
            }

            String beginningDelimiter = this.properties.getProperty("beginningDelimiter");
            if(StringUtility.stringHasValue(beginningDelimiter)) {
                this.beginningDelimiter = beginningDelimiter;
            }

            this.commentCfg.addProperty("beginningDelimiter", this.beginningDelimiter);
            endingDelimiter = this.properties.getProperty("endingDelimiter");
            if(StringUtility.stringHasValue(endingDelimiter)) {
                this.endingDelimiter = endingDelimiter;
            }

            this.commentCfg.addProperty("endingDelimiter", this.endingDelimiter);
            String schema = this.properties.getProperty("schema");
            if(StringUtility.stringHasValue(schema)) {
                this.schema = schema;
            }

        }
    }

    public String getDelimiterName(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        if(StringUtility.stringHasValue(this.schema)) {
            nameBuilder.append(this.schema);
            nameBuilder.append(".");
        }

        nameBuilder.append(this.beginningDelimiter);
        nameBuilder.append(name);
        nameBuilder.append(this.endingDelimiter);
        return nameBuilder.toString();
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        Iterator var5 = this.mappers.iterator();

        while(var5.hasNext()) {
            String mapper = (String)var5.next();
            mapper = "com.winning.core.component";
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
        }

        interfaze.addAnnotation("@Repository");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
        interfaze.addImportedType(entityType);
        return true;
    }

    private void processEntityClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("javax.persistence.*");
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        if(StringUtility.stringContainsSpace(tableName)) {
            tableName = this.context.getBeginningDelimiter() + tableName + this.context.getEndingDelimiter();
        }

        if(this.caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
        } else if(!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
        } else if(!StringUtility.stringHasValue(this.schema) && !StringUtility.stringHasValue(this.beginningDelimiter) && !StringUtility.stringHasValue(this.endingDelimiter)) {
            if(this.forceAnnotation) {
                topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
            }
        } else {
            topLevelClass.addAnnotation("@Table(name = \"" + this.getDelimiterName(tableName) + "\")");
        }

    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.processEntityClass(topLevelClass, introspectedTable);
        return false;
    }

    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientInsertMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientSelectAllMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientSelectAllMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean providerApplyWhereMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean providerInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean providerUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }
}
