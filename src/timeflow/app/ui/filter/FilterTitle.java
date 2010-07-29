package timeflow.app.ui.filter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import timeflow.app.ui.DottedLine;
import timeflow.data.db.Field;
import timeflow.model.ModelPanel;

import timeflow.util.*;

public class FilterTitle extends JPanel {
	public FilterTitle(final Field field, final ModelPanel parent, boolean dots)
	{
		this(field.getName(), field, parent, dots);
	}
	public FilterTitle(String title, final Field field, final ModelPanel parent, boolean dots)
	{
		JPanel top=new JPanel();
		top.setBackground(Color.white);
		top.setLayout(new BorderLayout());
		JLabel label=new JLabel(title);
		JPanel pad=new Pad(30,30);
		pad.setBackground(Color.white);
		top.add(pad, BorderLayout.NORTH);
		top.add(label, BorderLayout.CENTER);
		label.setBackground(Color.white);
		
		if (parent instanceof FilterControlPanel)
		{
			ImageIcon redX=new ImageIcon("images/red_circle.gif");
			JLabel close=new JLabel(redX);
			close.setBackground(Color.white);
			top.add(close, BorderLayout.EAST);
			close.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					((FilterControlPanel)parent).setFacet(field, false);
				}
				});
		}
		setLayout(new BorderLayout());
		add(top, BorderLayout.CENTER);
		
		if (dots)
			add(new DottedLine(), BorderLayout.SOUTH);
	}
}
