package it.polito.tdp.imdb.model;

public class Adiacenza
{
	private Integer a1;
	private Integer a2;
	private Integer peso;
	public Adiacenza(Integer a1, Integer a2, Integer peso)
	{
		this.a1 = a1;
		this.a2 = a2;
		this.peso = peso;
	}
	public Integer getA1()
	{
		return a1;
	}
	public Integer getA2()
	{
		return a2;
	}
	public Integer getPeso()
	{
		return peso;
	}
	@Override public String toString()
	{
		return "Adiacenza [a1=" + a1 + ", a2=" + a2 + ", peso=" + peso + "]";
	}
	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a1 == null) ? 0 : a1.hashCode());
		result = prime * result + ((a2 == null) ? 0 : a2.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Adiacenza other = (Adiacenza) obj;
		if (a1 == null)
		{
			if (other.a1 != null) return false;
		}
		else if (!a1.equals(other.a1)) return false;
		if (a2 == null)
		{
			if (other.a2 != null) return false;
		}
		else if (!a2.equals(other.a2)) return false;
		return true;
	} 
}
