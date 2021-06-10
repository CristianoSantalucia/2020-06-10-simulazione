package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO
{

	public void listAllActors(Map<Integer, Actor> attori)
	{
		String sql = "SELECT * FROM actors";
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{
				if(!attori.containsKey(res.getInt("id")))
				{
					Actor a = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
							res.getString("gender"));
					attori.put(a.getId(), a);
				}
			}
			conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public List<Movie> listAllMovies()
	{
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), res.getInt("year"),
						res.getDouble("rank"));

				result.add(movie);
			}
			conn.close();
			return result;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public List<Director> listAllDirectors()
	{
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{

				Director director = new Director(res.getInt("id"), res.getString("first_name"),
						res.getString("last_name"));

				result.add(director);
			}
			conn.close();
			return result;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public List<String> getGenres()
	{
		String sql = "SELECT DISTINCT(g.genre) AS genre "
				+ "FROM movies_genres AS g "
				+ "ORDER BY g.genre ";

		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
			{
				result.add(res.getString("genre"));
			}
			conn.close();
			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	public List<Integer> getActorsInGenre(String genre)
	{
		String sql = "SELECT DISTINCT(r.actor_id) AS aId "
					+ "FROM roles AS r, movies AS m, movies_genres AS mg "
					+ "WHERE r.movie_id = m.id AND mg.movie_id = m.id "
					+ "		AND mg.genre = ? ";

		List<Integer> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genre);
			ResultSet res = st.executeQuery();
			while (res.next())
			{
				result.add(res.getInt("aId"));
			}
			conn.close();
			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	} 
	public List<Adiacenza> getAdiacenze(String genre)
	{
		String sql = "SELECT r1.actor_id AS a1, r2.actor_id AS a2, COUNT(*) AS peso "
					+ "FROM roles AS r1, roles AS r2, movies AS m "
					+ "WHERE m.id IN (SELECT mg.movie_id "
					+ "					FROM movies_genres AS mg "
					+ "					WHERE mg.genre = ? ) "
					+ "		AND r1.actor_id < r2.actor_id "
					+ "		AND r1.movie_id = r2.movie_id "
					+ "		AND m.id = r1.movie_id "
					+ "GROUP BY a1, a2 "
					+ "HAVING COUNT(*) > 0";

		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try
		{
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genre);
			ResultSet res = st.executeQuery();
			while (res.next())
			{
				Adiacenza a = new Adiacenza(res.getInt("a1"), res.getInt("a2"), res.getInt("peso")); 
				result.add(a);
			}
			conn.close();
			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	} 
}