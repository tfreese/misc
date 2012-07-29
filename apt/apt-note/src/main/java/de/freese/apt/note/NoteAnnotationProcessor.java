/**
 * Created: 05.12.2011
 */

package de.freese.apt.note;

import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.swing.table.TableModel;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

/**
 * @author Thomas Freese
 */
@SupportedAnnotationTypes("de.freese.apt.note.Note")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class NoteAnnotationProcessor extends AbstractProcessor
{
	/**
	 * Erstellt ein neues {@link NoteAnnotationProcessor} Object.
	 */
	public NoteAnnotationProcessor()
	{
		super();
	}

	/**
	 * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set,
	 *      javax.annotation.processing.RoundEnvironment)
	 */
	@Override
	public boolean process(final Set<? extends TypeElement> annotations,
							final RoundEnvironment roundEnv)
	{
		try
		{
			JavaFileObject fileObject =
					this.processingEnv.getFiler().createSourceFile("test.Dummy");

			PrintWriter writer = new PrintWriter(fileObject.openWriter());
			writer.println("package test;");
			writer.println("");
			writer.println("/**");
			writer.println(" * @author Thomas Freese");
			writer.println(" */");
			writer.println("public class Dummy");
			writer.println("{");
			writer.println("	/**");
			writer.println("	* @see java.lang.Object#toString()");
			writer.println("	*/");
			writer.println("	@Override");
			writer.println("	public String toString()");
			writer.println("	{");
			writer.println("		return getClass().getSimpleName();");
			writer.println("	}");
			writer.println("}");

			writer.close();
		}
		catch (Exception ex)
		{
			this.processingEnv.getMessager().printMessage(Kind.ERROR, ex.getMessage(), null);
		}

		// Get all declarations that use the note annotation.
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Note.class);

		for (Element annotatedElement : annotatedElements)
		{
			TypeMirror typeMirror = annotatedElement.asType();

			if (!TableModel.class.getName().equals(typeMirror.toString()))
			{
				String message =
						String.format("Note is only allowed for fields of Type %s",
								TableModel.class.getName());

				// LoggerFactory.getLogger(getClass()).warn(message);
				// System.out.println(message);

				this.processingEnv.getMessager()
						.printMessage(Kind.ERROR, message, annotatedElement);
			}
		}

		return true;
	}

	// /**
	// * @param declaration {@link Declaration}
	// */
	// private void processNoteAnnotations(final Declaration declaration)
	// {
	// // Get all of the annotation usage for this declaration.
	// // the annotation mirror is a reflection of what is in the source.
	// Collection<AnnotationMirror> annotations = declaration.getAnnotationMirrors();
	//
	// // iterate over the mirrors.
	// for (AnnotationMirror mirror : annotations)
	// {
	// // if the mirror in this iteration is for our note declaration...
	// if (mirror.getAnnotationType().getDeclaration().equals(this.noteDeclaration))
	// {
	//
	// // print out the goodies.
	// SourcePosition position = mirror.getPosition();
	// Map<AnnotationTypeElementDeclaration, AnnotationValue> values =
	// mirror.getElementValues();
	//
	// System.out.println("Declaration: " + declaration.toString());
	// System.out.println("Position: " + position);
	// System.out.println("Values:");
	//
	// for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : values
	// .entrySet())
	// {
	// AnnotationTypeElementDeclaration elemDecl = entry.getKey();
	// AnnotationValue value = entry.getValue();
	// System.out.println("    " + elemDecl + "=" + value);
	// }
	// }
	// }
	// }
}
