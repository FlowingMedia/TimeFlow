package timeflow.app.ui.filter;

import timeflow.data.db.filter.ActFilter;

import javax.swing.*;

public abstract class FilterDefinitionPanel extends JPanel {
	public abstract ActFilter defineFilter();
	public abstract void clearFilter();
}
