/**
 * Created: 05.12.2011
 */

package de.freese.apt.note;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

/**
 * @author Thomas Freese
 */
public class NoteAnnotationProcessorFactory implements AnnotationProcessorFactory
{
	/**
	 * Erstellt ein neues {@link NoteAnnotationProcessorFactory} Object.
	 */
	public NoteAnnotationProcessorFactory()
	{
		super();
	}

	/**
	 * Returns a note annotation processor.
	 * 
	 * @return An annotation processor for note annotations if requested, otherwise, returns the
	 *         NO_OP annotation processor.
	 * @see com.sun.mirror.apt.AnnotationProcessorFactory#getProcessorFor(java.util.Set,
	 *      com.sun.mirror.apt.AnnotationProcessorEnvironment)
	 */
	@Override
	public AnnotationProcessor getProcessorFor(final Set<AnnotationTypeDeclaration> declarations,
												final AnnotationProcessorEnvironment env)
	{
		AnnotationProcessor result = null;

		if (declarations.isEmpty())
		{
			result = AnnotationProcessors.NO_OP;
		}
		else
		{
			// Next Step - implement this class:
			// result = new NoteAnnotationProcessor();// (env);
		}

		return result;
	}

	/**
	 * @see com.sun.mirror.apt.AnnotationProcessorFactory#supportedAnnotationTypes()
	 */
	@Override
	public Collection<String> supportedAnnotationTypes()
	{
		return Collections.singletonList("de.freese.apt.note.Note");
	}

	/**
	 * @see com.sun.mirror.apt.AnnotationProcessorFactory#supportedOptions()
	 */
	@Override
	public Collection<String> supportedOptions()
	{
		return Collections.emptyList();
	}
}
