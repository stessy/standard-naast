/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package standardNaast.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import standardNaast.entities.Personne;

/**
 *
 * @author stessy
 */
public class PersonneFacade extends AbstractFacade<Personne> {
	@PersistenceContext(unitName = "StandardNaastPeristenceUnit")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return this.em;
	}

	public PersonneFacade() {
		super(Personne.class);
	}

}
