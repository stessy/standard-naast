package standardNaast.beans;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import standardNaast.entities.Benevolat;

@Controller("memberBenevolats")
@Scope("session")
public class MemberBenevolatBean implements Serializable {

	private static final long serialVersionUID = 7952399282469201994L;

	private List<Benevolat> benevolats;

	private Benevolat selectedBenevolat;

	public List<Benevolat> getBenevolats() {
		return this.benevolats;
	}

	public void setBenevolats(List<Benevolat> benevolats) {
		this.benevolats = benevolats;
	}

	public Benevolat getSelectedBenevolat() {
		return this.selectedBenevolat;
	}

	public void setSelectedBenevolat(Benevolat selectedBenevolat) {
		this.selectedBenevolat = selectedBenevolat;
	}
}
