package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * 
 * @author Asher Stern
 * @since Sep 5, 2012
 *
 */
public class SurroundingViewDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = -547289324286489071L;

	public SurroundingViewDialog(Frame frame, List<ExtendedNode> trees, ActionsPerformer actionsPerformer)
	{
		super(frame,true);
		this.frame = frame;
		this.trees = new ArrayList<ExtendedNode>(trees.size());
		this.trees.addAll(trees);
		this.actionsPerformer = actionsPerformer;
	}
	
	public void startDialog() throws VisualTracingToolException
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		imagePaintingComponent = new ImagePaintingComponent();
		imagePanel = new JScrollPane(imagePaintingComponent, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.add(imagePanel,BorderLayout.CENTER);
		
		JPanel buttonsPanel = new JPanel();
		JButton nextButton = new JButton("Next");
		nextButton.setActionCommand("Next");
		nextButton.addActionListener(this);
		JButton prevButton = new JButton("Prev");
		prevButton.setActionCommand("Prev");
		prevButton.addActionListener(this);
		
		buttonsPanel.add(prevButton);
		buttonsPanel.add(nextButton);
		
		mainPanel.add(buttonsPanel,BorderLayout.PAGE_END);
		this.setContentPane(mainPanel);		
		
		if (this.trees!=null) { if (this.trees.size()>0)
		{
			indexTrees = 0;
			drawTree(trees.get(indexTrees));
		}}
		
		Dimension currentDimension = this.getPreferredSize();
		Dimension frameDimension = frame.getSize();
		this.setPreferredSize(new Dimension((int)(frameDimension.getWidth()*0.8), (int)currentDimension.getHeight()));
		this.pack();
		
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			if (e.getActionCommand().equals("Next"))
			{
				if (indexTrees<(trees.size()-1))
				{
					indexTrees++;
					drawTree(trees.get(indexTrees));
				}
			}
			else if (e.getActionCommand().equals("Prev"))
			{
				if (indexTrees>0)
				{
					indexTrees--;
					drawTree(trees.get(indexTrees));
				}
			}
		}
		catch(Exception ex)
		{
			SwingUtilities.handleError(this, ex, false);
		}
	}
	
	private void drawTree(ExtendedNode tree) throws VisualTracingToolException
	{
		
		BufferedImage image = actionsPerformer.getGuiUtils().createImage(tree, "", null);
		imagePaintingComponent.setFullSizeImage(image);
		imagePaintingComponent.setZoomRatio(actionsPerformer.getMasterZoomRatio());
		image = imagePaintingComponent.getImage();
		imagePaintingComponent.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		this.repaint();
		imagePanel.setViewportView(imagePaintingComponent);
	}

	
	
	
	private Frame frame;
	private List<ExtendedNode> trees;
	private ActionsPerformer actionsPerformer;
	
	private ImagePaintingComponent imagePaintingComponent;
	private JScrollPane imagePanel;
	
	private int indexTrees = 0;

}
