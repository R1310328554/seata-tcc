package io.seata.samples.account.config;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/** 
 * 刷新MyBatis Mapper XML 线程 
 * @author LuoKai 
 * @version 2020-10-2
 */
@Slf4j
@Component
@Profile("dev")
public class MapperRefresh implements ApplicationContextAware,InitializingBean {
  
    private static String filename = "/mybatis-refresh.properties";
    private static Properties prop = new Properties();
  
    private static boolean enabled;       // 是否启用Mapper刷新线程功能  
    private static boolean refresh;       // 刷新启用后，是否启动了刷新线程  
      
    private Set<File> location;       // Mapper实际资源路径

    private Resource[] mapperLocations;   // Mapper资源路径  
    private Configuration configuration;      // MyBatis配置对象  
      
    private Long beforeTime = 0L;         // 上一次刷新时间  
    private static int delaySeconds;      // 延迟刷新秒数  
    private static int sleepSeconds;      // 休眠时间  
    private static String mappingPath;    // xml文件夹匹配字符串，需要根据需要修改  

    private final static String OPEN = "1";
    private volatile SqlSessionFactoryBean sqlSessionFactoryBean;

    static {
        try {  
            prop.load(MapperRefresh.class.getResourceAsStream(filename));
        } catch (Exception e) {  
//            e.printStackTrace();
            System.err.println("Load mybatis-refresh “"+filename+"” file error.");
        }
        delaySeconds = getPropInt("delaySeconds");
        sleepSeconds = getPropInt("sleepSeconds");
        mappingPath = getPropString("mappingPath");
        if (getPropString("enabled") == null) {
            String property = System.getProperty("MapperRefresh.enabled");
            if (property != null) {
                enabled = Boolean.parseBoolean(getPropString(property));
            }
        } else {
            enabled = getPropBoolean("enabled");
        }

        delaySeconds = delaySeconds == 0 ? 10 : delaySeconds;
        sleepSeconds = sleepSeconds == 0 ? 5 : sleepSeconds;
        mappingPath = StringUtils.isBlank(mappingPath) ? "mappings" : mappingPath;
  
        log.debug("[enabled] " + enabled);
        log.debug("[delaySeconds] " + delaySeconds);
        log.debug("[sleepSeconds] " + sleepSeconds);
        log.debug("[mappingPath] " + mappingPath);
    }
  
    public static boolean isRefresh() {  
        return refresh;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        sqlSessionFactoryBean = applicationContext.getBean(SqlSessionFactoryBean.class);
        SqlSessionFactory sqlSessionFactory = null;
        try {
            sqlSessionFactory = sqlSessionFactoryBean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        configuration = sqlSessionFactory.getConfiguration();

        MapperRegistry mapperRegistry = configuration.getMapperRegistry();
        Collection<Class<?>> mappers = mapperRegistry.getMappers();
        for (Iterator<Class<?>> iterator = mappers.iterator(); iterator.hasNext(); ) {
            Class<?> next =  iterator.next();
            System.out.println("next = " + next);
        }

        System.out.println("sqlSessionFactory = " + sqlSessionFactory);
        System.out.println("configuration = " + configuration);
        this.mapperLocations = getResource();
    }

    /** 
     * 执行刷新 
     * @param xmlFile 刷新目录
     * @param beforeTime 上次刷新时间 
     * @throws NestedIOException 解析异常 
     * @throws FileNotFoundException 文件未找到 
     * @author LuoKai 
     */  
    @SuppressWarnings({ "rawtypes", "unchecked" })  
    private void refresh(File xmlFile, Long beforeTime) throws Exception {
  
        // 本次刷新时间  
        Long refrehTime = System.currentTimeMillis();
  
        // 获取需要刷新的Mapper文件列表  
        List<File> fileList = this.getRefreshFile(xmlFile, beforeTime);
        if (fileList.size() > 0) {  
            log.debug("Refresh file: " + fileList.size());
        }
        for (int i = 0; i < fileList.size(); i++) {  
            InputStream inputStream = new FileInputStream(fileList.get(i));
            String resource = fileList.get(i).getAbsolutePath();
            try {  
                  
                // 清理原有资源，更新为自己的StrictMap方便，增量重新加载  
                String[] mapFieldNames = new String[]{  
                    "mappedStatements", "caches",  
                    "resultMaps", "parameterMaps",  
                    "keyGenerators", "sqlFragments"  
                };
                for (String fieldName : mapFieldNames){  
                    Field field = configuration.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Map map = ((Map)field.get(configuration));
                    if (!(map instanceof StrictMap)){  
                        Map newMap = new StrictMap(StringUtils.capitalize(fieldName) + "collection");
                        for (Object key : map.keySet()){  
                            try {  
                                newMap.put(key, map.get(key));
                            }catch(IllegalArgumentException ex){  
                                newMap.put(key, ex.getMessage());
                            }
                        }
                        field.set(configuration, newMap);
                    }
                }
                  
                // 清理已加载的资源标识，方便让它重新加载。  
                Field loadedResourcesField = configuration.getClass().getDeclaredField("loadedResources");
                loadedResourcesField.setAccessible(true);
                Set loadedResourcesSet = ((Set)loadedResourcesField.get(configuration));
                loadedResourcesSet.remove(resource);
                  
                //重新编译加载资源文件。  
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(inputStream, configuration,   
                        resource, configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            } catch (Exception e) {  
                throw new NestedIOException("Failed to parse mapping resource: '" + resource + "'", e);
            } finally {  
                ErrorContext.instance().reset();
            }
            System.out.println("Refresh file: " + mappingPath + StringUtils.substringAfterLast(fileList.get(i).getAbsolutePath(), mappingPath));
            if (log.isDebugEnabled()) {  
                log.debug("Refresh file: " + fileList.get(i).getAbsolutePath());
                log.debug("Refresh filename: " + fileList.get(i).getName());
            }
        }
        // 如果刷新了文件，则修改刷新时间，否则不修改  
        if (fileList.size() > 0) {  
            this.beforeTime = refrehTime;
        }
    }
      
    /** 
     * 获取需要刷新的文件列表 
     * @param xmlFile 目录
     * @param beforeTime 上次刷新时间 
     * @return 刷新文件列表 
     */  
    private List<File> getRefreshFile(File xmlFile, Long beforeTime) {
        List<File> fileList = new ArrayList<File>();
        if (xmlFile.isFile()) {
            if (this.checkFile(xmlFile, beforeTime)) {
                fileList.add(xmlFile);
            }
            return fileList;
        }
        File[] files = xmlFile.listFiles();
        if (files != null) {  
            for (int i = 0; i < files.length; i++) {  
                File file = files[i];
                if (file.isDirectory()) {  
                    fileList.addAll(this.getRefreshFile(file, beforeTime));
                } else if (file.isFile()) {  
                    if (this.checkFile(file, beforeTime)) {  
                        fileList.add(file);
                    }
                } else {  
                    System.out.println("Error file." + file.getName());
                }
            }
        }
        return fileList;
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
     *  获取对象指定属性
     *  @param obj 对象信息
     *  @param fieldName 属性名称
     *  @return java.lang.Object
     *  @date                    ：2018/12/19
     *  @author                  ：zc.ding@foxmail.com
     */
    private Object getFieldValue(Object obj, String fieldName){
        log.info("从{}中加载{}属性", obj, fieldName);
        try{
            Field field = obj.getClass().getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object value = field.get(obj);
            field.setAccessible(accessible);
            return value;
        }catch(Exception e){
            log.info("ERROR: 加载对象中[{}]", fieldName, e);
            throw new RuntimeException("ERROR: 加载对象中[" + fieldName + "]", e);
        }
    }

    /** 
     * 判断文件是否需要刷新 
     * @param file 文件 
     * @param beforeTime 上次刷新时间 
     * @return 需要刷新返回true，否则返回false 
     */  
    private boolean checkFile(File file, Long beforeTime) {  
        if (file.lastModified() > beforeTime) {  
            return true;
        }
        return false;
    }

    /** 
     * 获取整数属性 
     * @param key
     * @return
     */  
    private static boolean getPropBoolean(String key) {
        boolean i = false;
        try {  
            i = Boolean.parseBoolean(getPropString(key));
        } catch (Exception e) {  
        }
        return i;
    }
    /**
     * 获取整数属性
     * @param key
     * @return
     */
    private static int getPropInt(String key) {
        int i = 0;
        try {
            i = Integer.parseInt(getPropString(key));
        } catch (Exception e) {
        }
        return i;
    }
  
    /** 
     * 获取字符串属性 
     * @param key 
     * @return 
     */  
    private static String getPropString(String key) {  
        return prop == null ? null : prop.getProperty(key);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if(enabled){
            beforeTime = System.currentTimeMillis();
            log.debug("[location] " + location);
            log.debug("[configuration] " + configuration);
            // 启动刷新线程
            final MapperRefresh runnable = this;
            new Thread(new java.lang.Runnable() {
                @Override
                public void run() {
                    if (location == null){
                        location = Sets.newHashSet();
                        log.debug("MapperLocation's length:" + mapperLocations.length);
                        for (Resource mapperLocation : mapperLocations) {
                            File file = null;
                            try {
                                file = mapperLocation.getFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                            if (!location.contains(file)) {
                                location.add(file);
                                log.debug("Location:" + file);
                            }
                        }
                        log.debug("Locarion's size:" + location.size());
                    }

                    try {
                        Thread.sleep(delaySeconds * 1000);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    refresh = true;

                    System.out.println("========= Enabled refresh mybatis mapper =========");

                    while (true) {
                        try {
                            for (File s : location) {
                                runnable.refresh(s, beforeTime);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        try {
                            Thread.sleep(sleepSeconds * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }, "MyBatis-Mapper-Refresh").start();
        }
    }

    /** 
     * 重写 org.apache.ibatis.session.Configuration.StrictMap 类 
     * 来自 MyBatis3.4.0版本，修改 put 方法，允许反复 put更新。 
     */  
    public static class StrictMap<V> extends HashMap<String, V> {  
  
        private static final long serialVersionUID = -4950446264854982944L;
        private String name;
  
        public StrictMap(String name, int initialCapacity, float loadFactor) {  
            super(initialCapacity, loadFactor);
            this.name = name;
        }
  
        public StrictMap(String name, int initialCapacity) {  
            super(initialCapacity);
            this.name = name;
        }
  
        public StrictMap(String name) {  
            super();
            this.name = name;
        }
  
        public StrictMap(String name, Map<String, ? extends V> m) {  
            super(m);
            this.name = name;
        }
  
        @SuppressWarnings("unchecked")  
        public V put(String key, V value) {  
            // LuoKai 如果现在状态为刷新，则刷新(先删除后添加)  
            if (MapperRefresh.isRefresh()) {  
                remove(key);
                MapperRefresh.log.debug("refresh key:" + key.substring(key.lastIndexOf(".") + 1));
            }
            // LuoKai end  
            if (containsKey(key)) {  
                throw new IllegalArgumentException(name + " already contains value for " + key);
            }
            if (key.contains(".")) {  
                final String shortKey = getShortName(key);
                if (super.get(shortKey) == null) {  
                    super.put(shortKey, value);
                } else {  
                    super.put(shortKey, (V) new Ambiguity(shortKey));
                }
            }
            return super.put(key, value);
        }
  
        public V get(Object key) {  
            V value = super.get(key);
            if (value == null) {  
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }
            if (value instanceof Ambiguity) {  
                throw new IllegalArgumentException(((Ambiguity) value).getSubject() + " is ambiguous in " + name  
                        + " (try using the full name including the namespace, or rename one of the entries)");
            }
            return value;
        }
  
        private String getShortName(String key) {  
            final String[] keyparts = key.split("\\.");
            return keyparts[keyparts.length - 1];
        }
  
        protected static class Ambiguity {  
            private String subject;
  
            public Ambiguity(String subject) {  
                this.subject = subject;
            }
  
            public String getSubject() {  
                return subject;
            }
        }
    }
}