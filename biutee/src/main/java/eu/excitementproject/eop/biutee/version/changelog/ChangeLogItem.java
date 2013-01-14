package eu.excitementproject.eop.biutee.version.changelog;
import eu.excitementproject.eop.biutee.version.Version;

/**
 * Represents an item in the change-log. Note that the version member field
 * ( {@link #versionBeforeChange} ) indicates the version <b>before</b> this
 * change.
 * 
 * @author Asher Stern
 * @since Apr 30, 2012
 *
 */
public class ChangeLogItem
{
	public ChangeLogItem(Version versionBeforeChange, ChangeType changeType,
			String description)
	{
		this(versionBeforeChange,false,changeType,description);
	}
	
	public ChangeLogItem(Version versionBeforeChange, boolean major, ChangeType changeType,
			String description)
	{
		super();
		this.versionBeforeChange = versionBeforeChange;
		this.changeType = changeType;
		this.description = description;
		this.major = major;
	}

	
	public Version getVersionBeforeChange()
	{
		return versionBeforeChange;
	}
	public ChangeType getChangeType()
	{
		return changeType;
	}
	public String getDescription()
	{
		return description;
	}
	public boolean isMajor()
	{
		return major;
	}




	private final Version versionBeforeChange;
	private final ChangeType changeType;
	private final String description;
	private final boolean major;
}
