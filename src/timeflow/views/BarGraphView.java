package timeflow.views;

import timeflow.model.*;
import timeflow.views.ListView.LinkIt;
import timeflow.data.db.*;
import timeflow.data.time.*;

import javax.swing.*;

import timeflow.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BarGraphView extends AbstractView {
	
	BarGraph graph=new BarGraph();
	JPanel controls;
	ArrayList<BarData> bars;
	enum Aggregate {TOTAL, AVERAGE, COUNT};
	Aggregate agg;
	JComboBox splitFieldChoice, numFieldChoice;
	

	public BarGraphView(TFModel model) {
		super(model);
		
		setLayout(new BorderLayout());
		controls=new JPanel();
		add(controls, BorderLayout.NORTH);
		controls.setLayout(null);
		controls.setBackground(Color.white);
		
		JScrollPane scrollPane = new JScrollPane(graph);
		add(scrollPane, BorderLayout.CENTER);

        makeTop();		
	}
	
	protected JComponent _getControls()
	{
		return controls;
	}
	
	void makeTop()
	{
		int x=10, y=10;
		int ch=25, pad=5, cw=160;

		controls.removeAll();
		TFModel model=getModel();
		if (model.getDB()==null || model.getDB().size()==0)
		{
			JLabel empty=new JLabel("Empty database");
			controls.add(empty);
			empty.setBounds(x,y,cw,ch);
			return;
		}		
		
		JLabel top=new JLabel("For each value of");
		controls.add(top);
		top.setBounds(x,y,cw,ch);
		y+=ch+pad;
		
		splitFieldChoice=new JComboBox();
		String splitSelection=null;
		for (Field f: DBUtils.categoryFields(model.getDB()))
		{
			splitFieldChoice.addItem(f.getName());
			if (f==graph.splitField)
				splitSelection=f.getName();
		}
		controls.add(splitFieldChoice);
		splitFieldChoice.setBounds(x,y,cw,ch);
		y+=ch+3*pad;
		
		if (splitSelection!=null)
			splitFieldChoice.setSelectedItem(splitSelection);
		else if (getModel().getColorField()!=null)
			splitFieldChoice.setSelectedItem(getModel().getColorField().getName());
		splitFieldChoice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graph.redo();
			}});		
		
		JLabel showLabel=new JLabel("show");
		controls.add(showLabel);
		showLabel.setBounds(x,y,cw,ch);
		y+=ch+pad;
		
		numFieldChoice=new JComboBox();
		numFieldChoice.addItem("Number of events");
		final ArrayList<Field> valueFields=new ArrayList<Field>();
		for (Field f:model.getDB().getFields(Double.class))
		{
			numFieldChoice.addItem("Total: "+f.getName());
			numFieldChoice.addItem("Average: "+f.getName());
			valueFields.add(f);
		}
		controls.add(numFieldChoice);
		numFieldChoice.setBounds(x,y,cw,ch);
		
		boolean chosen=false;
		for (int i=0; i<numFieldChoice.getItemCount(); i++)
		{
			if (numFieldChoice.getItemAt(i).equals(graph.lastValueMenuChoice))
			{
				numFieldChoice.setSelectedIndex(i);
				chosen=true;
			}
		}
		if (!chosen)
		{
			Field size=getModel().getDB().getField(VirtualField.SIZE);
			if (size!=null)
				numFieldChoice.setSelectedItem("Total: "+size.getName());
		}
		numFieldChoice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				graph.redo();
			}});
		revalidate();
		repaint();
	}

	void reset()
	{
		makeTop();
		graph.redo();
		revalidate();
		repaint();
	}

	@Override
	protected void _note(TFEvent e) {
		if (e.affectsSchema())
			reset();
		else
			graph.redo();
		repaint();
	}

	@Override
	public String getName() {
		return "Bar Graph";
	}

	@Override
	protected void onscreen(boolean majorChange) {
		reset();
	}	
	
	class BarData
	{
		Object thing;
		double num;
		BarData(Object thing, double num)
		{
			this.thing=thing;
			this.num=num;
		}
	}
	
	class BarGraph extends JPanel
	{
		int numVals=0;
		int rowHeight=30;
		int barHeight=20;
		int labelX=10, barLeft=300, barRight;
		int topY=45;
		int numX=210;
		Field splitField, valueField;
		String lastValueMenuChoice;
		double min, max;
		
		void redo()
		{
			bars=new ArrayList<BarData>();
			splitField=getModel().getDB().getField((String)splitFieldChoice.getSelectedItem());
			if (splitField!=null)
			{
				int n=numFieldChoice.getSelectedIndex();
				
				if (n==0)
					agg=Aggregate.COUNT;
				else
					agg= n%2==1 ? Aggregate.TOTAL : Aggregate.AVERAGE;
				
				if (agg==Aggregate.COUNT)
				{
					Bag<String> bag=DBUtils.countValues(getModel().getActs(), splitField);
					for (String s: bag.list())
					{
						bars.add(new BarData(s, bag.num(s)));
					}
				}
				else
				{
					lastValueMenuChoice=(String)numFieldChoice.getSelectedItem();
					int colon=lastValueMenuChoice.indexOf(':');
					valueField=getModel().getDB().getField(lastValueMenuChoice.substring(colon+2));
					DoubleBag<String> bag=new DoubleBag<String>();
					for (Act a: getModel().getActs())
					{
						if (splitField.getType()==String.class)
							bag.add(a.getString(splitField), a.getValue(valueField));
						else
						{
							String[] tags=a.getTextList(splitField);
							for (String tag: tags)
								bag.add(tag, a.getValue(valueField));
						}
					}
					boolean isSum=agg==Aggregate.TOTAL;
					for (String s: bag.list(isSum))
					{
						bars.add(new BarData(s, isSum ? bag.num(s) : bag.average(s)));
					}
				}
			}
			revalidate();
			repaint();
		}
		
		public void paintComponent(Graphics g1)
		{
			Graphics2D g=(Graphics2D)g1;
			int w=getSize().width, h=getSize().height;
			g.setColor(Color.white);
			g.fillRect(0,0,w,h);
			TFModel model=getModel();
			Display display=model.getDisplay();
			
			if (display.emptyMessage(g, model))
				return;
			
			if (bars==null)
				return;

			if (bars.size()==0)
			{
				g.setColor(Color.gray);
				g.drawString("(No data selected.)", 10,30);
				return;
			}
			
			int n=bars.size();
			max=bars.get(0).num;
			min=Math.min(0, bars.get(n-1).num);
			

			barRight=w-30;
			
			int zero=scaleX(0);
			boolean isInColor=(splitField!=null && getModel().getColorField()==splitField);

			// draw header
			int titleY=topY-15;
			g.setColor(Color.black);
			g.setFont(display.big());
			g.drawString(splitField.getName().toUpperCase(), labelX, titleY);
			String aggLabel=agg.toString();
			if (agg!=Aggregate.COUNT)
				aggLabel+=" "+valueField.getName().toUpperCase();
			g.drawString(aggLabel, barLeft, titleY);
			g.setFont(display.plain());
			FontMetrics fm=display.plainFontMetrics();
			// draw bars
			
			for (int i=0; i<n; i++)
			{
				int y=topY+i*rowHeight;
				int ty=y+barHeight;
				BarData data=bars.get(i);
				
				Color c=null;
				
				g.setColor(Color.gray);
				
				// label value
				boolean missing=data.thing==null || (data.thing.toString().length()==0);
				String label=missing ? "[missing]" :
					display.format(display.toString(data.thing),25,false);
				if (isInColor)
				{
					g.setColor(missing ? Color.gray : display.makeColor(data.thing.toString()));
					display.makeColor(label);
				}
				g.drawString(label, labelX, ty);
				
				// label number
				String numLabel=display.format(data.num);
				g.drawString(numLabel, numX+70-fm.stringWidth(numLabel), ty);
				
				// draw bar.
				g.setColor(missing ? Color.lightGray : (isInColor ? c: Display.barColor));
				int x=scaleX(data.num);
				int a=Math.min(x, zero);
				int b=Math.max(x, zero);
				g.fillRect(a, y+5, b-a, barHeight);
			}			
		}
		
		int scaleX(double x)
		{
			if (max==min)
				return barLeft;
			return (int)(barLeft+(barRight-barLeft)*(x-min)/(max-min));
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(400, 100+rowHeight*(bars==null ? 0 : bars.size()));
		}
	}
}
