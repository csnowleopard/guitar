package gui.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class is an extension of the JPanel class that contains the Events
 * portion of the LegendPanel.
 * 
 * @author Chris Carmel
 *
 */
public class EventsLegendPanel extends JPanel {
	
	/**
	 * String array of types of Events.
	 */
	private String[] eventTypes = { "Menu Open", "Restricted Focus", "System Input", "Terminal", "Unrestricted Focus"};
	
	/**
	 * Color array of the colors of the types of Events.
	 */
	private Color[] eventColors = { Color.YELLOW, Color.ORANGE, Color.BLUE, Color.RED, Color.GREEN };
	
	/**
	 * Constructs an EventsLegendPanel.
	 */
	public EventsLegendPanel() {
		super();
		
		this.setBorder(BorderFactory.createTitledBorder("Events"));
		
		this.setMinimumSize(new Dimension(152, 173));
		this.setPreferredSize(new Dimension(152, 173));
		this.setMaximumSize(new Dimension(152, 173));
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbConstraints = new GridBagConstraints();
		
		for (int i = 0; i < eventTypes.length; i++) {
			gbConstraints.gridy = i;
			gbConstraints.gridx = 0;

			this.add(eventColorPanel(eventColors[i]), gbConstraints);
			
			gbConstraints.gridx = 1;
			JLabel eventLabel = new JLabel(eventTypes[i], JLabel.LEADING);
			this.add(eventLabel, gbConstraints);
		}
	}
	
	/**
	 * Returns a bordered 5 pixel by 5 pixel JPanel of the specified color.
	 * 
	 * @param colorIn		color to set the JPanel background to
	 * 
	 * @return				a bordered 5 pixel by 5 pixel JPanel of the specified color
	 */
	public JPanel eventColorPanel(Color colorIn) {
		JPanel coloredPanelContainer = new JPanel();
		JPanel coloredPanel = new JPanel();
		coloredPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		coloredPanel.setBackground(colorIn);
		coloredPanel.setSize(5, 5);
		coloredPanelContainer.add(coloredPanel);
		
		return coloredPanelContainer;
	}
}
