/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.imdb;

import java.net.URL;
import java.util.ResourceBundle;

import javax.management.RuntimeErrorException;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController
{
	private Model model;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="btnCreaGrafo"
	private Button btnCreaGrafo; // Value injected by FXMLLoader

	@FXML // fx:id="btnSimili"
	private Button btnSimili; // Value injected by FXMLLoader

	@FXML // fx:id="btnSimulazione"
	private Button btnSimulazione; // Value injected by FXMLLoader

	@FXML // fx:id="boxGenere"
	private ComboBox<String> boxGenere; // Value injected by FXMLLoader

	@FXML // fx:id="boxAttore"
	private ComboBox<Actor> boxAttore; // Value injected by FXMLLoader

	@FXML // fx:id="txtGiorni"
	private TextField txtGiorni; // Value injected by FXMLLoader

	@FXML // fx:id="txtResult"
	private TextArea txtResult; // Value injected by FXMLLoader

	@FXML void OnGenreSelected(ActionEvent event)
	{
		this.btnCreaGrafo.setDisable(false);	
	}

	@FXML void OnActorSelected(ActionEvent event)
	{
		try
		{
			this.btnSimili.setDisable(false);			
			this.btnSimulazione.setDisable(false);		 
		}
		catch (Exception e)
		{
			this.txtResult.appendText("\nERRORE RELATIVO ATTORE!");
			throw new RuntimeException("ERRORE INIZIALIZZAZIONE" + e);
		}
	}
	
	@FXML void doCreaGrafo(ActionEvent event)
	{
		this.txtResult.clear();
		this.boxAttore.setDisable(false);
		try
		{
			String genre = this.boxGenere.getValue();
			if (genre == null)
				throw new Exception();
			this.boxAttore.getItems().clear();
			this.boxAttore.getItems().addAll(this.model.getActors(genre));
			
			this.model.creaGrafo(genre);
			
			this.txtResult.appendText(String.format("Grafo Creato grazie al cielo!\n#Vertici: %d\n#Archi: %d", this.model.getNumVertici(),this.model.getNumArchi()));
		}
		catch (Exception e)
		{
			this.txtResult.appendText("\nERRORE RELATIVO GRAFO!");
			throw new RuntimeException("ERRORE INIZIALIZZAZIONE" + e);
		}
	}

	@FXML void doAttoriSimili(ActionEvent event)
	{
		try
		{
			Actor a = this.boxAttore.getValue(); 
			if (a != null)
			{
				this.txtResult.appendText("\n\nSIMILI A: " + a + this.model.getRaggiungibili(a));
			}
			else return; 
		}
		catch (Exception e)
		{
			this.txtResult.appendText("\nERRORE RELATIVO ATTORI SIMILI!");
			throw new RuntimeException("ERRORE INIZIALIZZAZIONE" + e);
		}
	}
	
	@FXML void doSimulazione(ActionEvent event)
	{
		try
		{
			int n = Integer.parseInt(this.txtGiorni.getText());
			this.model.simula(n);
			this.txtResult.appendText("\n\n\n****SIMUALZIONE:\n" + this.model.getResultSim());
		}
		catch (Exception e)
		{
			this.txtResult.appendText("\nERRORE SIMULAZIONE");
			e.printStackTrace();
		}
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize()
	{
		assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnSimili != null : "fx:id=\"btnSimili\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnSimulazione != null : "fx:id=\"btnSimulazione\" was not injected: check your FXML file 'Scene.fxml'.";
		assert boxGenere != null : "fx:id=\"boxGenere\" was not injected: check your FXML file 'Scene.fxml'.";
		assert boxAttore != null : "fx:id=\"boxAttore\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtGiorni != null : "fx:id=\"txtGiorni\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
	}

	public void setModel(Model model)
	{
		this.model = model;
		try
		{
			this.boxGenere.getItems().addAll(this.model.getGenres());
		}
		catch (Exception e)
		{
			throw new RuntimeException("ERRORE INIZIALIZZAZIONE" + e);
		}
	}
}
