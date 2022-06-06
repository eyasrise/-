package com.eyas.springfactory.boot.spring;

import com.eyas.springfactory.boot.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpringApplication {


    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();


    public SpringApplication(Class clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        // 扫描包路径得到classList
        List<String> classNameList = this.componentScan(clazz);
        System.out.println("classNameList------>" + classNameList);
        // 通过路径装载bean
        List<Class> beanClasses = this.genBeanClasses(classNameList, classLoader);
        System.out.println("beanClasses------>" + beanClasses);
        // 通过class构建Component注解的beanDefinition，并把beanDefinition以键值对方式到map中
        this.createBeanDefinition(beanClasses);
        System.out.println("beanDefinitionMap--->" + beanDefinitionMap);
        // 构建bean
        // 构建单例池
        this.singletonBeanPool();
    }

    private List<String> componentScan(Class clazz){
        // 构建对象
        // 判断启动类是否有扫描入口方法，如果有开始创建对象
        System.out.println("有ComponentScan注解扫描");
        // 先得到包路径
        ComponentScan componentScanAnnotation = (ComponentScan) clazz.getAnnotation(ComponentScan.class);
        // 获得ComponentScan的包路径，用来创建beanDefinition
        String packagePath = componentScanAnnotation.value();
        // 获取文件包的路径
        System.out.println(packagePath);

        ClassLoader classLoader = clazz.getClassLoader();
        packagePath = packagePath.replace(".", "/");
        URL resource = classLoader.getResource(packagePath);
        List<String> classNameList = new ArrayList<>();
        if (null == resource){
            return classNameList;
        }
        File file = new File(resource.getFile());
        System.out.println("----------");
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            // 判空
            if (null == files){
                return classNameList;
            }
            // -todo-使用递归来处理文件
            for (File f : files) {
                if (f.isDirectory()) {
                    File[] files2 = f.listFiles();
                    if (null == files2){
                        continue;
                    }
                    for (File f1 : files2) {
                        String classPathName = getClassPathName(f1);
                        if (null != classPathName){
                            classNameList.add(classPathName);
                        }
                    }
                } else {
                    String classPathName = getClassPathName(f);
                    if (null != classPathName){
                        classNameList.add(classPathName);
                    }
                }
            }
        }
        return classNameList;
    }

    /**
     * 获取类路径名称
     *
     * @param file 文件
     * @return String
     */
    private String getClassPathName(File file){
        String fileName = file.getAbsolutePath();
        System.out.println(fileName);
        if (fileName.endsWith(".class")) {
            String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
            className = className.replace("/", ".");
            return className;
        }
        return null;
    }

    private List<Class> genBeanClasses(List<String> packagePathList, ClassLoader classLoader){
        // 通过路径装在bean
        List<Class> beanClasses = new ArrayList<>();
        packagePathList.forEach(s -> {
            try {
                Class<?> clazz2 = classLoader.loadClass(s);
                beanClasses.add(clazz2);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return beanClasses;
    }

    private void createBeanDefinition(List<Class> beanClassList){
        beanClassList.forEach(clazz->{
            BeanDefinition beanDefinition = new BeanDefinition();
            if (clazz.isAnnotationPresent(Component.class)){
                // 创建对象
                beanDefinition.setBeanClass(clazz);
                // 要么Spring自动生成，要么从Component注解上获取
                Component component = (Component) clazz.getAnnotation(Component.class);
                String beanName = component.value();
                if ("".equals(beanName)){
                    // 如果是空，使用class的
                    beanName = StringUtils.lowerFirst(clazz.getSimpleName());
                }

                // 解析scope--只正对非懒加载的单例创建对象
                if (clazz.isAnnotationPresent(Scope.class)){
                    Scope scope = (Scope) clazz.getAnnotation(Scope.class);
                    // 获取
                    if (ScopeEnum.singleton.equals(scope.value())){
                        // 如果是单例
                        beanDefinition.setScope(ScopeEnum.singleton);
                    }else {
                        beanDefinition.setScope(ScopeEnum.prototype);
                    }
                }else {
                    beanDefinition.setScope(ScopeEnum.singleton);
                }
                beanDefinitionMap.put(beanName, beanDefinition);
            }
        });
    }

    private void singletonBeanPool(){
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            if (ScopeEnum.singleton.equals(beanDefinition.getScope())){
                // 如果是单例
                Object object = this.doCreateBean(beanDefinition);
                if (null != object) {
                    singletonObjects.put(beanName, object);
                }
            }
        });
    }



    private Object getBean(String beanName){
        // 获取对象
        if (singletonObjects.containsKey(beanName)){
            // 如果存在直接返回
            return singletonObjects.get(beanName);
        }else{
            // 创建bean
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            // 创建bean
            return this.doCreateBean(beanDefinition);
        }
    }

    public Object getBean(Class clazz){
        // 获取对象
        String beanName = this.getComponentName(clazz);
        return this.getBean(beanName);
    }

    private Object doCreateBean(BeanDefinition beanDefinition){
        // 创建对象
        Class beanClass = beanDefinition.getBeanClass();

        try {
            // 实例化
            Constructor[] constructorList = beanClass.getDeclaredConstructors();
            // 创建对象
            Constructor constructor = null;
            if (constructorList.length > 0){
                constructor = constructorList[0];
            }
            if (null == constructor){
                return null;
            }
            Object instance = constructor.newInstance();
            // 填充属性
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                // 如果有注解Autowired
                if (field.isAnnotationPresent(Autowired.class)){
                    Class fieldName = field.getType();
                    Object object = this.getBean(fieldName);
                    field.setAccessible(true);
                    field.set(instance, object);
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;

    }

    // 获取注解名字
    private String getComponentName(Class clazz){
        // 要么Spring自动生成，要么从Component注解上获取
        Component component = (Component) clazz.getAnnotation(Component.class);
        String beanName = component.value();
        if ("".equals(beanName)){
            // 如果是空，使用class的
            beanName = StringUtils.lowerFirst(clazz.getSimpleName());
        }
        return beanName;
    }

}
