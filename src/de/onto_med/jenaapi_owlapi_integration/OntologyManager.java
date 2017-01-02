package de.onto_med.jenaapi_owlapi_integration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


/**
 * This class can be used to write or append axioms to an owl file, by using jena api.
 * Calling save() writes all changes into the specified file and converts the file into neatly OWL-API format.
 * @author Christoph Beger
 *
 */
public class OntologyManager {

	private String path;
	private String iri;
	private OntModel model;
	
	public OntologyManager(String path, String iri) {
		this.path = path;
		this.iri  = iri;
		
		model = ModelFactory.createOntologyModel();
		
		if (new File(path).exists()) {
			System.out.println("Appending data to existing output file.");
			model.read(FileManager.get().open(path), null);
		} else {
			model.createOntology(iri);
		}
	}
	
	public OntModel getModel() {
		return model;
	}
	
	public void save() throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		File file = new File(path);
		FileOutputStream stream = new FileOutputStream(file);
		model.setNsPrefix("", iri);
		model.write(stream, "RDF/XML", null);
		loadAndSaveWithOwlApi();
		System.out.println("\nSaved ontologie in '" + file.getAbsolutePath() + "'.");
	}
	
	private void loadAndSaveWithOwlApi() throws OWLOntologyCreationException, OWLOntologyStorageException {
		File file = new File(path);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		manager.saveOntology(ontology);
	}
}
