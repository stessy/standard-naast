package standardNaast;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.standardNaast.view.member.MembersPanel;
import com.standardNaast.view.member.MembersTable;
import com.standardNaast.view.types.MembersPanelQualifier;

@ApplicationScoped
public class ComponentFactory {

	@Produces
	public MembersTable getMembersTable(final InjectionPoint injectionPoint) {
		return new MembersTable();
	}

	@Produces
	public JPanel getPanel(final InjectionPoint injectionPoint) {
		return new JPanel();
	}

	@Produces
	public JFrame getJFrame(final InjectionPoint injectionPoint) {
		return new JFrame();
	}

	@Produces
	@MembersPanelQualifier
	public MembersPanel getMembersPanel(final InjectionPoint injectionPoint) {
		return new MembersPanel();
	}
}
