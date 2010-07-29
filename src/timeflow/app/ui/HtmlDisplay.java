package timeflow.app.ui;

import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

public class HtmlDisplay {
	public static JEditorPane create()
	{
		JEditorPane p = new JEditorPane();
		p.setEditable(false);
		p.setContentType("text/html");
		
		Font font = UIManager.getFont("Label.font");
        String bodyRule = "body { font-family: "+font.getFamily()+"; "+
                "font-size: " + font.getSize() + "pt; }";
        StyleSheet styles=((HTMLDocument)p.getDocument()).getStyleSheet();
        styles.addRule(bodyRule);
        return p;
	}
}
