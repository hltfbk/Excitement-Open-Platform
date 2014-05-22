/**
 * 
 */
package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * This is used to render the appearance of the JTable headline
 * @author Amnon Lotan
 *
 * @since 11 Mar 2012
 */
class CustomHeaderRenderer extends JList<String> implements TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3498925717258638169L;

	public CustomHeaderRenderer() {
		setOpaque(true);
		setForeground(UIManager.getColor("TableHeader.foreground"));
		setBackground(Color.LIGHT_GRAY);	//  UIManager.getColor("TableHeader.background"));
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		ListCellRenderer<? super String> renderer = getCellRenderer();
		((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
		setCellRenderer(renderer);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		//		    setFont(table.getFont());
		setFont(new Font("Verdana", Font.PLAIN, table.getFont().getSize()));
		String str = (value == null) ? "" : value.toString();
		BufferedReader br = new BufferedReader(new StringReader(str));
		String line;
		Vector<String> v = new Vector<String>();
		try {
			while ((line = br.readLine()) != null) {
				v.addElement(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		setListData(v);
		return this;
	}

	/**
	 * Add the custom header renderer
	 * @param existingTreesTable
	 */
	public static void setHeaderRendererToJTable(JTable existingTreesTable) {
		Enumeration<TableColumn> e = existingTreesTable.getColumnModel().getColumns();
		while (e.hasMoreElements()) {
			((TableColumn) e.nextElement()).setHeaderRenderer(CUSTOM_HEADER_RENDERER);
		}
	}

	/**
	 * This is used to render the appearance of the JTable headline
	 */
	private static final CustomHeaderRenderer CUSTOM_HEADER_RENDERER = new CustomHeaderRenderer();

}
