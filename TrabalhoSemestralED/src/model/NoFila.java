package model;

public class NoFila <T>{
	T dado;
	NoFila<T> proximo;
	
	@Override
	public String toString() {
		
		return "Dado: "+dado;
	}
	
	
}
