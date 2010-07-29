package timeflow.views;

import timeflow.model.*;
import timeflow.util.Pad;
import timeflow.data.db.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class DescriptionView extends AbstractView {

	JTextArea content;
	JComponent controls;
	
	public DescriptionView(TFModel model) {
		super(model);
		setLayout(new BorderLayout());
		JPanel left=new Pad(5,5);
		left.setBackground(Color.white);
		add(left, BorderLayout.WEST);
		JPanel right=new Pad(5,5);
		right.setBackground(Color.white);
		add(right, BorderLayout.EAST);
		JPanel top=new JPanel();
		add(top, BorderLayout.NORTH);
		top.setLayout(new FlowLayout(FlowLayout.LEFT));
		top.add(new JLabel("Notes & Comments on This Data:"));
		content=new JTextArea();
		content.setLineWrap(true);
		content.setWrapStyleWord(true);
		add(content, BorderLayout.CENTER);
		content.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				getModel().getDB().setDescription(content.getText());
				getModel().noteNewDescription(DescriptionView.this);
			}});
		controls=new HtmlControls("Each TimeFlow data set<br> comes with a free-form <br> "+
				"description area. <p>This is a good place to write<br> notes "+
				"about sources, how the data<br>was cleaned, etc.");
	}
	
	@Override
	public JComponent _getControls()
	{
		return controls;
	}
	
	@Override
	protected void _note(TFEvent e) {
		if (e.type==TFEvent.Type.DESCRIPTION_CHANGE || e.type==TFEvent.Type.DATABASE_CHANGE)
		{
			content.setText(getModel().getDB().getDescription());
			repaint();
		}
	}

	@Override
	public String getName() {
		return "Notes";
	}

	@Override
	protected void onscreen(boolean majorChange) {
		ActDB db=getModel().getDB();
		content.setText(db.getDescription());
		content.requestFocus();
	}
}
