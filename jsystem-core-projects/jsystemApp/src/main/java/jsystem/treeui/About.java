/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.ClassSearchUtil;

/**
 * The About Version Dialog, activated from the Help Menu
 * 
 * @author yoram.shamir
 * 
 */
public class About extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(About.class.getName());
	
	private String version;

	private static About instance;

	/**
	 * Returns the static instance of the About dialog.
	 */
	public static About getInstance(JFrame parent) {
		if (instance == null) {
			instance = new About(parent);
		}
		return instance;
	}

	/**
	 * @param parent	JFrame parent
	 */
	public About(JFrame parent) {
		
		super(parent);

		// Set window frame - label and icon
		setTitle("About JSystem");
		setIconImage(ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_JSYSTEM));

		// Set window location in the middle of the physical screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation(screenWidth / 3, screenHeight / 3);
		setModal(true);
		setResizable(false);
		
		// Get dialog panel
		JPanel panel = (JPanel)getContentPane();
		
		// Add JPanel with background
		ImageIcon leftImage = ImageCenter.getInstance().getImage(ImageCenter.ABOUT_DIALOG_LEFT_IMAGE);
		JPanel bgPanel = jsystem.utils.SwingUtils.getJPannelWithLeftBgImage(leftImage);
		bgPanel.setLayout(new BorderLayout());
		bgPanel.setBackground(new Color(0xf6, 0xf6, 0xf6));
		panel.add(bgPanel);
		
		// Add JSystem logo
		ImageIcon logoImage = ImageCenter.getInstance().getImage(ImageCenter.ABOUT_DIALOG_LOGO);
		JLabel logoImageLable = new JLabel(logoImage);
		logoImageLable.setOpaque(false);
		logoImageLable.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		bgPanel.add(logoImageLable, BorderLayout.PAGE_START);
		
		/*
		 * Add label panel for version, contribution note and customer product 
		 */
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		labelPanel.setOpaque(false);
		labelPanel.setBorder(BorderFactory.createEmptyBorder(4, 32, 4, 8));
		
		// Add version
		try {
			version = ClassSearchUtil.getPropertyFromClassPath("META-INF/maven/org.jsystemtest/jsystemApp/pom.properties","version");
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed getting client version: " + e.getMessage());
		}
		JLabel versionLabel = new JLabel("Version: " + version);
		versionLabel.setOpaque(false);
		versionLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
		versionLabel.setFont(new Font("sansserif", Font.BOLD, 16));		
		labelPanel.add(versionLabel);

		// Add copyright
		final JLabel copyrightLabel = new JLabel("<html> Copyright 2005-2012 <a href=\"www.top-q.co.il\">Top-Q</a>. All rights reserved.</html>");
		copyrightLabel.setOpaque(false);
		copyrightLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 8, 4));
		copyrightLabel.setFont(new Font("sansserif", Font.PLAIN, 12));
		labelPanel.add(copyrightLabel);
		
		// Add link to Ignis URL
		copyrightLabel.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				copyrightLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));  
			}  
			public void mouseExited(MouseEvent me) {  
				copyrightLabel.setCursor(Cursor.getDefaultCursor());  
			}  
			public void mouseClicked(MouseEvent me) {
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.browse(new URI("http://www.top-q.co.il"));
					} catch (Exception e) {
						log.log(Level.WARNING, "Failed openning browser to Ignis website: " + e.getMessage());
					}
				}
			}  
		});		

		// Add contribution notification
		JLabel contributionLable1 = new JLabel("JSystem is developed using other open source projects.");
		contributionLable1.setOpaque(false);
		contributionLable1.setBorder(BorderFactory.createEmptyBorder(8, 4, 2, 4));
		contributionLable1.setFont(new Font("sansserif", Font.PLAIN, 12));
		labelPanel.add(contributionLable1);
		JLabel contributionLable2 = new JLabel("For a complete list go to");
		contributionLable2.setOpaque(false);
		contributionLable2.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		contributionLable2.setFont(new Font("sansserif", Font.PLAIN, 12));
		labelPanel.add(contributionLable2);
		final JLabel contributionLable3 = new JLabel("<html><a href=\"http://trac.jsystemtest.org/wiki/DetailedOSProjectsList\">http://trac.jsystemtest.org</a></html>");
		contributionLable3.setOpaque(false);
		contributionLable3.setBorder(BorderFactory.createEmptyBorder(2, 4, 16, 4));
		contributionLable3.setFont(new Font("sansserif", Font.PLAIN, 12));
		labelPanel.add(contributionLable3);
		
		// Add link to JSystem URL
		contributionLable3.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				contributionLable3.setCursor(new Cursor(Cursor.HAND_CURSOR));  
			}  
			public void mouseExited(MouseEvent me) {  
				contributionLable3.setCursor(Cursor.getDefaultCursor());  
			}  
			public void mouseClicked(MouseEvent me) {
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.browse(new URI("http://trac.jsystemtest.org/wiki/DetailedOSProjectsList"));
					} catch (Exception e) {
						log.log(Level.WARNING, "Failed openning browser to JSystem website: " + e.getMessage());
					}
				}
			}  
		});		
		
		// Add customer version if required
		String customerProduct = JSystemProperties.getInstance().getPreference(FrameworkOptions.CUSTOMER_PRODUCT);
		if (customerProduct != null) {
			String customerProductList[] = customerProduct.split(CommonResources.DELIMITER);
			labelPanel.add(new JSeparator());
			JLabel customerLabel = new JLabel("Customer information:");
			customerLabel.setOpaque(false);
			customerLabel.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
			customerLabel.setFont(new Font("sansserif", Font.PLAIN, 12));
			labelPanel.add(customerLabel);
			JLabel customerLabels[] = new JLabel[customerProductList.length]; 
			for (int i = 0; i < customerProductList.length; i++) {
				customerLabels[i] = new JLabel(customerProductList[i]);
				customerLabels[i].setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
				customerLabels[i].setFont(new Font("sansserif", Font.PLAIN, 12));
				labelPanel.add(customerLabels[i]);
			}
		}
		
		// Add label panel to the dialog
		bgPanel.add(labelPanel, BorderLayout.CENTER);
		
		// Add Close button
		JButton closeButton = new JButton("Close");
		closeButton.setOpaque(false);
		closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		JPanel closeButtonPanel = new JPanel();
		closeButtonPanel.setOpaque(false);
		closeButtonPanel.add(closeButton);
		closeButtonPanel.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
		bgPanel.add(closeButtonPanel, BorderLayout.PAGE_END);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
		pack();
		
	}
	
	public void reload(){
		setVisible(true);
	}

}