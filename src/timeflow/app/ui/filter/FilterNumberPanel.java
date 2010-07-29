package timeflow.app.ui.filter;

import timeflow.data.db.*;
import timeflow.data.db.filter.*;
import timeflow.model.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import timeflow.util.*;

public class FilterNumberPanel  extends FilterDefinitionPanel 
{
	BabyHistogram histogram;
	Field field;
	JTextField lowEntry;
	JTextField highEntry;
	JCheckBox nullBox;
	Runnable action;
	
	public FilterNumberPanel(final Field field, final Runnable action, final FilterControlPanel parent)
	{
		this.field=field;
		this.action=action;
		setLayout(new BorderLayout());
		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
		
		
		setBackground(Color.white);
		add(new FilterTitle(field, parent, false), BorderLayout.NORTH);
		
		Runnable fullAction=new Runnable()
		{
			public void run()
			{
				lowEntry.setText(format(histogram.getLow()));
				highEntry.setText(format(histogram.getHigh()));
				action.run();
			}
		};
		
		histogram=new BabyHistogram(fullAction);
		
		add(histogram, BorderLayout.CENTER);
		
		JPanel bottomStuff=new JPanel();
		bottomStuff.setLayout(new GridLayout(2,1));
		add(bottomStuff, BorderLayout.SOUTH);
		bottomStuff.setBackground(Color.white);
		
		JPanel lowHighPanel=new JPanel();
		bottomStuff.add(lowHighPanel);
		lowHighPanel.setBackground(Color.white);
		
		lowHighPanel.setLayout(new BorderLayout());
		
		Font small=parent.getModel().getDisplay().small();
		lowEntry=new JTextField(7);
		lowEntry.setFont(small);
		lowEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setLowFromText();
				action.run();
			}});
		lowHighPanel.add(lowEntry, BorderLayout.WEST);
		JLabel rangeLabel=new JLabel("to", JLabel.CENTER);
		
		rangeLabel.setFont(small);
		rangeLabel.setForeground(Color.gray);
		lowHighPanel.add(rangeLabel, BorderLayout.CENTER);
		highEntry=new JTextField(7);
		lowHighPanel.add(highEntry, BorderLayout.EAST);
		highEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHighFromText();
				action.run();
			}});
		highEntry.setFont(small);
		
		nullBox=new JCheckBox("Include Missing Values");
		nullBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.run();
			}});
		bottomStuff.add(nullBox);
		nullBox.setBackground(Color.white);
		nullBox.setForeground(Color.gray);
		nullBox.setFont(small);
	}
	
	String format(double x)
	{
		if (Math.abs(x)>10)
			return Display.format(Math.round(x));
		return Display.format(x);
	}
	
	void setLowFromText()
	{
		try
		{
			double low=Double.parseDouble(lowEntry.getText());
			double high=histogram.getHigh();
			if (low>high)
			{
				high=low;
				highEntry.setText(lowEntry.getText());
			}
			histogram.setTrueRange(low,high);
			
		}
		catch (Exception e)
		{
			
		}
	}
	
	
	void setHighFromText()
	{
		try
		{
			double high=Double.parseDouble(highEntry.getText());
			double low=histogram.getLow();
			if (low>high)
			{
				low=high;
				lowEntry.setText(highEntry.getText());
			}
			histogram.setTrueRange(low,high);
			
		}
		catch (Exception e)
		{
			
		}		
	}

	public void setData(double[] data)
	{
		histogram.setData(data);
		lowEntry.setText(Display.format(histogram.getLow()));
		highEntry.setText(Display.format(histogram.getHigh()));
		repaint();
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(200,160);
	}

	@Override
	public ActFilter defineFilter() {
		double low=histogram.getLow();
		double high=histogram.getHigh();
		boolean acceptNull=nullBox.isSelected();
		return new NumericRangeFilter(field, low, high, acceptNull);
	}

	@Override
	public void clearFilter() {
		histogram.setRelRange(0, 1);
	}
}