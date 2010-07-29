package timeflow.app.actions;

import timeflow.model.*;
import timeflow.app.*;
import timeflow.format.file.*;

import javax.swing.*;

import java.awt.Toolkit;
import java.io.*;

public abstract class TimeflowAction extends AbstractAction {
	TimeflowApp app;

    public TimeflowAction(TimeflowApp app, String text, ImageIcon icon, String desc) 
    {
		super(text, icon);
		this.app=app;
		putValue(SHORT_DESCRIPTION, desc);
	}
    
	
	protected void accelerate(char c)
	{
		putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(c,
			    Toolkit.getDefaultToolkit(  ).getMenuShortcutKeyMask(  ), false));
	}


    protected TFModel getModel()
    {
    	return app.model;
    }
    

}
