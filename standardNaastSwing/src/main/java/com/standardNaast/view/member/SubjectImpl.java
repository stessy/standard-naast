package com.standardNaast.view.member;

import java.util.ArrayList;
import java.util.List;

import standardNaast.entities.Personne;

public class SubjectImpl implements Subject {

	private final List<Observer> observers = new ArrayList<>();

	@Override
	public void registerObserver(final Observer observer) {
		this.observers.add(observer);
	}

	@Override
	public void removeObserver(final Observer observer) {
		this.observers.remove(observer);

	}

	@Override
	public void notifyObservers(final Personne personne) {
		for (final Observer observer : this.observers) {
			observer.update(personne);
		}
	}

}
