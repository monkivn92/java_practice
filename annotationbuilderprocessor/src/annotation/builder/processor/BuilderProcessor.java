package annotation.builder.processor;

import java.io.IOException;
import java.io.PrintWriter;


import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.LinkedHashSet;

import java.util.stream.Collectors;
import javax.annotation.processing.*;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic;
import java.nio.file.*;

public class BuilderProcessor extends AbstractProcessor
{
    private Messager messager;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        messager.printMessage(Diagnostic.Kind.WARNING,"Start processor");
        for(TypeElement anno : annotations)
        {
            Set <? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(anno);

            //only works with setter function
            Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream().collect(
                    Collectors.partitioningBy(
                            element -> ((ExecutableType) element.asType()).getParameterTypes().size() == 1 && element.getSimpleName().toString().startsWith("set")
                    )
            );

            List<Element> setters = annotatedMethods.get(true);
            List<Element> otherfunc = annotatedMethods.get(false);
            if (setters.isEmpty())
            {
                continue;
            }

            messager.printMessage(Diagnostic.Kind.WARNING,"Found setter functions" + setters.size());
            String className = ( (TypeElement) setters.get(0).getEnclosingElement() )
                                                                    .getQualifiedName().toString();

            //function and its params
            Map<String, String> setterMap = setters.stream().collect(
                    Collectors.toMap(
                            setter -> setter.getSimpleName().toString(), setter -> ((ExecutableType) setter.asType()).getParameterTypes().get(0).toString()
                    )
            );

            try
            {

                writeBuilderFile(className, setterMap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        return true;
    }

    private void writeBuilderFile(String className, Map<String, String> setterMap) throws IOException
    {

        String packageName = null;
        messager.printMessage(Diagnostic.Kind.WARNING,"Writing file");
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0)
        {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile( builderSimpleClassName );
        String genfile = builderFile.toUri().getPath();
        String desfile = "/media/DataBackup/java_practice/annotationbuideruser/src/PersonBuilderSrc.java";
        messager.printMessage(Diagnostic.Kind.WARNING, genfile);
        try (PrintWriter out = new PrintWriter( builderFile.openWriter() ))
        {

            if (packageName != null)
            {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("public class ");
            out.print(builderSimpleClassName);
            out.println(" {");
            out.println();

            out.print("    private ");
            out.print(simpleClassName);
            out.print(" object = new ");
            out.print(simpleClassName);
            out.println("();");
            out.println();

            out.print("    public ");
            out.print(simpleClassName);
            out.println(" build() {");
            out.println("        return object;");
            out.println("    }");
            out.println();

            setterMap.entrySet().forEach(setter -> {
                String methodName = setter.getKey();
                String argumentType = setter.getValue();

                out.print("    public ");
                out.print(builderSimpleClassName);
                out.print(" ");
                out.print(methodName);

                out.print("(");

                out.print(argumentType);
                out.println(" value) {");
                out.print("        object.");
                out.print(methodName);
                out.println("(value);");
                out.println("        return this;");
                out.println("    }");
                out.println();
            });

            out.println("}");

        }

        //Files.copy(Paths.get(genfile), Paths.get(desfile), StandardCopyOption.REPLACE_EXISTING);

    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(BuilderProperty.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.RELEASE_8;
    }



}
