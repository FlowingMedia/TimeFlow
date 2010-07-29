package timeflow.format.file;

// This is meant to be a repository for different
// types of import functions, arranged by file extension.

// We currently do not import anything but the standard file type.
// There actually is some code that will import from JSON/XML SIMILE
// timelines, but we have removed it from this release to simplify
// both the application and because it would mean redistributing additional
// third-party libraries.
public class FileExtensionCatalog {

	public static Import get(String fileName)
	{
		/*
		// not in this release...
		// but contact us if you'd like to see this.
		// we took out the SIMILE import material as too "techie"
		// for the first release!
		 
		if (fileName.endsWith("xml"))
			return new SimileXMLFormat();
		if (fileName.endsWith("json"))
			return new SimileJSONFormat();
			*/
		return new TimeflowFormat();
	}
}
