package standardNaast;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.wildfly.WildFly8xInstalledLocalContainer;
import org.codehaus.cargo.container.wildfly.WildFly8xStandaloneLocalConfiguration;

public class Launcher {

	public static void main(final String[] args) throws Exception {
		if ((args.length == 0) || "start".equalsIgnoreCase(args[0])) {
			Launcher.startContainer();
		} else {
			Launcher.stopContainer();
		}
	}

	private static void startContainer() {
		InstalledLocalContainer container = Launcher.getContainer();
		container.start();
	}

	private static void stopContainer() {
		InstalledLocalContainer container = Launcher.getContainer();
		container.stop();
	}

	private static InstalledLocalContainer getContainer() {
		Deployable deployable = new WAR(
				"D:/SkyDrive/Standard_Naast/dev/standardNaast-web.war");
		LocalConfiguration configuration = new WildFly8xStandaloneLocalConfiguration(
				"D:/SkyDrive/Standard_Naast/dev/server/configuration");
		configuration.addDeployable(deployable);

		InstalledLocalContainer container = new WildFly8xInstalledLocalContainer(
				configuration);
		container
				.setHome("D:/SkyDrive/Standard_Naast/dev/server/wildfly-8.1.0.Final");

		return container;
	}
}
