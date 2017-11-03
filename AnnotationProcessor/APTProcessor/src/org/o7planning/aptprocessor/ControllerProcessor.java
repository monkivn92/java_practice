package org.o7planning.aptprocessor;
 
import java.util.List;
import java.util.Set;
 
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
 
import org.o7planning.log.DevLog;
 
// Có tác dụng với @Controller
 
@SupportedAnnotationTypes({ "org.o7planning.ann.Controller" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ControllerProcessor extends AbstractProcessor {
 
   private Filer filer;
   private Messager messager;
 
   @Override
   public void init(ProcessingEnvironment env) {
       filer = env.getFiler();
       messager = env.getMessager();
   }
 
   // annotations - là các Annotation chịu tác dụng của Processor này.
   @Override
   public boolean process(Set<? extends TypeElement> annotations,
           RoundEnvironment env) {
       DevLog.log("\n\n");
       DevLog.log(" ======================================================== ");
       DevLog.log("#process(...) in " + this.getClass().getSimpleName());
       DevLog.log(" ======================================================== ");
 
       for (TypeElement ann : annotations) {
           DevLog.log(" ==> TypeElement ann = " + ann);
           //
           List<? extends Element> es = ann.getEnclosedElements();
           DevLog.log(" ====> ann.getEnclosedElements() count = " + es.size());
           for (Element e : es) {
               DevLog.log(" ========> EnclosedElement: " + e);
           }
           Element enclosingElement = ann.getEnclosingElement();
 
           DevLog.log(" ====> ann.getEnclosingElement() = " + enclosingElement);
 
           ElementKind kind = ann.getKind();
           DevLog.log(" ====> ann.getKind() = " + kind);
           Set<? extends Element> e2s = env.getElementsAnnotatedWith(ann);
 
           DevLog.log(" ====> env.getElementsAnnotatedWith(ann) count = "
                   + e2s.size());
           for (Element e2 : e2s) {
               DevLog.log(" ========> ElementsAnnotatedWith: " + e2);
               DevLog.log("           - Kind : " + e2.getKind());
 
               // @Controller chỉ dùng cho class!
               // Thông báo nếu sử dụng sai.
               if (e2.getKind() != ElementKind.CLASS) {
                   DevLog.log("           - Error!!!");
                   messager.printMessage(Kind.ERROR,
                           "@Controller using for class only ", e2);
               } else {
                   // Tên class sử dụng @Controller
 
                   String className = e2.getSimpleName().toString();
                   // @Controller chỉ áp dụng cho class có đuôi là Controller
                   // Thông báo nếu sử dụng sai.
 
                   if (!className.endsWith("Controller")) {
                       DevLog.log("           - Error!!!");
                       messager.printMessage(
                               Kind.ERROR,
                               "Class using @Controller must have suffix Controller",
                               e2);
                   }
               }
           }
 
       }
 
 
       return true;
   }
 
}