package io.seata.samples.account.config;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
 
/**
*  mapper.xml热部署，最小单位是一个xml文件
*  @date                    ：2018/12/20
*  @author                  ：zc.ding@foxmail.com
*/
public class MapperHotDeployPlugin implements InitializingBean, ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(MapperHotDeployPlugin.class);
    private final static String OPEN = "1";
    private volatile SqlSessionFactoryBean sqlSessionFactoryBean;
    private volatile Configuration configuration;

    @Value("${mapper.hot.deploy:true}")
    private boolean enable;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        logger.info("Mybatis热部署标识mapper.hot.deploy={}", enable);
        // 判断是否开启了热部署
        if(enable){
            new WatchThread().start();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
//        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) applicationContext.getBean("sqlSessionFactory");
//        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        sqlSessionFactoryBean = applicationContext.getBean(SqlSessionFactoryBean.class);
        SqlSessionFactory sqlSessionFactory = null;
        try {
            sqlSessionFactory = sqlSessionFactoryBean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
          configuration = sqlSessionFactory.getConfiguration();
        System.out.println("sqlSessionFactory = " + sqlSessionFactory);
        System.out.println("configuration = " + configuration);
    }
    
    class WatchThread extends Thread{
        private final Logger logger = LoggerFactory.getLogger(WatchThread.class);
        @Override
        public void run() {
            startWatch();
        }
        /**
         *  启动监听
         *  @date                    ：2018/12/19
         *  @author                  ：zc.ding@foxmail.com
         */
        private void startWatch(){
            try{
                WatchService watcher = FileSystems.getDefault().newWatchService();
                getWatchPaths().forEach(p -> {
                    try {
                        Paths.get(p).register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
                    } catch (Exception e) {
                        logger.error("ERROR: 注册xml监听事件", e);
                        throw new RuntimeException("ERROR: 注册xml监听事件", e);
                    }
                });
                while (true) {
                    WatchKey watchKey = watcher.take();
                    Set<String> set = new HashSet<>();
                    for (WatchEvent<?> event: watchKey.pollEvents()) {
                        set.add(event.context().toString());
                    }
                    // 重新加载xml
                    reloadXml(set);
                    boolean valid = watchKey.reset();
                    if (!valid) {
                        break;
                    }
                }
            }catch(Exception e){
                System.out.println("Mybatis的xml监控失败!");
                logger.info("Mybatis的xml监控失败!", e);
            }
        }
        /**
         *  加载需要监控的文件父路径
         *  @return java.util.Set<java.lang.String>
         *  @date                    ：2018/12/19
         *  @author                  ：zc.ding@foxmail.com
         */
        private Set<String> getWatchPaths(){
            Set<String> set = new HashSet<>();
            Arrays.stream(getResource()).forEach(r -> {
                try{
                    logger.info("资源路径:{}", r.toString());
                    set.add(r.getFile().getParentFile().getAbsolutePath());
                }catch(Exception e){
                    logger.info("获取资源路径失败", e);
                    throw new RuntimeException("获取资源路径失败");
                }
            });
            logger.info("需要监听的xml资源: {}", set);
            return set;
        }
        /**
         *  获取配置的mapperLocations
         *  @return org.springframework.core.io.Resource[]
         *  @date                    ：2018/12/19
         *  @author                  ：zc.ding@foxmail.com
         */
        private Resource[] getResource(){
            return (Resource[]) getFieldValue(sqlSessionFactoryBean, "mapperLocations");
        }
        /**
         *  删除xml元素的节点缓存
         *  @param nameSpace xml中命名空间
         *  @date                    ：2018/12/19
         *  @author                  ：zc.ding@foxmail.com
         */
        private void clearMap(String nameSpace) {
            logger.info("清理Mybatis的namespace={}在mappedStatements、caches、resultMaps、parameterMaps、keyGenerators、sqlFragments中的缓存");
            Arrays.asList("mappedStatements", "caches", "resultMaps", "parameterMaps", "keyGenerators", "sqlFragments").forEach(fieldName -> {
                Object value = getFieldValue(configuration, fieldName);
                if (value instanceof Map) {
                    Map<?, ?> map = (Map)value;
                    List<Object> list = map.keySet().stream().filter(o -> o.toString().startsWith(nameSpace + ".")).collect(Collectors.toList());
                    logger.info("需要清理的元素: {}", list);
                    list.forEach(k -> map.remove((Object)k));
                }
            });
        }
        /**
         *  清除文件记录缓存
         *  @param resource xml文件路径
         *  @date                    ：2018/12/19
         *  @author                  ：zc.ding@foxmail.com
         */
        private void clearSet(String resource) {
            logger.info("清理mybatis的资源{}在容器中的缓存", resource);
            Object value = getFieldValue(configuration, "loadedResources");
            if (value instanceof Set) {
                Set<?> set = (Set)value;
                set.remove(resource);
                set.remove("namespace:" + resource);
            }
        }
        /**
         *  获取对象指定属性
         *  @param obj 对象信息
         *  @param fieldName 属性名称
         *  @return java.lang.Object
         *  @date                    ：2018/12/19
         *  @author                  ：zc.ding@foxmail.com
         */
        private Object getFieldValue(Object obj, String fieldName){
            logger.info("从{}中加载{}属性", obj, fieldName);
            try{
                Field field = obj.getClass().getDeclaredField(fieldName);
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Object value = field.get(obj);
                field.setAccessible(accessible);
                return value;
            }catch(Exception e){
                logger.info("ERROR: 加载对象中[{}]", fieldName, e);
                throw new RuntimeException("ERROR: 加载对象中[" + fieldName + "]", e);
            }
        }
        /**
         *  重新加载set中xml
         *  @param set 修改的xml资源
         *  @date                    ：2018/12/19
         *  @author                  ：zc.ding@foxmail.com
         */
        private void reloadXml(Set<String> set){
            logger.info("需要重新加载的文件列表: {}", set);
            List<Resource> list = Arrays.stream(getResource())
                    .filter(p -> set.contains(p.getFilename()))
                    .collect(Collectors.toList());
            logger.info("需要处理的资源路径:{}", list);
            list.forEach(r ->{
                try{
                    clearMap(getNamespace(r));
                    clearSet(r.toString());
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(r.getInputStream(), configuration,
                            r.toString(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                }catch(Exception e){
                    logger.info("ERROR: 重新加载[{}]失败", r.toString(), e);
                    throw new RuntimeException("ERROR: 重新加载[" + r.toString() + "]失败", e);
                }finally {
                    ErrorContext.instance().reset();
                }
            });
            logger.info("成功热部署文件列表: {}", set);
        }
        /**
         *  获取xml的namespace
         *  @param resource xml资源
         *  @return java.lang.String
         *  @date                    ：2018/12/19
         *  @author                  ：zc.ding@foxmail.com
         */
        private String getNamespace(Resource resource){
            logger.info("从{}获取namespace", resource.toString());
            try{
                XPathParser parser = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver());
                return parser.evalNode("/mapper").getStringAttribute("namespace");
            }catch(Exception e){
                logger.info("ERROR: 解析xml中namespace失败", e);
                throw new RuntimeException("ERROR: 解析xml中namespace失败", e);
            }
        }
    }
}