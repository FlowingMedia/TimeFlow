package timeflow.app.ui;

import timeflow.model.*;
import timeflow.app.ui.filter.FilterControlPanel;
import timeflow.data.db.*;
import timeflow.data.db.filter.ActFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.*;

public class StatusPanel extends ModelPanel
{
	JLabel numLabel=new JLabel("")
	{
		public Dimension getPreferredSize()
		{
			return new Dimension(30,25);
		}
	};
	JLabel filterLabel=new JLabel("")
	{
		public Dimension getPreferredSize()
		{
			return new Dimension(30,25);
		}
	};
	
	static final DecimalFormat niceFormat=new DecimalFormat("###,###");
	
	public StatusPanel(TFModel model, final FilterControlPanel filterControls) {
		super(model);	
		setLayout(new BorderLayout());
		setBackground(new Color(245, 245, 245));
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
		JPanel center=new JPanel();
		center.setBackground(getBackground());
		center.setLayout(new GridLayout(2,1));
		add(center, BorderLayout.CENTER);
		
		center.add(numLabel);
		numLabel.setFont(model.getDisplay().plain());
		numLabel.setBackground(new Color(245, 245, 245));
		
		JPanel bottom=new JPanel();
		center.add(bottom);
		bottom.setLayout(new BorderLayout());
		bottom.add(filterLabel, BorderLayout.CENTER);
		bottom.setBackground(new Color(245, 245, 245));
		filterLabel.setFont(model.getDisplay().plain());
		filterLabel.setBackground(new Color(245, 245, 245));
		filterLabel.setForeground(Color.red);
		
		JPanel clearPanel=new JPanel();
		clearPanel.setBackground(new Color(245, 245, 245));
		clearPanel.setLayout(new GridLayout(1,1));
		JButton clear=new JButton(new ImageIcon("images/button_clear_all.gif"));
		clear.setBorder(null);
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterControls.clearFilters();
			}
		});
		clearPanel.add(clear);
		bottom.add(clearPanel, BorderLayout.EAST);
		
		add(new DottedLine(), BorderLayout.SOUTH);
	}

	@Override
	public void note(TFEvent e) {
		
		ActDB db=getModel().getDB();
		if (db==null || db.size()==0)
		{
			numLabel.setForeground(new Color(245,245,245));//Color.gray);
			numLabel.setText("No data");
			return;
		}
		int numTotal=db.size();
		ActList acts=getModel().getActs();
		int numShown=acts.size();
		filterLabel.setText(numShown<numTotal ? " Filters applied" : " Not Filtering");
		filterLabel.setForeground(numShown==numTotal ? Color.lightGray : Color.red);
		numLabel.setForeground(numShown==0 ? Color.red : Color.darkGray);
		String plural=(numTotal==1 ? "" : "s");
		if (numShown==numTotal)
			numLabel.setText(" Showing All "+niceFormat.format(numTotal)+" Event"+plural);
		else
			numLabel.setText(" Showing "+niceFormat.format(numShown)
					+" / "+niceFormat.format(numTotal)+" Event"+ plural);
		repaint();
	}

}
