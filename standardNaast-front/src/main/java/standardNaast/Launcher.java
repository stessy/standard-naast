package standardNaast;

import java.io.BufferedReader;
import java.io.File;

import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

public class Launcher {

	public static void main(final String[] args) throws Exception {

		System.out.println("Working Directory = "
				+ System.getProperty("user.dir"));

		// Properties properties = new Properties();
		// properties.load(new FileInputStream(""));
		GlassFish glassfish = null;
		GlassFishRuntime glassfishRuntime = null;

		File configFile = new File(
				"D:\\SkyDrive\\Standard_Naast\\embeddedGlassfish\\domains\\standardNaastDomain\\config\\domain.xml");
		File war = new File(
				"C:\\Users\\stessy\\.m2\\repository\\com\\standardNaast\\standardNaast-front\\1.0-SNAPSHOT\\standardNaast-front-1.0-SNAPSHOT.war");
		try {
			glassfishRuntime = GlassFishRuntime.bootstrap();
			GlassFishProperties glassfishProperties = new GlassFishProperties();
			glassfishProperties.setConfigFileURI(configFile.toURI().toString());
			glassfishProperties.setConfigFileReadOnly(false);
			glassfish = glassfishRuntime.newGlassFish(glassfishProperties);
			glassfish.start();

			Deployer deployer = glassfish.getDeployer();
			deployer.deploy(war, "--force=true");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Press Enter to stop server");
		// wait for Enter
		new BufferedReader(new java.io.InputStreamReader(System.in)).readLine();
		try {
			if (glassfish != null) {
				glassfish.dispose();
			}
			if (glassfishRuntime != null) {
				glassfishRuntime.shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
