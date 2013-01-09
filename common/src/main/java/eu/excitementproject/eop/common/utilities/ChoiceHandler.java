package eu.excitementproject.eop.common.utilities;

import java.util.List;

/**
 * Used by {@link AllChoices}.
 * 
 * @author Asher Stern
 *
 * @param <T>
 */
public interface ChoiceHandler<T>
{
	public void handleChoice(List<T> choice);

}
