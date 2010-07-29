package timeflow.format.file;

import timeflow.model.*;

import java.io.BufferedWriter;

public interface Export {
	public String getName();
	public void export(TFModel model, BufferedWriter out) throws Exception;
}
