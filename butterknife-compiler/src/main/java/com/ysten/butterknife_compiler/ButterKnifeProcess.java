package com.ysten.butterknife_compiler;

import com.google.auto.service.AutoService;
import com.ysten.butterknife_annotations.BindView;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

/**
 * desc    : 监听者、盯着app中的注解
 * time    : 2018/6/7 0007 14:44
 * //需要让我们这个类有注解处理器的能力 需要绑定
 * @author : wangjitao
 */
@AutoService(Processor.class)
public class ButterKnifeProcess extends AbstractProcessor{

    /**
     * 1,确认我们处理那些注解
     * 2，确认使用哪种jdk版本
     * 3，用来生成java文件的对象
     */
    private Filer filer ;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler() ;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(BindView.class);

        //前面存activity名字,后面使用list存id
        Map<String ,List<VariableElement>> cache = new HashMap<>();
        for(Element element :elementSet){
            VariableElement variableElement = (VariableElement) element ;
            String activityName = getActivityName(variableElement);
            List<VariableElement> list = cache.get(activityName);
            if (list == null){
                list = new ArrayList<>();
                cache.put(activityName,list);
            }
            list.add(variableElement);
        }

        //产生java文件
        Iterator iterable = cache.keySet().iterator();
        while (iterable.hasNext()){
            String activity = (String) iterable.next() ;
            List<VariableElement> variableElements = cache.get(activity);
            String packageName = getPackageName(variableElements.get(0));
            //获取最后生成的文件的文件名
            String newActivity = activity+"$ViewBinder";

            //生成额外的文件(写java文件)
            Writer writer = null ;
            JavaFileObject javaFileObject = null;
            try {
                javaFileObject = filer.createSourceFile(newActivity);
                writer = javaFileObject.openWriter() ;
                String activitySimpleName = variableElements.get(0).getEnclosingElement().getSimpleName().toString()+"$ViewBinder";
                writer.write("package "+packageName+" ;");
                writer.write("\n");
                writer.write("import "+packageName+".ViewBinder;");
                writer.write("\n");
                writer.write("public class " + activitySimpleName+ " implements ViewBinder<"+activity+">{");
                writer.write("\n");
                writer.write("public void bind("+activity+" target ){");
                writer.write("\n");
                for (VariableElement variableElement :variableElements){
                    BindView bindView = variableElement.getAnnotation(BindView.class);

                    String filedName = variableElement.getSimpleName().toString();
                    TypeMirror typeMirror = variableElement.asType() ;

                    writer.write("target."+filedName+"=("+typeMirror.toString()+")"+"target.findViewById("+bindView.value()+");");
                    writer.write("\n");
                }
                writer.write("}");
                writer.write("\n");
                writer.write("}");
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 获取包名
     * @param variableElement
     * @return
     */
    private String getPackageName(VariableElement variableElement) {
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement() ;
        String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        return packageName ;
    }

    /**
     * 获取类名
     * @param variableElement
     * @return
     */
    private String getActivityName(VariableElement variableElement) {
        String packageName  = getPackageName(variableElement);
        TypeElement typeElement =  (TypeElement) variableElement.getEnclosingElement() ;
        return  packageName +"."+typeElement.getSimpleName().toString();
    }

}
