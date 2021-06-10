package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model
{
	private ImdbDAO dao;
	private Map<Integer, Actor> attori; //tutti gli attori nel db 
	private Map<Integer, Actor> vertici; //attori dato un genere di film
	private Graph<Actor, DefaultWeightedEdge> grafo; 
	
	public Model()
	{
		this.dao = new ImdbDAO();
		this.attori = new HashMap<>(); 
		this.dao.listAllActors(this.attori); 
	}
	
	public Collection<String> getGenres()
	{
		return this.dao.getGenres();
	}
	public Collection<Actor> getActors(String genre)
	{
		this.vertici = new HashMap<>(); 
		this.dao.listAllActors(this.attori);  
		for (Integer id : this.dao.getActorsInGenre(genre))
			if(this.attori.containsKey(id))
			{
				Actor a = this.attori.get(id); 
				vertici.put(id, a); 
			}
		ArrayList<Actor> result = new ArrayList<>(this.vertici.values());
		result.sort((a1,a2)->a1.getLastName().compareTo(a2.getLastName()));
		return result; 
	}
	
	public void creaGrafo(String genre)
	{
		// ripulisco mappa e grafo
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		/// vertici 
		this.getActors(genre);
		Graphs.addAllVertices(this.grafo, this.vertici.values()); 
		
		/// archi
		List<Adiacenza> adiacenze = new ArrayList<>(this.dao.getAdiacenze(genre));
		for (Adiacenza a : adiacenze)
		{
			//recupero gli Oggetti dalla chiave della mappa e faccio controlli di esistenza
			Actor a1 = this.attori.get(a.getA1());
			Actor a2 = this.attori.get(a.getA2());
			if (a1 != null && a2 != null)
				Graphs.addEdge(this.grafo, a1, a2, a.getPeso());
		}
	}
	public int getNumVertici()
	{
		return this.grafo.vertexSet().size();
	}
	public int getNumArchi()
	{
		return this.grafo.edgeSet().size();
	}
	public Collection<Actor> getVertici()
	{
		return this.grafo.vertexSet();
	}
	public Collection<DefaultWeightedEdge> getArchi()
	{
		return this.grafo.edgeSet();
	}
	
	public Collection<Actor> getRaggiungibili(Actor partenza)
	{
		//VERSIONE 1
//		ArrayList<Actor> result = new ArrayList<>(); 
//		BreadthFirstIterator<Actor, DefaultWeightedEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza);
//		while(bfv.hasNext())
//		{
//			Actor a = bfv.next(); 
//			if(!result.contains(a))
//				result.add(a); 
//		}
//		result.sort((a1,a2)->a1.getLastName().compareTo(a2.getLastName()));
//		result.remove(partenza);
		//VERSIONE 2
		ConnectivityInspector<Actor, DefaultWeightedEdge> insp = new ConnectivityInspector<>(this.grafo); 
		List<Actor> result = new ArrayList<>(insp.connectedSetOf(partenza)); 
		result.sort((a1,a2)->a1.getLastName().compareTo(a2.getLastName()));
		result.remove(partenza);
//		System.out.println(result.size());
		return result;
	}
	
	
	///SIMULAZIONE 
	
	public String getResultSim()
	{
		String s = "\n"; 
		for(int i=1; i<=this.eventi.size(); i++)
		{
			s += "GIORNO " + i + ": "; 
			if(eventi.get(i-1) != null)
				s += eventi.get(i-1).toString(); 
			else 
				s += " *pausa*;\n"; 
		}
		s += "\nTOT PAUSE: " + this.pause; 
		return s; 
	}
	
	private Integer giorni; //n
	private Integer pause = 0; //n
	List<Actor> eventi; 
	public void simula(int n)
	{
		this.giorni = n;
		List<Actor> attori = new ArrayList<>(this.vertici.values());
		eventi = new ArrayList<>(); 
		//genero eventi per n giorni 
		for(int g = 0; g < this.giorni; g++)
		{
			//primo giorno 
			if(g == 0) 
			{
				eventi.add(g, this.attoreCasuale(eventi)); 
			}
			else
			{
				if(eventi.get(eventi.size()-1) == null) //se ha fatto una pausa
				{
					Actor casuale = this.attoreCasuale(eventi);
					eventi.add(g, casuale); 
//					System.out.println("ATTORE CASUALE: " + casuale);
					pause++; 
					continue; 
				}
				
				double prob = Math.random()*100; 
				if(prob < 40.0)
				{
					Actor consigliato = this.attoreConsigliato(eventi); 
					if (consigliato != null)
					{
						eventi.add(g, consigliato); 
//						System.out.println("ATTORE CONSIGLIATO: " + consigliato);
					}
					else 
					{
						Actor casuale = this.attoreCasuale(eventi);
//						System.out.println("ATTORE CONSIGLIATO (CASUALE): " + casuale);
					}
				}
				else // > 60.0
				{
					Actor casuale = this.attoreCasuale(eventi);
					eventi.add(g, casuale); 
//					System.out.println("ATTORE CASUALE: " + casuale);
				}
				
				//confronto genere
				Actor ultimo = eventi.get(eventi.size()-1);
				Actor penultimo = eventi.get(eventi.size()-2);
				if(this.confrontaGenere(ultimo, penultimo))
				{
					g++; 
					eventi.add(g,null);  
				}
			}
		}
//		System.out.println("\nEVENTI: " + eventi + "\nPAUSE : " + this.pause);
	}
	
	//metodi
	private Actor attoreCasuale(List<Actor> eventi)
	{ 
		List<Actor> actors = new ArrayList<>(this.vertici.values());
		actors.removeAll(eventi); 		
		Actor intervistato = actors.get((int) (Math.random() * actors.size())); 
		return intervistato;
	}
	private Actor attoreConsigliato(List<Actor> eventi)
	{ 
		Actor ultimo = eventi.get(eventi.size()-1);
		List<Actor> vicini = new ArrayList<>(Graphs.neighborListOf(this.grafo, ultimo));
		List<Actor> viciniBest = new ArrayList<>();
		double gradoMax = 0.0; 
		Actor consigliato = null; 
		
		if(vicini.isEmpty())
			return null; 
		
		for (Actor a : vicini)
		{
			double grado = this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, a));
			if(grado > gradoMax)
			{
				gradoMax = grado; 
				consigliato = a;
			}
		}
		for (Actor a : vicini)
		{
			double grado = this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, a));
			if(grado == gradoMax) 
				viciniBest.add(a);
		}
		if(viciniBest.size() > 1)
			consigliato = viciniBest.get((int)(Math.random()*viciniBest.size()));
		
		return consigliato; 
	}
	private boolean confrontaGenere(Actor ultimo, Actor penultimo)
	{
		return ultimo.getGender().equals(penultimo.getGender()); 
	}
}
