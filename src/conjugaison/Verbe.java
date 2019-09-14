package conjugaison;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Verbe {
	
	// les 2 attributs de la classe
	String verbe;
	int est_pronominal=0;
	int est_atone=0;
        int est_atone_a_double_consonne=0;//Pour que lors de l'affichage on affiche la deuxieme "t" en meme couleur que les terminaison 
	//
	MysqlConnexion connexion =new MysqlConnexion("verbes","root","");
	
	// le constructeur
		public Verbe(String verbe) {
			super();
			this.verbe = verbe.toLowerCase();
			}
                
                
        //Pour avoir le verbe

                public String getVerbe() {
                 return verbe;
		}

                /*la methode qui permet de se connecter a la base de donner: on se connectera
                une seule fois pour ne pas retablir le Driver a chaque fois ce qui augmente le temps d'execution */
                public void connecter() {
                    connexion.connect();
                }

		//Pour voir si c'est pronominal ou pas;
		public boolean est_pronominal(){
			if(verbe.substring(0,3).compareTo("se ")==0 || verbe.substring(0,2).compareTo("s'")==0) {
				this.est_pronominal=1;
				return true;
			}
			else {
				return false;
			}
		}
		
                
		// Dans le cas ou c'est pronominal: Pour enlever le "Se " ou le "s' "
		public void enlever() {
			if(verbe.substring(0,3).compareTo("se ")==0 ) {
			verbe=verbe.substring(3,verbe.length());
			}
			else
				verbe=verbe.substring(2,verbe.length());
		}
		
		/*
		 * Pour voir si une chaine se trouve dans la liste des chaines donnees. Cette fonction est necessaire pour savoir dans quelle
			table le verbe est stock�.
			Nous avons repartis les verbes en 6 tables; ceux qui debutent par:		
			[a,b,c]=>					table=verbe_abc;
			[d]=> 						table=verbe_d;
			[e]=>						table=verbe_e;
			[f,g,h,i,j,k,l,m,n]=>		table=verbe_fghijklmn;
			[o,p,q,r]=>					table=verbe_opqr;
			[s,t,u,v,w,x,y,z]=>  		table=verbe_stuvwxyz;
		 */
		private boolean exist_dans_liste(String a,String A[]) {
			int i;
			for(i=0;i<A.length;i++) {
				if(A[i].compareTo(a)==0)
					return true;
			}
			return false;
		}
		
		
		// Ici on va regarder dans notre base de donnees si le verbe existe ou pas. On travaille avec une connexion mysql;
		public boolean verbe_exist() {
			char premier_caractere;
			String table_choisie = null;
			premier_caractere=verbe.charAt(0);
			
			String liste_des_tables[][]= {{"a","b","c"},{"d"},{"e","é"},{"f","g","h","i","j","k","l","m","n"},{"o","p","q","r"},{"s","t","u","v","w","x","y","z"}};
			
			
			if(exist_dans_liste(Character.toString(premier_caractere),liste_des_tables[0])) {
				table_choisie="verbe_abc";
			}
			if(exist_dans_liste(Character.toString(premier_caractere),liste_des_tables[1])) {
				table_choisie="verbe_d";
			}
			if(exist_dans_liste(Character.toString(premier_caractere),liste_des_tables[2])) {
				table_choisie="verbe_e";
			}
			if(exist_dans_liste(Character.toString(premier_caractere),liste_des_tables[3])) {
				table_choisie="verbe_fghijklmn";
			}
			if(exist_dans_liste(Character.toString(premier_caractere),liste_des_tables[4])) {
				table_choisie="verbe_opqr";
			}
			if(exist_dans_liste(Character.toString(premier_caractere),liste_des_tables[5])) {
				table_choisie="verbe_stuvwxyz";
			}
			
			
			try{
					ResultSet resultat;
					System.out.println(table_choisie);
                                        String requete=new String("select verbes from "+table_choisie+" where verbes='"+verbe+"'");
					resultat=connexion.SQLSelect(requete);
					while(resultat.next()) {
						return true;
					}
					return false;
			}
			catch(SQLException e) {
				return false;
			}
		}
		
		// Ici on regarde si le verbe si le verbe est irregulier ou pas;
		public boolean est_irregulier() {
			try {
				ResultSet resultat;
				resultat=connexion.SQLSelect("select distinct  verbes from verbe_irregulier where verbes='"+verbe+"'");
				while(resultat.next()) {
					return true;
				}

			}
			catch(SQLException e) {
				return false;
			}
			return false;
		}
		// On doit conjuguer sachant que le verbe est irreguliers c-a-d extraire sa conjugaison de la base de donnee.
		public void conjuguer_irregulier() {
			try {
				ResultSet resultat=connexion.SQLSelect("select present,passecompose,imparfait,plusqueparfait,passesimple,passeanterieur,futursimple,futuranterieur from verbes_irreguliers where Lower(verbes)='"+this.verbe+"' ");
				while(resultat.next()) {
					System.out.println(resultat.getString("present"));
				}
			}
			catch(SQLException e) {
				
			}
		}
		
		//ON doit maintenant conjuguer sachant le groupe
		
		// On va regarder si le verbe est du 1er groupe ou pas .S'il n'est pas du 1er groupe alors il est du second groupe car on conjugue juste dans le cas ou le verbe est regulier;
		private boolean groupe1() {
			if(verbe.substring(verbe.length()-2,verbe.length()).compareTo("er")==0 )
				return true;
			else
				return false;
		}
		
		// GRAMMAIRE:
                //Les verbes atones
		public boolean est_atone() {
                        if(verbe.substring(verbe.length()-3,verbe.length()-2).compareTo("t")==0||verbe.substring(verbe.length()-3,verbe.length()-2).compareTo("l")==0){
                            return false;
                        }                    
			String consonnes[]= {"b","c","d","f","g","h","j","k","l","m","n","p","q","r","s","t","v","w","x","y","z"};
                        //atone avec une seule consonne intermediaire=>est_atone=1:
			if((verbe.substring(verbe.length()-4,verbe.length()-3).compareTo("e")==0||verbe.substring(verbe.length()-4,verbe.length()-3).compareTo("é")==0 )&& exist_dans_liste(verbe.substring(verbe.length()-3,verbe.length()-2),consonnes) &&  groupe1() ) {
                            est_atone=1;
                            return true;
			}
                        //atone avec deux consonne intermediaires=>est_atone=2:
			if(verbe.length()>=5 && (verbe.substring(verbe.length()-5,verbe.length()-4).compareTo("e")==0 ||verbe.substring(verbe.length()-5,verbe.length()-4).compareTo("é")==0 )&& exist_dans_liste(verbe.substring(verbe.length()-4,verbe.length()-3),consonnes) && exist_dans_liste(verbe.substring(verbe.length()-3,verbe.length()-2),consonnes) &&  groupe1()) {
                            est_atone=2;
			    return true;
			}
			return false;
		}
                
                // Les verbes atones mais qui se doublent justes c-a-d les atones qui ont comme consonne intermediaire:"t"/"l" .Exemple: "jeter"=>"je jette" ou "appler"=>"j'appelle"
		public boolean est_atone_a_double(String verb2){
                    // on a apporte verb2 pour qu'a chaque fois, il n'y ai pas ajout d'une nouvelle "t" ou "l"
                    if((verbe.substring(verbe.length()-3,verbe.length()-2).compareTo("t")==0  && verb2.substring(verb2.length()-2,verb2.length()).compareTo("tt")!=0 )||(verbe.substring(verbe.length()-3,verbe.length()-2).compareTo("l")==0 && verb2.substring(verb2.length()-2,verb2.length()).compareTo("ll")!=0)){
                    if((verbe.substring(verbe.length()-4,verbe.length()-3).compareTo("e")==0||verbe.substring(verbe.length()-4,verbe.length()-3).compareTo("é")==0 ) &&  groupe1() ) {
                        est_atone_a_double_consonne=1;    
                        return true;
			}
                    }
                    return false;
                }
                
                //Fonction qui permet de savoir si le h est muet ou aspire
                public boolean h_est_aspire( ){
                    //les verbes ave h aspire
                    String h_aspire[]={"hacher","hachurer","haflinger","haïr","haire","haler","hâler","haleter","hamster","hancher","handicaper","hanter","happer","haranguer","harasser","harceler","harder","harenguier","harnacher","harponner","harrier","hasarder","hâter","hâtier","hausser","haussier","haver","havre","héler","hennir","hennuyer","hérisser","herser","heurter","hiérarchiser","hocher","homardier","hongrer","honnir","houer","houiller","houppier","hourder","hourdir","housser","hucher","huchier","huer","huir","hululer","humer","hunier","hunter","hurdler","hurler"};
                    if(exist_dans_liste(this.verbe,h_aspire)){
                        return true;// c-a-d que le h n'est pas muet
                    }
                    return false;
                }
		public String correction_grammaticale(String conjugaison,int controle) {
			// la variable controle permet de savoir si c'est pour appliquer la regle atonique qui ne concerne que les verbes atoniques ou les autres regles qui concerne tous 
                        String consonnes[]= {"b","c","d","f","g","h","j","k","l","m","n","p","q","r","s","t","v","w","x","y","z"};
                        
			int i;
			if(controle==0) {
				String particulier[]= {"a","o","u","â"};
				// S le verbe se termine par "cer"(pour les eventuelles apparition de c-cedille "ç" --a,o,u--)
				
				if(verbe.substring(verbe.length()-3,verbe.length()).compareTo("cer")==0) {
					for(i=conjugaison.length()-2;i>=0;i--) {
						if(conjugaison.charAt(i)=='c' ) {
							if(exist_dans_liste(Character.toString(conjugaison.charAt(i+1)),particulier)){
							conjugaison=conjugaison.substring(0,i)+"ç"+conjugaison.substring(i+1,conjugaison.length());
							}
						break;
						}
						}
					}
				
				//Si le verbe se termine par "ger" (pour les eventuelles apparition de c-cedille "gea"=>manger->mangea)--a,o,u,â--
				if(verbe.substring(verbe.length()-3,verbe.length()).compareTo("ger")==0) {
					for(i=conjugaison.length()-2;i>=0;i--) {
						if(conjugaison.charAt(i)=='g' ) {
							if(exist_dans_liste(Character.toString(conjugaison.charAt(i+1)),particulier)){
							conjugaison=conjugaison.substring(0,i)+"ge"+conjugaison.substring(i+1,conjugaison.length());
							}
						break;
						}
					}
				}
                                /*La regle qui dit que deux voyelle ne se suivent pas; Et  s'il y'a lieu on change le premier en apostrophe 
                                 condition: 
                                1) s'applique juste au premier personne du singulier de tous les temps
                                2)pour les pronominaux: lorsque le je est suivi d'une voyelle ou lorsque le "je me" .
                                3)pour les pronominaux est suivi d'une voyelle*/
                                //Pour les non pronominaux: "je"   :
                                if(est_pronominal!=1 &&conjugaison.substring(0, 2).compareTo("je")==0 && (!exist_dans_liste(Character.toString(conjugaison.charAt(3)),consonnes)|| h_est_aspire() && Character.toString(conjugaison.charAt(3)).compareTo("h")==0  ) ){
                                         conjugaison=conjugaison.substring(0,1)+"'"+conjugaison.substring(3,conjugaison.length());
                                     } 
                                //Pour les pronominaux:
                                if(est_pronominal==1){
                               //Pour "je me":
                                if(conjugaison.length()>=6 &&conjugaison.substring(0, 5).compareTo("je me")==0 && (!exist_dans_liste(Character.toString(conjugaison.charAt(6)),consonnes)|| !h_est_aspire() && Character.toString(conjugaison.charAt(6)).compareTo("h")==0 )){
                                         conjugaison=conjugaison.substring(0,4)+"'"+conjugaison.substring(6,conjugaison.length());
                                     }
                                // Pour tu te
                                 if(conjugaison.length()>=6 && conjugaison.substring(0, 5).compareTo("tu te")==0 && (!exist_dans_liste(Character.toString(conjugaison.charAt(6)),consonnes) || !h_est_aspire() && Character.toString(conjugaison.charAt(6)).compareTo("h")==0 ) ){
                                         conjugaison=conjugaison.substring(0,4)+"'"+conjugaison.substring(6,conjugaison.length());
                                     }
                                 //Pour "Il/Elle se"
                                 if(conjugaison.length()>=11 && conjugaison.substring(0, 10).compareTo("Il/Elle se")==0 && (!exist_dans_liste(Character.toString(conjugaison.charAt(11)),consonnes) || !h_est_aspire() && Character.toString(conjugaison.charAt(11)).compareTo("h")==0 ) ){
                                         conjugaison=conjugaison.substring(0,9)+"'"+conjugaison.substring(11,conjugaison.length());
                                     }
                                 //Pour "Ils/Elles se"
                                 if(conjugaison.length()>=13 && conjugaison.substring(0, 12).compareTo("Ils/Elles se")==0 && (!exist_dans_liste(Character.toString(conjugaison.charAt(13)),consonnes) || !h_est_aspire() && Character.toString(conjugaison.charAt(13)).compareTo("h")==0 ) ){
                                         conjugaison=conjugaison.substring(0,11)+"'"+conjugaison.substring(13,conjugaison.length());
                                     }
                                }
			}       
                        
                        //Pour les gerer les verbes atoniques
			if(controle==1 && est_atone()) {
				if(est_atone==1) {
					conjugaison=conjugaison.substring(0,conjugaison.length()-2)+"è"+conjugaison.substring(conjugaison.length()-1);
				}
				else {
					conjugaison=conjugaison.substring(0,conjugaison.length()-3)+"è"+conjugaison.substring(conjugaison.length()-2,conjugaison.length());				}
				}
                        // Pour les verbes atones donnant des doubles consonnes:
                        if(controle==1 && est_atone_a_double(conjugaison)){
                            conjugaison=conjugaison+conjugaison.substring(conjugaison.length()-1);
                        }
			return conjugaison;
		}
		
                
                    //La place de la derniere consonne. pour jouer avec les couleurs lors de l'affichage
            public int index_derniere_consonne(String phrase){
                String consonnes[]= {"b","c","d","f","g","h","j","k","l","m","n","p","q","r","s","t","v","w","x","y","z"};
                int i;
                for(i=phrase.length()-1;i>=0;i--){
                    if(exist_dans_liste(Character.toString(phrase.charAt(i)),consonnes)){
                        return i;
                    }
                }
                return 0;
            }
                
                
                
                //--------------------Le code Pour ajouter et effacer le contenue des espaces de conjugaison------------------------------
    
            public void ajouter_au_textarea(javax.swing.JTextArea textarea,String conjugaison){
                int index;
                index=index_derniere_consonne(conjugaison);
                textarea.setForeground(Color.white);
                if(est_atone_a_double_consonne!=1){
                textarea.append(conjugaison.substring(0,index+1));
                textarea.setForeground(Color.white);
                textarea.append(conjugaison.substring(index+1,conjugaison.length())+"\r\n");
                }
                else{
                textarea.append(conjugaison.substring(0,index));
                textarea.setForeground(Color.white);
                textarea.append(conjugaison.substring(index,conjugaison.length())+"\r\n");
                }
            }
            public void effacer_textarea(javax.swing.JTextArea textarea){
                textarea.setText("");
            }
                
            
           
            
		// La fonction de conjugaison des verbes irreguliers.
            public void conjuguer_regulier(javax.swing.JTextArea present,javax.swing.JTextArea passecompose,javax.swing.JTextArea imparfait,javax.swing.JTextArea plusqueparfait,javax.swing.JTextArea passesimple,javax.swing.JTextArea passeanterieur,javax.swing.JTextArea futursimple,javax.swing.JTextArea futuranterieur) {
			//les temps sont ordonnés comme suit: present-imparfait-passe simple-futur-simple-
			String terminaison_groupe1[][]= {{"e","es","e","ons","ez","ent"},{"ais","ais","ait","ions","iez","aient"},{"ai","as","a","âmes","âtes","èrent"},{"erai","eras","era","erons","erez","eront"}};
			String terminaison_groupe2[][]= {{"is","is","it","issons","issez","issent"},{"issais","issais","issait","issions","issiez","issaient"},{"is","is","it","îmes","îtes","irent"},{"irai","iras","ira","irons","irez","iront"}};
			// les auxiliaires pour les temps composés: passe compose-plusque parfait-passe anterieur- futur anterieur
			String avoir[][]= {{"ai ","as ","a ","avons ","avez ","ont "},{"avais ","avais ","avait ","avions ","aviez ","avaient "},{"eus ","eus ","eut ","eûmes ","eûtes ","eurent "},{"aurais ","auras ","aura ","aurons ","aurez ","auront "}};
			String etre[][]= {{"suis ","es ","est ","sommes ","êtes ","sont "},{"étais ","étais ","était ","étions ","étiez ","étaient "},{"fus ","fus ","fut ","fûmes ","fûtes ","furent "},{"serai ","seras ","sera ","serons ","serez ","seront "}};
			
                       
                        //La liste des 14 verbes qui forment leur temps composés avec “être”:["aller","entrer","passer par","monter","tomber","arriver","naître","venir","sortir","retourner","descendre","rester","partir","mourir"]
			//le verbes:["aller","passer par","naître","venir",""sortir","descendre","partir","mourir"] sont tous des verbes irreguiers et ne sont pas traités dans cette partie
			String verbes14[]= {"entrer","monter","tomber","arriver","retourner","rester"};
			//Les pronoms et les pronominaux
			String pronoms[]= {"je ","tu ","Il/Elle ","Nous ","Vous ","Ils/Elles "};
			String liste_pronominal[]= {"me ","te ","se ","nous ","vous ","se "};
			//les iterateurs: i->pour chaque temps; j->pour chaque pronom
			int i=0 ;
                        int j=0 ;
			//on va casser les deux dernieres lettes du verbes pour mettre les terminaisons
			String verb=new String(this.verbe);
			verb=verb.substring(0,verb.length()-2);
			String verb2=verb;//Cette chaine nous serait utile dans le cas ou on doit mettre un accent grave pour les voyelles atones
			
			//qui va contenir la conjugaison
			String conjugaison;
                        
                        //Liste des textarea des temps
                        javax.swing.JTextArea textarea_temps_simple[]={present,imparfait,passesimple,futursimple};
			javax.swing.JTextArea textarea_temps_compose[]={passecompose,plusqueparfait,passeanterieur,futuranterieur};
                        //on efface le contenu de toutes les boite de conjugaison pour commmencer un nouveau conjugaison;
                        for(i=0;i<4;i++){
                            effacer_textarea(textarea_temps_simple[i]);
                            effacer_textarea(textarea_temps_compose[i]);
                        }

			//On commence cette partie pour les non pronominaux:
                        //non pronominal
			if(est_pronominal!=1) {
				// non pronominal- groupe 1
				if(groupe1()) {
					//non pronominal- groupe 1-Temps simples
					for(i=0;i<4;i++) {
						for(j=0;j<6;j++) {
							if(i==0 && j==0 || i==0 && j==1||i==0 && j==2 ||i==0 && j==5|| i==3) {
								verb2=correction_grammaticale(verb2,1);
								conjugaison=new String(pronoms[j]+verb2+terminaison_groupe1[i][j]);
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(textarea_temps_simple[i],conjugaison);
								continue;
							}
							conjugaison=new String(pronoms[j]+verb+terminaison_groupe1[i][j]);
							conjugaison=correction_grammaticale(conjugaison,0);
							//System.out.println(conjugaison);
                                                        ajouter_au_textarea(textarea_temps_simple[i],conjugaison);
						}
					}
			//non pronominal- groupe 1-Temps composes
					if(exist_dans_liste(verbe,verbes14)) {
						for(i=0;i<4;i++) {
							for(j=0;j<6;j++) {	
								//Si c'est "elle":
								if(j==2){
									conjugaison=new String(pronoms[j]+etre[i][j]+verb+"é"+"(e)");
									conjugaison=correction_grammaticale(conjugaison,0);
									//System.out.println(conjugaison);
                                                                        ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
								}
								//Si c'est "elles":
								else if(j==5) {
									conjugaison=new String(pronoms[j]+etre[i][j]+verb+"é"+"(es)");
									conjugaison=correction_grammaticale(conjugaison,0);
									//System.out.println(conjugaison);
                                                                        ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
								}
								//Si ce n'est ni "elle" ni "elles":
								else {
									conjugaison=new String(pronoms[j]+etre[i][j]+verb+"é");
									conjugaison=correction_grammaticale(conjugaison,0);
									//System.out.println(conjugaison);
                                                                        ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
								}
							}
						}
					}
					//Si ce n'est pas dans les 14 verbes;
					else {
						for(i=0;i<4;i++) {
							for(j=0;j<6;j++) {	
									conjugaison=new String(pronoms[j]+avoir[i][j]+verb+"é");
									conjugaison=correction_grammaticale(conjugaison,0);
									//System.out.println(conjugaison);
                                                                        ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
								}
							}
						}
					}
				
				//non pronominal groupe 2;
				else {
					//non pronominal-groupe2-temps simples:
					for(i=0;i<4;i++) {
						for(j=0;j<6;j++) {
                                                        conjugaison=new String(pronoms[j]+verb+terminaison_groupe2[i][j]);
							//System.out.println();
                                                        ajouter_au_textarea(textarea_temps_simple[i],conjugaison);
						}
						
					}
                                        //non pronominal-2eme groupe-temps compose . Ici on a pas besoin de specifier si l'auxiliaire est etre ou avoir parce que aucun verbe du deuxieme groupe n'apparait dans les 14 verbes 
					for(i=0;i<4;i++) {
						for(j=0;j<6;j++) {	
								conjugaison=new String(pronoms[j]+avoir[i][j]+verb+"i");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                 ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
							}
						}
				}
			}

			// pronominal
			if(est_pronominal==1) {
				//pronominal-groupe1 :
				if(groupe1()) {
                                    
                                    //pronominal-groupe1-temps simples:
					for(i=0;i<4;i++) {
						for(j=0;j<6;j++) {
                                                        if(i==0 && j==0 || i==0 && j==1||i==0 && j==2 ||i==0 && j==5|| i==3) {
								verb2=correction_grammaticale(verb2,1);
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+verb2+terminaison_groupe1[i][j]);
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(textarea_temps_simple[i],conjugaison);
								continue;
							}
							conjugaison=new String(pronoms[j]+liste_pronominal[j]+verb+terminaison_groupe1[i][j]);
							conjugaison=correction_grammaticale(conjugaison,0);
							//System.out.println(conjugaison);
                                                         ajouter_au_textarea(textarea_temps_simple[i],conjugaison);
						}
					}
                                        
                                        //pronominal-groupe1-temps compose
					for(i=0;i<4;i++) {
						for(j=0;j<6;j++) {	
							//Si c'est "elle":
							if(j==2){
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"é"+"(e)");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
							}
							//Si c'est "elles":
                                    			else if(j==5) {
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"é"+"(es)");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
							}
							//Si ce n'est ni "elle" ni "elles":
							else {
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"é");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
							}
						}
					}
				}
				
				//pronominale-groupe 2:
				else {
					//pronominal-groupe2-temps-simple:
					for(i=0;i<4;i++) {
						for(j=0;j<6;j++) {
							conjugaison=new String(pronoms[j]+liste_pronominal[j]+verb+terminaison_groupe2[i][j]);
							conjugaison=correction_grammaticale(conjugaison,0);
							//System.out.println(conjugaison);
                                                         ajouter_au_textarea(textarea_temps_simple[i],conjugaison);
						}
					}
					//pronominal-groupe2-temps compos�s
					for(i=0;i<4;i++) {
						for(j=0;j<6;j++) {	
							//Si c'est "elle":
							if(j==2){
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"i"+"(e)");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
							}
							//Si c'est "elles":
							else if(j==5) {
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"i"+"(es)");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
							}
							//Si ce n'est ni "elle" ni "elles":
							else {
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"i");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(textarea_temps_compose[i],conjugaison);
							}
						}
					}
				}
			}
		}
		
		// Pour la revision;
            
            
           public void conjuguer_regulier_revision(javax.swing.JTextArea table_verbes_a_reviser) {
			//les temps sont ordonnés comme suit: present-imparfait-passe simple-futur-simple-
			String terminaison_groupe1[][]= {{"e","es","e","ons","ez","ent"},{"ais","ais","ait","ions","iez","aient"},{"ai","as","a","âmes","âtes","èrent"},{"erai","eras","era","erons","erez","eront"}};
			String terminaison_groupe2[][]= {{"is","is","it","issons","issez","issent"},{"issais","issais","issait","issions","issiez","issaient"},{"is","is","it","îmes","îtes","irent"},{"irai","iras","ira","irons","irez","iront"}};
			// les auxiliaires pour les temps composés: passe compose-plusque parfait-passe anterieur- futur anterieur
			String avoir[][]= {{"ai ","as ","a ","avons ","avez ","ont "},{"avais ","avais ","avait ","avions ","aviez ","avaient "},{"eus ","eus ","eut ","eûmes ","eûtes ","eurent "},{"aurais ","auras ","aura ","aurons ","aurez ","auront "}};
			String etre[][]= {{"suis ","es ","est ","sommes ","êtes ","sont "},{"étais ","étais ","était ","étions ","étiez ","étaient "},{"fus ","fus ","fut ","fûmes ","fûtes ","furent "},{"serai ","seras ","sera ","serons ","serez ","seront "}};
			
                       
                        //La liste des 14 verbes qui forment leur temps composés avec “être”:["aller","entrer","passer par","monter","tomber","arriver","naître","venir","sortir","retourner","descendre","rester","partir","mourir"]
			//le verbes:["aller","passer par","naître","venir",""sortir","descendre","partir","mourir"] sont tous des verbes irreguiers et ne sont pas traités dans cette partie
			String verbes14[]= {"entrer","monter","tomber","arriver","retourner","rester"};
			//Les pronoms et les pronominaux
			String pronoms[]= {"je ","tu ","Il/Elle ","Nous ","Vous ","Ils/Elles "};
			String liste_pronominal[]= {"me ","te ","se ","nous ","vous ","se "};
			//les iterateurs: i->pour chaque temps; j->pour chaque pronom
			int i=0 ;
                        int j=0 ;
			//on va casser les deux dernieres lettes du verbes pour mettre les terminaisons
			String verb=new String(this.verbe);
			verb=verb.substring(0,verb.length()-2);
			String verb2=verb;//Cette chaine nous serait utile dans le cas ou on doit mettre un accent grave pour les voyelles atones
			
			//qui va contenir la conjugaison
			String conjugaison;
                        
                        //
                        String temps_simples[]={"-PRESENT","-IMPARFAIT","-FUTUR SIMPLE","-PASSE SIMPLE"};
                        String temps_composes[]={"-PASSE COMPOSE","=PLUS QUE PARFAIT","-PASSE ANTERIEUR","FUTUR ANTERIEUR"};
                        //on efface le contenu de toutes les boite de conjugaison pour commmencer un nouveau conjugaison;
                            effacer_textarea(table_verbes_a_reviser);
                            
			//On commence cette partie pour les non pronominaux:
                        //non pronominal
			if(est_pronominal!=1) {
				// non pronominal- groupe 1
				if(groupe1()) {
					//non pronominal- groupe 1-Temps simples
					for(i=0;i<4;i++) {
                                            ajouter_au_textarea(table_verbes_a_reviser,"\n"+temps_simples[i]);
						for(j=0;j<6;j++) {
							if(i==0 && j==0 || i==0 && j==1||i==0 && j==2 ||i==0 && j==5|| i==3) {
								verb2=correction_grammaticale(verb2,1);
								conjugaison=new String(pronoms[j]+verb2+terminaison_groupe1[i][j]);
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
								continue;
							}
							conjugaison=new String(pronoms[j]+verb+terminaison_groupe1[i][j]);
							conjugaison=correction_grammaticale(conjugaison,0);
							//System.out.println(conjugaison);
                                                        ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
						}
					}
			//non pronominal- groupe 1-Temps composes
					if(exist_dans_liste(verbe,verbes14)) {
						for(i=0;i<4;i++) {
                                                    ajouter_au_textarea(table_verbes_a_reviser,"\n"+temps_composes[i]);
							for(j=0;j<6;j++) {	
								//Si c'est "elle":
								if(j==2){
									conjugaison=new String(pronoms[j]+etre[i][j]+verb+"é"+"(e)");
									conjugaison=correction_grammaticale(conjugaison,0);
									//System.out.println(conjugaison);
                                                                        ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
								}
								//Si c'est "elles":
								else if(j==5) {
									conjugaison=new String(pronoms[j]+etre[i][j]+verb+"é"+"(es)");
									conjugaison=correction_grammaticale(conjugaison,0);
									//System.out.println(conjugaison);
                                                                        ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
								}
								//Si ce n'est ni "elle" ni "elles":
								else {
									conjugaison=new String(pronoms[j]+etre[i][j]+verb+"é");
									conjugaison=correction_grammaticale(conjugaison,0);
									//System.out.println(conjugaison);
                                                                        ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
								}
							}
						}
					}
					//Si ce n'est pas dans les 14 verbes;
					else {
						for(i=0;i<4;i++) {
                                                    ajouter_au_textarea(table_verbes_a_reviser,"\n"+temps_composes[i]);
							for(j=0;j<6;j++) {	
									conjugaison=new String(pronoms[j]+avoir[i][j]+verb+"é");
									conjugaison=correction_grammaticale(conjugaison,0);
									//System.out.println(conjugaison);
                                                                        ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
								}
							}
						}
					}
				
				//non pronominal groupe 2;
				else {
					//non pronominal-groupe2-temps simples:
					for(i=0;i<4;i++) {
                                            ajouter_au_textarea(table_verbes_a_reviser,"\n"+temps_simples[i]);
						for(j=0;j<6;j++) {
                                                        conjugaison=new String(pronoms[j]+verb+terminaison_groupe2[i][j]);
							//System.out.println();
                                                        ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
						}
						
					}
                                        //non pronominal-2eme groupe-temps compose . Ici on a pas besoin de specifier si l'auxiliaire est etre ou avoir parce que aucun verbe du deuxieme groupe n'apparait dans les 14 verbes 
					for(i=0;i<4;i++) {
                                            ajouter_au_textarea(table_verbes_a_reviser,"\n"+temps_composes[i]);
						for(j=0;j<6;j++) {	
								conjugaison=new String(pronoms[j]+avoir[i][j]+verb+"i");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                 ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
							}
						}
				}
			}

			// pronominal
			if(est_pronominal==1) {
				//pronominal-groupe1 :
				if(groupe1()) {
                                    
                                    //pronominal-groupe1-temps simples:
					for(i=0;i<4;i++) {
                                            ajouter_au_textarea(table_verbes_a_reviser,"\n"+temps_simples[i]);
						for(j=0;j<6;j++) {
                                                        if(i==0 && j==0 || i==0 && j==1||i==0 && j==2 ||i==0 && j==5|| i==3) {
								verb2=correction_grammaticale(verb2,1);
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+verb2+terminaison_groupe1[i][j]);
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
								continue;
							}
							conjugaison=new String(pronoms[j]+liste_pronominal[j]+verb+terminaison_groupe1[i][j]);
							conjugaison=correction_grammaticale(conjugaison,0);
							//System.out.println(conjugaison);
                                                         ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
						}
					}
                                        
                                        //pronominal-groupe1-temps compose
					for(i=0;i<4;i++) {
                                             ajouter_au_textarea(table_verbes_a_reviser,"\n"+temps_composes[i]);
						for(j=0;j<6;j++) {	
							//Si c'est "elle":
							if(j==2){
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"é"+"(e)");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
							}
							//Si c'est "elles":
                                    			else if(j==5) {
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"é"+"(es)");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
							}
							//Si ce n'est ni "elle" ni "elles":
							else {
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"é");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
							}
						}
					}
				}
				
				//pronominale-groupe 2:
				else {
					//pronominal-groupe2-temps-simple:
					for(i=0;i<4;i++) {
                                             ajouter_au_textarea(table_verbes_a_reviser,"\n"+temps_simples[i]);
						for(j=0;j<6;j++) {
							conjugaison=new String(pronoms[j]+liste_pronominal[j]+verb+terminaison_groupe2[i][j]);
							conjugaison=correction_grammaticale(conjugaison,0);
							//System.out.println(conjugaison);
                                                         ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
						}
					}
					//pronominal-groupe2-temps compos�s
					for(i=0;i<4;i++) {
                                            ajouter_au_textarea(table_verbes_a_reviser,"\n"+temps_composes[i]);
						for(j=0;j<6;j++) {	
							//Si c'est "elle":
							if(j==2){
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"i"+"(e)");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
							}
							//Si c'est "elles":
							else if(j==5) {
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"i"+"(es)");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
							}
							//Si ce n'est ni "elle" ni "elles":
							else {
								conjugaison=new String(pronoms[j]+liste_pronominal[j]+etre[i][j]+verb+"i");
								conjugaison=correction_grammaticale(conjugaison,0);
								//System.out.println(conjugaison);
                                                                ajouter_au_textarea(table_verbes_a_reviser,conjugaison);
							}
						}
					}
				}
			}
		}
		
		@Override
		public String toString() {
			return "Verbe [verbe=" + verbe + "]";
		}
		
		//Ici on se deconnecte de notre base de donnees
		public void deconnecter() {
		connexion.disconnect();
		}
}
