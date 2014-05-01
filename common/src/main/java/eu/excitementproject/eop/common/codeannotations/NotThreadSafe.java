package eu.excitementproject.eop.common.codeannotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotates a class or interface as not-thread-safe.
 * This annotation is not mandatory. Not every class has to be
 * annotated.
 * However it is a good practice to do that in cases that it are not
 * naturally clear.
 * <P>
 * <B>Interpretation of this annotations:</B><BR>
 * Any instance of the annotated class will never be shared by threads.
 * Only one thread will access it.
 * <P>
 * <B>Note:</B> If it is safe to share only some methods of the class
 * among different threads, but not all of them, for example - a
 * classifier that can be shared among thread for classifying samples,
 * but the training must be done only once by a single thread - then it
 * <B>should not</B> be annotated by this annotation. Such cases should
 * be documented in Java-Doc.
 * <P>
 * This might be somewhat confusing, so be aware of the following:
 * Not-thread-safe is the <B>easier</B> case from programmer's
 * point of view. Since if a class is not thread safe, the programmer
 * can assume that the user of the class will not share its instances
 * among threads, so the programmer does not have to take care of
 * thread-safety issues.
 * On the contrary, it is harder to write a class that <B>is</B> thread
 * safe, since it will be shared among threads, so the programmer should
 * make sure it is written in a way that is safe to be shared among threads.
 * 
 * 
 * @author Asher Stern
 * @since Feb 8, 2012
 * 
 * @see ThreadSafe
 *
 */
@Documented
@Target(ElementType.TYPE)
public @interface NotThreadSafe
{

}
