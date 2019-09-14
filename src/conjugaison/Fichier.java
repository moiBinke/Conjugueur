/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conjugaison;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author ADMIN
 */
public class Fichier {
                final Charset UTF_8=Charset.forName("UTF-8");
		final Charset ISO=Charset.forName("ISO-8859-1");
		BufferedReader fluxEntree=null; 
                BufferedWriter flux_a_inserer=null;
                String fichier;
                public Fichier(String fichier){
                    this.fichier=fichier;
                    try {
			fluxEntree=new BufferedReader(new FileReader(this.fichier));
                        flux_a_inserer=new BufferedWriter(new FileWriter(this.fichier,true));
                    }
                    catch (FileNotFoundException e) {
                        // Cette exception est lev�e si l'objet FileInputStream ne trouve
                        // aucun fichier
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        // Celle-ci se produit lors d'une erreur d'�criture ou de lecture
                        e.printStackTrace();
                     }
                }
                 public void ecrire(String ligne_a_ecrire){
                    try{
                        flux_a_inserer.write(ligne_a_ecrire);
                    }
                    catch (IOException e) {
                        // Celle-ci se produit lors d'une erreur d'�criture ou de lecture
                        e.printStackTrace();
                     }
                }
               
                public String lire(){
                    String ligneLue=null;
                    try{
                    	ligneLue=fluxEntree.readLine();
                        if(ligneLue!=null)
                            ligneLue=new String(ligneLue.getBytes(ISO),UTF_8);	
                    }
                    catch (IOException e) {
                        // Celle-ci se produit lors d'une erreur d'�criture ou de lecture
                        e.printStackTrace();
                     }
                    return ligneLue;
                }
                public void fermer_fichier(){
			try {
				if(flux_a_inserer!=null) {
					flux_a_inserer.close();
				}
			}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		
                }
              
