package de.onto_med.jena_owlapi_integration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
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
	
	/**
	 * Creates a new Ontology Manager.
	 * @param path OWL file path
	 * @param iri Ontology IRI
	 * @param overwrite overwrites the file if true
	 */
	public OntologyManager(String path, String iri, Boolean overwrite) {
		this.path = path;
		this.iri  = iri + (iri.matches(".*#$") ? "" : "#");
		
		model = ModelFactory.createOntologyModel();
		
		File file = new File(path);
		if (file.exists() && !overwrite) {
			System.out.println("Appending data to existing output file.");
			model.read(FileManager.get().open(path), null);
		} else {
			if (file.exists() && overwrite) {
				System.out.println("Overwriting existing output file.");
				file.delete();
			}
			model.createOntology(iri);
		}
	}
	
	/**
	 * Creates a new Ontology Manager.
	 * @param path OWL file path
	 * @param iri Ontology IRI
	 */
	public OntologyManager(String path, String iri) {
		this(path, iri, false);
	}
	
	/**
	 * Returns the jena API OntModel.
	 * @return OntModel
	 */
	public OntModel getModel() {
		return model;
	}
	
	/**
	 * Saves the Ontology with OWLAPI in the specified location.
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 */
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
		
		manager.saveOntology(ontology, new RDFXMLDocumentFormat());
	}
}
