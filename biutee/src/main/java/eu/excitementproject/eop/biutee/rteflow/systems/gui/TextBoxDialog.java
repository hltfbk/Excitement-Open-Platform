package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 * Like a dialog box, but with text in {@link JTextArea}, and inside a Scroll-pane.
 * The return value is an enum, specified in the constructor, or null.
 * You can use {@link EMPTY_ENUM} if you don't need a return value.
 * <P>
 * Create using the appropriate constructor, and then call {@link #showMe()}.
 * <P>
 * <B>Note:</B> If not modal, the return value is <code>null</code>.
 * <P>
 * If you want a return value - then use the constructor in which you can specify
 * the enum's class. The dialog-box will create buttons for each enum constant.
 * The user will have to push one of them to close the dialog box (or just close
 * it with the "x" in the title-line). Once the user clicks one of these buttons,
 * the dialog is disposed, and the corresponding enum-constant is returned as result.
 * 
 * @author Asher Stern
 * @since Aug 13, 2012
 *
 */
public class TextBoxDialog<E extends Enum<E>> extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 7131286579973195048L;
	
	public static enum EMPTY_ENUM{}
	
	public TextBoxDialog(Frame owner, String title, boolean modal, String text)
	{
		this(owner,title,modal,text,null);
	}
	
	public TextBoxDialog(Frame owner, String title, boolean modal, String text, Class<E> enumButton)
	{
		super(owner,title,modal);
		this.owner = owner;
		this.text = text;
		this.enumButton = enumButton;
		
		if (enumButton!=null)
		{
			createMapEnum();
		}
	}
	

	public void setWordWrap(boolean wordWrap)
	{
		this.wordWrap = wordWrap;
	}

	/**
	 * Displays the dialog box. If modal, returns the value corresponds to the
	 * clicked button.
	 * 
	 * @return If modal, and if the enumButton is specified in the constructor, and it is not an empty
	 * enum, then it returns the enum-constant that corresponds to the button clicked
	 * by the user. Returns <code>null</code> if closed without clicking any button
	 * (e.g. by clicking the "x" at the title).<BR>
	 * Otherwise (not-modal, empty enum, no enum specified) returns <code>null</code>.
	 */
	public E showMe()
	{
		buttonPressed = null;
		
		Rectangle ownerRectangle = owner.getBounds();
		
		
		
		contentPane = new JPanel(new BorderLayout());
		
		JTextArea mainMessageTextArea = new JTextArea(text);
		
		mainMessageTextArea.setLineWrap(wordWrap);
		if (wordWrap)
		{
			mainMessageTextArea.setWrapStyleWord(true);
		}
		
		mainMessageTextArea.setEditable(false);
		
		
		JScrollPane scrollPane = new JScrollPane(mainMessageTextArea);
		scrollPane.setPreferredSize(new Dimension((int) (ownerRectangle.getWidth()/2), (int) (ownerRectangle.getHeight()/2)));
		
		contentPane.add(scrollPane,BorderLayout.CENTER);
		
		addButtons();
		
		this.setContentPane(contentPane);

		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(owner);
		this.pack();
		
		
		
		
		int x = ownerRectangle.x + (ownerRectangle.width - this.getSize().width)/2;
		int y = ownerRectangle.y + (ownerRectangle.height - this.getSize().height)/2;
		this.setLocation(x, y);
		
		this.setVisible(true);
		
		return buttonPressed;
	}
	

	/**
	 * If not modal, you can use this to get the button clicked (but only after it has
	 * been clicked.).
	 * @return
	 */
	public E getButtonPressed()
	{
		return buttonPressed;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		buttonPressed = null;
		if (mapEnum.containsKey(e.getActionCommand()))
		{
			buttonPressed = mapEnum.get(e.getActionCommand());
		}
		setVisible(false);
		dispose();
		
	}
	
	private void addButtons()
	{
		boolean hasEnums = false;
		JPanel buttonPanel = new JPanel();
		if (enumButton!=null)
		{
			E[] enumConstants = enumButton.getEnumConstants();

			if (enumConstants.length>0)
			{
				hasEnums = true;
				for (E e : enumConstants)
				{
					JButton button = new JButton(e.name());
					button.setActionCommand(e.name());
					button.addActionListener(this);
					buttonPanel.add(button);
				}
			}
		}
		if (!hasEnums)
		{
			JButton buttonOK = new JButton("OK");
			buttonOK.setActionCommand("~OK~");
			buttonOK.addActionListener(this);
			buttonPanel.add(buttonOK);

		}
		contentPane.add(buttonPanel,BorderLayout.SOUTH);
	}
	
	private void createMapEnum()
	{
		mapEnum = new LinkedHashMap<String, E>();
		for (E e : enumButton.getEnumConstants())
		{
			mapEnum.put(e.name(),e);
		}
			
	}
	

	private Frame owner;
	private String text;
	private Class<E> enumButton;
	private boolean wordWrap = false;
	
	private JPanel contentPane;
	
	private E buttonPressed = null;
	private Map<String, E> mapEnum = new LinkedHashMap<String, E>();
}
