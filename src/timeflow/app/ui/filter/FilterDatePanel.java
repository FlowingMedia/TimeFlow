package timeflow.app.ui.filter;

import timeflow.data.db.*;
import timeflow.data.db.filter.*;
import timeflow.model.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.*;
import java.util.Date;

import javax.swing.*;

// in theory it should be easy to refactor this to share code with
// NumberFilterPanel.
// but, i'm not sure how to do it in a way that doesn't make the code
// seem too complicated.

public class FilterDatePanel  extends FilterDefinitionPanel 
{
	BabyHistogram histogram;
	Field field;
	JTextField startEntry;
	JTextField endEntry;
	JCheckBox nullBox;
	Runnable action;
	SimpleDateFormat df=new SimpleDateFormat("MMM dd yyyy");
	
	public FilterDatePanel(final Field field, final Runnable action, final FilterControlPanel parent)
	{
		this.field=field;
		this.action=action;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
		setBackground(Color.white);
		add(new FilterTitle(field, parent, false), BorderLayout.NORTH);
		
		Runnable fullAction=new Runnable()
		{
			public void run()
			{
				startEntry.setText(format(histogram.getLow()));
				endEntry.setText(format(histogram.getHigh()));
				action.run();
			}
		};
		
		histogram=new BabyHistogram(fullAction);
		
		add(histogram, BorderLayout.CENTER);
		
		JPanel bottomStuff=new JPanel();
		bottomStuff.setLayout(new GridLayout(2,1));
		add(bottomStuff, BorderLayout.SOUTH);
		
		JPanel lowHighPanel=new JPanel();
		bottomStuff.add(lowHighPanel);
		lowHighPanel.setBackground(Color.white);
		lowHighPanel.setLayout(new BorderLayout());
		Font small=parent.getModel().getDisplay().small();
		
		startEntry=new JTextField(7);
		startEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setLowFromText();
				action.run();
			}});
		lowHighPanel.add(startEntry, BorderLayout.WEST);
		startEntry.setFont(small);
		
		JLabel rangeLabel=new JLabel("to", JLabel.CENTER);
		rangeLabel.setForeground(Color.gray);
		rangeLabel.setFont(small);
		lowHighPanel.add(rangeLabel, BorderLayout.CENTER);
		endEntry=new JTextField(7);
		lowHighPanel.add(endEntry, BorderLayout.EAST);
		endEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHighFromText();
				action.run();
			}});
		endEntry.setFont(small);
		
		nullBox=new JCheckBox("Include Missing Values");
		nullBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.run();
			}});
		bottomStuff.add(nullBox);
		bottomStuff.setBackground(Color.white);
		nullBox.setBackground(Color.white);
		nullBox.setForeground(Color.gray);
		nullBox.setFont(small);
		
	}
	
	String format(double x)
	{
		Date date=new Date((long)x);
		return df.format(date);
	}
	
	void setLowFromText()
	{
		try
		{
			long low=df.parse(startEntry.getText()).getTime();
			long high=(long)histogram.getHigh();
			if (low>high)
			{
				high=low;
				endEntry.setText(startEntry.getText());
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
			long high=df.parse(endEntry.getText()).getTime();
			double low=(long)histogram.getLow();
			if (low>high)
			{
				low=high;
				startEntry.setText(endEntry.getText());
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
		startEntry.setText(format(histogram.getLow()));
		endEntry.setText(format(histogram.getHigh()));
		repaint();
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(200,160);
	}

	@Override
	public ActFilter defineFilter() {
		long low=(long)histogram.getLow();
		long high=(long)histogram.getHigh();
		boolean acceptNull=nullBox.isSelected();
		return new TimeIntervalFilter(low, high, acceptNull, field);
	}

	@Override
	public void clearFilter() {
		histogram.setRelRange(0, 1);
	}
}