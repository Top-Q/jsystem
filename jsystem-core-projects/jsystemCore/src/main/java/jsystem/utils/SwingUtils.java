/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * Holds sreval Swing utils , like image background adding on diffrent kind of
 * components.
 * 
 * @author uri.koaz
 */
public class SwingUtils {

	/**
	 * 
	 * @param name
	 *            component name
	 * @param orientation
	 *            which orientation the component will use
	 * @param image
	 *            image to put as backgroud
	 * @return JToolBar with bg image
	 */
	public static JToolBar getJToolBarWithBgImage(String name, int orientation, ImageIcon image) {
		return new ToolBarWithBgImage(name, orientation, image);
	}

	/**
	 * 
	 * @param name
	 *            component name
	 * @param orientation
	 *            which orientation the component will use
	 * @param c1
	 *            from color
	 * @param c2
	 *            to color
	 * @return JToolBar with bg gradient color
	 */
	public static JToolBar getJToolBarWithGradientBgColor(String name, int orientation, Color c1, Color c2) {
		return new ToolBarWithGradientBgColor(name, orientation, c1, c2);
	}

	/**
	 * 
	 * @param tabBg
	 * @param paneBg
	 * @return the tabbed pane
	 */
	public static JTabbedPane getJTabbedPaneWithBgImage(ImageIcon tabBg, ImageIcon paneBg) {
		return new ImageTabbedPane(tabBg, paneBg);
	}

	public static JButton getJButtonWithBgImage(ImageIcon image) {
		return new JButtonWithBgImage(image);
	}

	public static JPanel getJPannelWithBgImage(ImageIcon image, int fromHeight) {
		return new JPanelWithBgImage(image, fromHeight);
	}

	public static JPanel getJPannelWithLeftBgImage(ImageIcon bgImage) {
		return new JPanelWithLeftBgImage(bgImage);
	}

	/**
	 * returns a JScrollPane with static iumage bg
	 * 
	 * @param image
	 *            image to put as water mark ( static bg image)
	 * @param view
	 *            JComponenet to put on it
	 * 
	 * @return JScrollPane with water mark on it
	 */
	public static JScrollPane getJScrollPaneWithWaterMark(Image image, JComponent view) {
		JScrollPane sc = new JScrollPane();
		sc.setViewport(new ScrollPaneWatermark(image, view));
		return sc;
	}
	
	/**
	 * 
	 */
	public static void setToolBarComboBoxLAF(JComboBox box){
		box.setOpaque(false);
		box.setPreferredSize(new Dimension(100, 20));
		box.setRenderer(new MyComboBoxRenderer());
	}
	
	public static void setBusyCursor(Component comp,boolean busy){
		if (busy) {
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}

class MyComboBoxRenderer extends BasicComboBoxRenderer {

	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
			if (-1 < index) {
				list.setToolTipText((value == null) ? "" : new String(value.toString()));
			}
		} else {
			setBackground(Color.white);
			setForeground(Color.black);
		}
		setFont(list.getFont());
		setText((value == null) ? "" : value.toString());
		return this;
	}
}

/**
 * This implementation stretches the image to fit the panel. * Could modify
 * this to "tile" the image, center the image, etc.
 */
class ToolBarWithBgImage extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImageIcon bgImage;

	ToolBarWithBgImage(String name, int orientation, ImageIcon ii) {
		super(name, orientation);
		this.bgImage = ii;
		setOpaque(true);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension size = this.getSize();
		if (bgImage != null) {
			g.drawImage(bgImage.getImage(), 0, 0, size.width, size.height, this);
		}
		Color s1 = Color.red;
		Color e = Color.green;
		GradientPaint gradient1 = new GradientPaint(size.width, size.height, s1, 30, 30, e, true);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setPaint(gradient1);
	}
}

class ToolBarWithGradientBgColor extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Color c1;

	private Color c2;

	ToolBarWithGradientBgColor(String name, int orientation, Color c1, Color c2) {
		super(name, orientation);
		setOpaque(true);
	}

	public void paint(Graphics g) {

		Dimension size = this.getSize();

		GradientPaint gradient1 = new GradientPaint(0, 0, c1, size.width, size.height, c2, true);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setPaint(gradient1);

		super.paint(g);
	}
}

/**
 * This implementation stretches the image to fit the panel. * Could modify
 * this to "tile" the image, center the image, etc.
 */
class JTabPaneWithBgImage extends JTabbedPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImageIcon bgImage;

	JTabPaneWithBgImage(ImageIcon ii) {
		this.bgImage = ii;
		setOpaque(true);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bgImage != null) {
			Dimension size = this.getSize();
			g.drawImage(bgImage.getImage(), 0, 0, size.width, size.height, this);
		}
	}
}

class JButtonWithBgImage extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImageIcon bgImage;

	JButtonWithBgImage(ImageIcon ii) {
		this.bgImage = ii;
		setOpaque(true);
	}

	public void paintComponent(Graphics g) {
		if (bgImage != null) {
			Dimension size = this.getSize();
			g.drawImage(bgImage.getImage(), 0, 0, size.width, size.height, this);
		}

		super.paintComponent(g);
	}
}

/**
 * This implementation stretches the image to fit the panel height.
 * 
 * @author yoram.shamir
 */
class JPanelWithLeftBgImage extends JPanel {

	private static final long serialVersionUID = 1L;

	private ImageIcon bgImage;

	JPanelWithLeftBgImage(ImageIcon bgImage) {
		this.bgImage = bgImage;
		setOpaque(true);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bgImage != null) {
			Dimension size = this.getSize();
			g.drawImage(bgImage.getImage(), 0, 0, bgImage.getImage().getWidth(null), (int)size.getHeight(), null);
		}
	}
	
}

/**
 * This implementation stretches the image to fit the panel. * Could modify
 * this to "tile" the image, center the image, etc.
 */
class JPanelWithBgImage extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImageIcon bgImage;

	private int fromHeight = 0;

	JPanelWithBgImage(ImageIcon ii, int fromHeight) {
		this.bgImage = ii;
		this.fromHeight = fromHeight;
		setOpaque(true);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bgImage != null) {
			Dimension size = this.getSize();
			g.drawImage(bgImage.getImage(), 0, fromHeight, size.width, bgImage.getImage().getHeight(null), this);
		}
	}
}

class ImageTabbedPane extends JTabbedPane {

	// Display properties

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Image m_tabBackground;

	private Image m_paneBackground;

	public ImageTabbedPane(ImageIcon tabBackground, ImageIcon paneBackground) {

		m_tabBackground = tabBackground.getImage();

		m_paneBackground = paneBackground.getImage();

		setUI((ImageTabbedPaneUI) ImageTabbedPaneUI.createUI(this));
	}

	public void setTabBackground(Image i) {
		m_tabBackground = i;

		repaint();
	}

	public void setPaneBackground(Image i) {
		m_paneBackground = i;

		repaint();
	}

	public Image getTabBackground() {
		return m_tabBackground;
	}

	public Image getPaneBackground() {
		return m_paneBackground;
	}
}

class ImageTabbedPaneUI extends BasicTabbedPaneUI {

	private Image m_image;

	public static ComponentUI createUI(JComponent c) {
		return new ImageTabbedPaneUI();
	}

	public void update(Graphics g, JComponent c) {

		if (c instanceof ImageTabbedPane) {

			Image paneImage = ((ImageTabbedPane) c).getPaneBackground();

			int w = c.getWidth();
			int h = c.getHeight();

			int iw = paneImage.getWidth(tabPane);
			int ih = paneImage.getHeight(tabPane);

			if (tabPane.getTabCount() > 0) {
				if (iw > 0 && ih > 0) {
					for (int j = 0; j < h; j += ih) {
						for (int i = 0; i < w; i += iw) {
							g.drawImage(paneImage, i, j, tabPane);
						}
					}
				}
			} else {
				g.setColor(new Color(0xf6, 0xf6, 0xf6));
				g.fillRect(0, 0, tabPane.getWidth(), tabPane.getHeight());
			}
		}

		paint(g, c);

	}

	public void paint(Graphics g, JComponent c) {

		if (c instanceof ImageTabbedPane) {
			m_image = ((ImageTabbedPane) c).getTabBackground();
		}
		super.paint(g, c);
	}

	protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
			boolean isSelected) {

		Color tp = tabPane.getBackgroundAt(tabIndex);

		switch (tabPlacement) {
		case LEFT:
			g.drawImage(m_image, x + 1, y + 1, (w - 2) + (x + 1), (y + 1) + (h - 3), 0, 0, w, h, tp, tabPane);
			break;
		case RIGHT:
			g.drawImage(m_image, x, y + 1, (w - 2) + (x), (y + 1) + (h - 3), 0, 0, w, h, tp, tabPane);
			break;
		case BOTTOM:
			g.drawImage(m_image, x + 1, y, (w - 3) + (x + 1), (y) + (h - 1), 0, 0, w, h, tp, tabPane);
			break;
		case TOP:
			g.drawImage(m_image, x + 1, y + 1, (w - 3) + (x + 1), (y + 1) + (h - 1), 0, 0, w, h, tp, tabPane);
		}
	}
}

class ScrollPaneWatermark extends JViewport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Image waterMarkImage;

	public ScrollPaneWatermark(Image waterMarkImage, JComponent view) {
		this.waterMarkImage = waterMarkImage;

		setView(view);
		setBackground(Color.white);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Dimension size = this.getSize();
		g.drawImage(waterMarkImage, 0, getHeight() - waterMarkImage.getHeight(null), size.width, waterMarkImage
				.getHeight(null), this);

	}

	public void setView(JComponent view) {
		view.setOpaque(false);
		super.setView(view);
	}
}