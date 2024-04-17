package abstraction.eq3Producteur3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.IProduit;


public abstract class Producteur3Acteur implements IActeur {
	
	protected int cryptogramme;
	protected Journal journal;
	protected Journal journal_bourse;
	protected Journal journal_contrat_cadre;
	protected HashMap<IProduit,Integer> stocks;
	//passable en parametre/indicateurs
	private double coutUnitaireProductionBQ = 1.0;
    private double coutUnitaireProductionMQ = 1.5;
    private double coutUnitaireProductionHQ = 2.0;
    //creation d'un tableau de variables qui donne la production pour chaque type de feve @alexis
    protected HashMap<Feve, Variable> prodfeve;
    //creation d'un tableau de variables qui donne les ventes pour chaque type de feve @alexis
    protected HashMap<Feve, Variable> ventefeve;
    protected HashMap<Feve, Double> ventefevebourse;
    protected HashMap<Feve, Double> ventefevecadre;
    protected HashMap<Feve,HashMap<Integer,Double>> stockGammeStep;
    protected HashMap<Feve,HashMap<Integer,Double>> coutGammeStep;
    //abstract
    abstract HashMap<Feve,Double> quantite();
    abstract void setProdTemps(HashMap<Feve, Double> d0,HashMap<Feve, Double> d1);
    abstract HashMap<Feve,Double> maindoeuvre();
    
	public Producteur3Acteur() {
		this.journal = new Journal(this.getNom()+" journal",this);
		this.journal_bourse = new Journal(this.getNom()+" journal bourse",this);
		this.journal_contrat_cadre = new Journal(this.getNom()+" journal contrat cadre",this);
		this.prodfeve = new HashMap<Feve,Variable>();
		this.ventefeve = new HashMap<Feve,Variable>();
		this.ventefevebourse = new HashMap<Feve, Double>();
		this.ventefevecadre = new HashMap<Feve, Double>();
		for (Feve f : Feve.values()) {
			this.prodfeve.put(f,  new Variable("Prod "+f, this, 1.1));
			this.ventefeve.put(f,  new Variable("Vente "+f, this, 1.0));
			this.ventefevebourse.put(f, 0.2);
			this.ventefevecadre.put(f, 0.8);
		}
	}
	
	
	public void initialiser() {
		this.stocks = new HashMap<IProduit,Integer>();
		//On set les stocks
		/**
		 *Initialisation basée sur les quantités produites actuellement au Pérou
		 *On considère 2 steps de stocks
		 *surface:
		HQ_BE : 8.42 ha 
		HQ : 22.74 ha ; HQ_E : 7.58 ha  (Non Bio) 
		MQ : 47.57 ha ; MQ_E : 11.89 
		BQ : 134.775 ha 

		 * @author galem (Gabin)
		 */
		setQuantiteEnStock(Feve.F_BQ,7.58);
		setQuantiteEnStock(Feve.F_MQ,1.26);
		setQuantiteEnStock(Feve.F_MQ_E,0.316);
		setQuantiteEnStock(Feve.F_HQ,0.5685);
		setQuantiteEnStock(Feve.F_HQ_E,0.19);
		setQuantiteEnStock(Feve.F_HQ_BE,0.19);
		//
		//On set les productions
	
		HashMap<Feve,Double> d01 = new HashMap<Feve,Double>();
		d01.put(Feve.F_BQ, 3.79);
		d01.put(Feve.F_MQ, 2.527);		//80% de HQ est non équitable
		d01.put(Feve.F_MQ_E, 0.63);        //20% de MQ est équitable
		d01.put(Feve.F_HQ, 1.137);			//60% de HQ est ni bio ni équitable
		d01.put(Feve.F_HQ_E, 0.379);		//20% de HQ est équitable
		d01.put(Feve.F_HQ_BE, 0.3789);		//20% de HQ est bio équitable
		setProdTemps(d01,d01);
		//On set les variables coutGammeStep
		this.coutGammeStep = new HashMap<Feve,HashMap<Integer,Double>>();
		HashMap<Integer,Double> bq0 = new HashMap<Integer,Double>();
		bq0.put(0, 7.58);
		this.coutGammeStep.put(Feve.F_BQ, bq0);
		HashMap<Integer,Double> mq0 = new HashMap<Integer,Double>();
		mq0.put(0, 1.26);
		this.coutGammeStep.put(Feve.F_MQ, mq0);
		HashMap<Integer,Double> mqE0 = new HashMap<Integer,Double>();
		mqE0.put(0, 0.316);
		this.coutGammeStep.put(Feve.F_MQ_E, mqE0);
		HashMap<Integer,Double> hq0 = new HashMap<Integer,Double>();
		hq0.put(0, 0.5685);
		this.coutGammeStep.put(Feve.F_HQ, hq0);
		HashMap<Integer,Double> hqE0 = new HashMap<Integer,Double>();
		hqE0.put(0, 0.19);
		this.coutGammeStep.put(Feve.F_HQ_E, hqE0);
		HashMap<Integer,Double> hqBE0 = new HashMap<Integer,Double>();
		hqBE0.put(0, 0.19);
		this.coutGammeStep.put(Feve.F_HQ_BE, hqBE0);
		//On set les variables stockGammeStep
		this.stockGammeStep = new HashMap<Feve,HashMap<Integer,Double>>();
		HashMap<Integer,Double> bq00 = new HashMap<Integer,Double>();
		bq00.put(0, 7.58);
		this.stockGammeStep.put(Feve.F_BQ, bq00);
		HashMap<Integer,Double> mq00 = new HashMap<Integer,Double>();
		mq00.put(0, 1.26);
		this.stockGammeStep.put(Feve.F_MQ, mq00);
		HashMap<Integer,Double> mqE00 = new HashMap<Integer,Double>();
		mqE00.put(0, 0.316);
		this.stockGammeStep.put(Feve.F_MQ_E, mqE00);
		HashMap<Integer,Double> hq00 = new HashMap<Integer,Double>();
		hq00.put(0, 0.5685);
		this.stockGammeStep.put(Feve.F_HQ, hq00);
		HashMap<Integer,Double> hqE00 = new HashMap<Integer,Double>();
		hqE00.put(0, 0.19);
		this.stockGammeStep.put(Feve.F_HQ_E, hqE00);
		HashMap<Integer,Double> hqBE00 = new HashMap<Integer,Double>();
		hqBE00.put(0, 0.19);
		this.stockGammeStep.put(Feve.F_HQ_BE, hqBE00);		
	}
	

	public String getNom() {// NE PAS MODIFIER
		return "EQ3";
	}
	
	public String toString() {// NE PAS MODIFIER
		return this.getNom();
	}

	////////////////////////////////////////////////////////
	//         En lien avec l'interface graphique         //
	////////////////////////////////////////////////////////

	protected abstract HashMap<Feve,Double> newQuantite();
	
	public void next() {
		//On gere nos intrants de production
		gestionStock(); 
		//On met a jour les variables GammeStep
		majGammeStep();
		this.journal.ajouter("etape="+Filiere.LA_FILIERE.getEtape());
		/**
		 * Implémentation des journaux spécifiques à la bourse et aux contrats cadres
		 * gagne en clarté
		 * @author galem (Gabin)
		 */
		this.journal_bourse.ajouter("etape="+Filiere.LA_FILIERE.getEtape());		
		this.journal_contrat_cadre.ajouter("etape="+Filiere.LA_FILIERE.getEtape());
		this.journal.ajouter("cout de stockage: "+Filiere.LA_FILIERE.getParametre("cout moyen stockage producteur").getValeur());
		//On paie les couts lies a la production et au stockage
		Filiere.LA_FILIERE.getBanque().payerCout(this, cryptogramme, "Production&Stockage", calculerCouts());
		//MaJ des quantites produites pour chaque type de feve: quantite() donne ce qui est produit et pret a la vente, @alexis
		for (Feve f : Feve.values()) {
			this.prodfeve.get(f).setValeur(this, quantite().get(f));
			this.ventefeve.get(f).setValeur(this, ventefevecadre.get(f)+ventefevebourse.get(f));
		}
		
	}

	public Color getColor() {// NE PAS MODIFIER
		return new Color(249, 230, 151); 
	}

	public String getDescription() {
		return "tiCao - Producteur 3";
	}

	// Renvoie les indicateurs
	public List<Variable> getIndicateurs() {
		List<Variable> res = new ArrayList<Variable>();
		for (Feve f : Feve.values()) {
			res.add(prodfeve.get(f));
		}
		return res;
	}

	

	// Renvoie les parametres
	public List<Variable> getParametres() {
		List<Variable> res = new ArrayList<Variable>();
		return res;
	}

	// Renvoie les journaux
	public List<Journal> getJournaux() {
		List<Journal> res = new ArrayList<Journal>();
		res.add(this.journal);
		res.add(this.journal_bourse);
		res.add(this.journal_contrat_cadre);
		return res;
	}

	////////////////////////////////////////////////////////
	//               En lien avec la Banque               //
	////////////////////////////////////////////////////////

	// Appelee en debut de simulation pour vous communiquer 
	// votre cryptogramme personnel, indispensable pour les
	// transactions.
	public void setCryptogramme(Integer crypto) {
		this.cryptogramme = crypto;
	}

	// Appelee lorsqu'un acteur fait faillite (potentiellement vous)
	// afin de vous en informer.
	public void notificationFaillite(IActeur acteur) {
		this.journal.ajouter("Faillite de l'acteur "+acteur.toString());	
	}

	// Apres chaque operation sur votre compte bancaire, cette
	// operation est appelee pour vous en informer
	public void notificationOperationBancaire(double montant) {
		this.journal.ajouter("Operation bancaire : "+montant+ " E");
	}
	
	// Renvoie le solde actuel de l'acteur
	protected double getSolde() {
		return Filiere.LA_FILIERE.getBanque().getSolde(Filiere.LA_FILIERE.getActeur(getNom()), this.cryptogramme);
	}

	////////////////////////////////////////////////////////
	//        Pour la creation de filieres de test        //
	////////////////////////////////////////////////////////

	// Renvoie la liste des filieres proposees par l'acteur
	public List<String> getNomsFilieresProposees() {
		ArrayList<String> filieres = new ArrayList<String>();
		return(filieres);
	}

	// Renvoie une instance d'une filiere d'apres son nom
	public Filiere getFiliere(String nom) {
		return Filiere.LA_FILIERE;
	}

	public double getQuantiteEnStock(IProduit p, int cryptogramme) {
		if (this.cryptogramme==cryptogramme) { // c'est donc bien un acteur assermente qui demande a consulter la quantite en stock
			//on renvoie la valeur (null => 0)
			if (this.stocks.get(p) != null) {
				return this.stocks.get(p);
			}
			else {
				return 0;
			}
		} else {
			return 0; // Les acteurs non assermentes n'ont pas a connaitre notre stock
		}
	}
	
	/**
	 * @author Arthur
	 */
	protected void setQuantiteEnStock(IProduit p, double stock) {
		//on set la valeur du stock ou la modifie si elle existe deja
		this.stocks.put(p,(int)stock);
	}
	/**
	 * @author mammouYoussef
	 */
	 protected double calculerCoutsStockage () {
	      double coutStockage = 0;
	      double cout=Filiere.LA_FILIERE.getParametre("cout moyen stockage producteur").getValeur();
	      for (Integer quantite : stocks.values()) {
	          coutStockage += quantite * cout  ;}
	      return coutStockage;
	      }
	
	 /**
	  * @author mammouYoussef
	  */		 
	 protected double calculerCoutsProduction() {
		    double coutProductionBQ = 0;
		    double coutProductionMQ = 0;
		    double coutProductionHQ = 0;
	
	    HashMap<Feve, Double> quantitesProduites = quantite();

	    for (Feve f : quantitesProduites.keySet()) {
	        double quantite = quantitesProduites.get(f); // Récupération de la quantité produite

	        // Calcul du coût de production pour la gamme de qualité concernée
	        if (f.getGamme() == Gamme.BQ) {
	            coutProductionBQ += quantite * coutUnitaireProductionBQ;
	        } else if (f.getGamme() == Gamme.MQ) {
	            
	            coutProductionMQ += quantite * coutUnitaireProductionMQ;
	        } else if (f.getGamme() == Gamme.HQ) {
	          
	            coutProductionHQ += quantite * coutUnitaireProductionHQ;
	        }
	    }
	    return coutProductionBQ + coutProductionMQ + coutProductionHQ;
	
        }
	 
	 /**
	  * @author mammouYoussef
	  */	
	 
	 protected double coutMaindoeuvre() {
		    // Calcule le coût de la main-d'œuvre en tenant compte des salaires des ouvriers

		    HashMap<Feve, Double> ouvriers = maindoeuvre();
		    double coutMaindoeuvre = 0;

		    // Pour chaque type de fève, calculer le coût de la main-d'œuvre en fonction du nombre d'ouvriers
		    // et ajuster le salaire selon que la fève est équitable ou non
		    for (Feve f : ouvriers.keySet()) {
		        double nbOuvriers = ouvriers.get(f);
		        double salaireOuvrier; 

		        // Déterminer le salaire en fonction du type de fève
		        if (f.isEquitable()) {
		            salaireOuvrier = 3.9; // Salaire pour l'equitable (bio ou non)
		        } else {
		            salaireOuvrier = 2.6; // Salaire standard pour les non équitable 
		        }

		        // Calculer le coût total pour tous les types de fève
		        coutMaindoeuvre += nbOuvriers * salaireOuvrier;
		    }

		    return coutMaindoeuvre;
		}
	
	 
	 protected double calculerCouts() {
		 return calculerCoutsProduction()+calculerCoutsStockage()+coutMaindoeuvre();
		 
	 }
	 
	 /**
	  * @author Arthur
	  * gestion des stocks pour les inputs de production (les outputs sont geres par les fonctions de ventes)
	  */
	 protected void gestionStock() {
		 HashMap<Feve,Double> prod = quantite();
		 for (Feve f : prod.keySet()) {
			 this.setQuantiteEnStock(f, this.getQuantiteEnStock(f, this.cryptogramme)+prod.get(f));
		 }
	 }
	 
	 /**
	  * @author Arthur
	  * Dans le but de s'assurer de ne pas vendre a perte, on regarde les couts de chaque feve par step et leurs quantites
	  *Cette fonction met a jour les variables associees
	  */
	 protected void majGammeStep() {
		 for (Feve f : stockGammeStep.keySet()) {
			//on ajoute la production du step
			 stockGammeStep.get(f).put(Filiere.LA_FILIERE.getEtape(), quantite().get(f));
		 //on ajoute les couts du step
			 if (f.isEquitable()) {
				 coutGammeStep.get(f).put(Filiere.LA_FILIERE.getEtape(), maindoeuvre().get(f)*3.9);
			 } else {
				 coutGammeStep.get(f).put(Filiere.LA_FILIERE.getEtape(), maindoeuvre().get(f)*2.6);
			 }
		//on regarde tous les steps pour prendre en compte les ventes sur les stocks et rapport de couts
			LinkedList<Integer> steps = new LinkedList<Integer>();
			steps.addAll(stockGammeStep.get(f).keySet());
			Collections.sort(steps);
			double venteF = ventefeve.get(f).getValeur();
			for (Integer step : steps) {
				double stockStep = stockGammeStep.get(f).get(step);
				//on met a jour les stocks en destockant les plus vieilles feves
				//on fait de meme avec les couts proportionnellement 
				if (stockStep > venteF) {
					stockGammeStep.get(f).put(step, stockStep-venteF);
					coutGammeStep.get(f).put(step, (stockStep-venteF)/stockStep*coutGammeStep.get(f).get(step));
				} else {
					venteF -= stockStep;
					stockGammeStep.get(f).remove(step);
					coutGammeStep.get(f).remove(step);
				}
			}
			for (Integer step : steps) {
				//On ajoute les frais de stockage si on a des stocks a ce step
				if (coutGammeStep.get(f).containsKey(step)) {
					coutGammeStep.get(f).put(step, coutGammeStep.get(f).get(step)+Filiere.LA_FILIERE.getParametre("cout moyen stockage producteur").getValeur()*stockGammeStep.get(f).get(step));			}
				}	
		 }
	 }
	 
	 /**
	  * @author Arthur
	  * La fonction renvoie le cout de revient pour une gamme et une quantite donnee par tonnes
	  * On vend en priorite les vieilles feves
	  */
	 protected double coutRevient(Feve f,double quantiteDem) {
		 if (quantiteDem <= 0) {
			 return 0.0;
		 } else {
			 double accu = 0.0;
			 //On veut destocker step par step
			 LinkedList<Integer> steps = new LinkedList<Integer>();
			 steps.addAll(stockGammeStep.get(f).keySet());
			 Collections.sort(steps);
			 for (Integer step : steps) {
				 double stockStep = stockGammeStep.get(f).get(step);
				 //On ajoute les couts de revient en proportion de la quantite demandee
				 if (stockStep > quantiteDem) {
					 accu += quantiteDem/stockStep * coutGammeStep.get(f).get(step);
				 } else {
					 accu += coutGammeStep.get(f).get(step);
					 quantiteDem -= stockStep;
				 }
			 }
			 this.journal_bourse.ajouter("acc"+accu);
			 this.journal_bourse.ajouter("q"+quantiteDem);
			 this.journal_bourse.ajouter("accQ"+accu/quantiteDem);
			 return accu/quantiteDem;
		 }
	 }
}