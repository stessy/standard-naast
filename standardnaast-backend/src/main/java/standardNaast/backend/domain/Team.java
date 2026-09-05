package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "EQUIPE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEAM_SEQ_GEN")
    @SequenceGenerator(name = "TEAM_SEQ_GEN", sequenceName = "TEAM_SEQ", allocationSize = 50)
    @Column(name = "EQUIPE_ID")
    private Long id;

    @Column(name = "NOM_EQUIPE", nullable = false, length = 150)
    private String name;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
