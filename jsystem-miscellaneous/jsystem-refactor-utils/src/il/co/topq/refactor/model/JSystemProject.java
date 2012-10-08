package il.co.topq.refactor.model;

import java.io.File;
import java.util.List;

/**
 * @author Itai Agmon
 */

public class JSystemProject {
	
	

//	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private JSystemFilesCollector filesCollector;

	public JSystemProject(File projectDir) {
		filesCollector = new JSystemFilesCollector(projectDir);
	}

	public List<ScenarioPropertiesFile> getScenariosPropertiesFiles() {
		return filesCollector.getScenariosPropertiesFiles();
	}

	public List<ScenarioXMLFile> getScenariosXMLFiles() {
		return filesCollector.getScenariosXMLFiles();
	}

	public List<ScenarioPairFiles> getProjectScenariosFiles() {
		return filesCollector.getProjectScenariosFiles();
	}

}
