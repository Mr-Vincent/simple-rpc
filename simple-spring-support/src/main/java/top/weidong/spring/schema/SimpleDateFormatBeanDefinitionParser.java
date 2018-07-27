package top.weidong.spring.schema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/07/27
 * Time: 14:43
 */
public class SimpleDateFormatBeanDefinitionParser extends AbstractSingleBeanDefinitionParser{

    @Override
    protected Class<?> getBeanClass(Element element) {
        return SimpleDateFormat.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {

        String pattern = element.getAttribute("pattern");
        builder.addConstructorArgValue(pattern);

        String lenient = element.getAttribute("lenient");
        if(StringUtils.hasText(lenient)) {
            builder.addPropertyValue("lenient",Boolean.valueOf(lenient));
        }
    }
}
