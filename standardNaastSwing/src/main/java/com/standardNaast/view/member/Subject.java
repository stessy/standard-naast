package com.standardNaast.view.member;

import standardNaast.entities.Personne;

public interface Subject {

	public void registerObserver(Observer observer);

	public void removeObserver(Observer observer);

	public void notifyObservers(Personne personne);

}
