package timeflow.format.file;

import java.io.File;
import timeflow.data.db.ActDB;

public interface Import {
	public String getName();
	public ActDB importFile(File file) throws Exception;
}
