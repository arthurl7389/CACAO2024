package abstraction.eq2Producteur2;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Feve;

// Toutes les variables de poids de cacao sont en TONNES 

public abstract class Producteur2_Plantation extends Producteur2_MasseSalariale {
	protected double nb_hectares_max;
	protected double nb_hectares_actuel;
	protected double prix_plantation_hectare;
	protected double nb_nouveau_hectares; // hectares nouvellement plantés sur 2 semaines

	protected int qualite;
	protected double pourcentage_HQ = 0.02;
	protected double pourcentage_MQ = 0.38;
	protected double pourcentage_BQ = 0.6;
	
	protected double rend_pest_BQ = 0.9;
	protected double rend_pest_MQ = 0.85;
	protected double rend_pest_HQ = 0.80;
	protected double rend_no_pest_HQ = 0.72;
	
	public Producteur2_Plantation() {
		//initialisation EFFICACE
		this.nb_hectares_actuel=5000000.0;
		this.nb_hectares_max=5000000.0*10;
		this.prix_plantation_hectare=500.0;
	} 

	public double getNb_hectares_max() {
		return this.nb_hectares_max;
	}

	public void setNb_hectares_max(double nb_hectares_max) {
		this.nb_hectares_max = nb_hectares_max;
	}

	public double getNb_hectares_actuel() {
		return nb_hectares_actuel;
	}

	public void setNb_hectares_actuel(double nb_hectares_actuel) {
		this.nb_hectares_actuel = nb_hectares_actuel;
	}

	public double getPrix_plantation_hectare() {
		return this.prix_plantation_hectare;
	}

	public void setPrix_plantation_hectare(int prix_plantation_hectare) {
		this.prix_plantation_hectare = prix_plantation_hectare;
	}
	
	public double getPourcentage_HQ() {
		return this.pourcentage_HQ;
	}
	
	public void setPourcentage_HQ(double pourcentage_HQ) {
		this.pourcentage_HQ = pourcentage_HQ;
	}

	public double getPourcentage_MQ() {
		return pourcentage_MQ;
	}

	public void setPourcentage_MQ(double pourcentage_MQ) {
		this.pourcentage_MQ = pourcentage_MQ;
	}

	public double getPourcentage_BQ() {
		return pourcentage_BQ;
	}

	public void setPourcentage_BQ(double pourcentage_BQ) {
		this.pourcentage_BQ = pourcentage_BQ;
	}
	
	public double getNb_nouveau_hectares() {
		return nb_nouveau_hectares;
	}

	public void setNb_nouveau_hectares(int nb_nouveau_hectares) {
		this.nb_nouveau_hectares = nb_nouveau_hectares;
	}
	
	public void initialiser() {
		super.initialiser();
	}
		
	public void planter(double nb_hectares) {
		if (getNb_hectares_actuel() + nb_hectares > getNb_hectares_max()) { //achat impossible
			this.journal.ajouter("on ne peut pas acheter plus de terrain.");
			return;
		}
		else { 
	 		setNb_hectares_actuel(getNb_hectares_actuel() + nb_hectares);
			// On embauche le même nombre de personnes que d'hectares car 1 hectare = 1 employé
			// 90% des personnes embauchées sont embauchées en tant qu'employé non-équitable
			int nb_employes = (int)(nb_hectares*0.9);
			this.embauche((int) nb_employes, "adulte");
			this.embauche((int) nb_hectares - nb_employes, "adulte équitable");
		}
	}
	
	public long production_cacao() { // retourne la production actuelle de cacao sur 2 semaines en kg
		long v =((long) getNb_hectares_actuel())/52; // 52 = 0.5 (tonnes) / 26 (tours de jeu en un an)
		return v;
	}
	public double production_HQ() { // retourne la production de cacao de haute qualité sur 2 semaines en kg
		return production_cacao()* getPourcentage_HQ();
	}
	public double production_BQ() { // retourne la production de cacao de basse qualité sur 2 semaines en kg
		return production_cacao() * getPourcentage_BQ();
	}
	public double production_MQ() { // retourne la production de cacao de moyenne qualité sur 2 semaines en kg
		return production_cacao() * getPourcentage_MQ();
	}
	
	// Retourne la production de cacao BQ, MQ et HQ après calculs des rendements en kilos
	public double get_prod_no_pest_HQ() { 
		return this.production_HQ() * rend_no_pest_HQ; //feve HQ_BE
	}
	public double get_prod_pest_HQ() {
		return this.production_HQ() * rend_pest_HQ; //feve HQ_E et HQ=0
	}
	public double get_prod_pest_MQ() {
		return this.production_MQ() * rend_pest_MQ; //feve MQ=98%*get_prod_pest et MQ_E=2%*get_prod_pest
	}
	public double get_prod_pest_BQ() {
		return this.production_BQ() * rend_pest_BQ; //feve BQ
	}
	public void nouveau_stock() { // ajoute la production sur 2 semaines aux stocks
		double total = production_BQ() + production_MQ() + production_HQ();
		ajout_stock(Feve.F_BQ, production_BQ());
		ajout_stock(Feve.F_MQ, production_MQ());
		ajout_stock(Feve.F_HQ_E, production_HQ());
	}
	
	public void modifie_prodParStep() {
	    this.prodParStep.put(Feve.F_HQ, production_HQ());
	    this.prodParStep.put(Feve.F_MQ, production_MQ());
	    this.prodParStep.put(Feve.F_BQ, production_BQ());
	}
	
	public double cout_plantation() {
		double res = getNb_nouveau_hectares() * getPrix_plantation_hectare();
		setNb_nouveau_hectares(0);
		return res;
	}	

	/* Cette fonction sert à implémenter notre stratégie
	 * Si notre stock est vide à la fin d'un tour cela implique que nous avons vendu l'intégralité
	 * de notre production. Il peut être utile d'acheter de nouveaux hectares pour produire plus de cacao.
	 */
	public void next_plantation() {
		// On place dans le stock tout ce qu'on produit en un tour
		this.nouveau_stock();
		
		// On décide si on achète de nouveaux hectares
		if (getStockTotal(this.cryptogramme) <= 0.0) {
			if (nb_hectares_actuel * 1.02 > nb_hectares_max) {
				planter(nb_hectares_max - nb_hectares_actuel);
			}
			planter((int) (nb_hectares_actuel * 0.02)); //on replante 2% de la plantation actuel
			setNb_nouveau_hectares((int) (nb_hectares_actuel * 0.02));
		}
	}
} 
// 1hectare = 500kg / an cacao
// implémenter la qualité 

// B219