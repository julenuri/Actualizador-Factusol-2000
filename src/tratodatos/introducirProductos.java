/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tratodatos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import modelo.Producto;
import modelo.grupoED;
import org.jdom.JDOMException;

/**
 *
 * @author Victor
 */
public class introducirProductos {
        private grupoED grupo;
        private leerXML xml;
        
       public introducirProductos(String dsn) throws FileNotFoundException, IOException, JDOMException, Exception{
            grupo=grupoED.getGrupoED();
            String path=System.getProperty("user.home") + "\\Mis Documentos";
            System.out.println("Descargando Fichero.Espere por favor...");
           // descargarFichero.descargarFichero("http://www.megasur.es/descargas/tarifas/SetXML.php?cod=223419&val=c246e06615e62d29f85461e62061d3d4", path+"\\megasur.xml");
            System.out.println("Fichero descargado.Procediendo a la comparacion");
            xml=new leerXML(path+"\\megasur.xml");
            this.parsear(xml);
            xml=new leerXML(path+"\\TARIFA.xml");
            this.parsear(xml);
            Map<String,Producto> data=this.grupo.masBarato();
            actualizarBD.actualizarBD(dsn,data);
            //escribirFichero.escribirFichero(path+"\\ficherosBaratos.csv", data);
       }
       
       private int convertirStock(String stock){
           if(stock.equalsIgnoreCase("NO DISPONIBLE")){
               return 0;
           }
           else if(stock.equalsIgnoreCase("POCAS UNIDADES")){
               return 1;
           }
           else if(stock.equalsIgnoreCase("DISPONIBLE")){
               return 2;
           }
           else{//Tiene un numero->Infortisa
               int dat=Integer.parseInt(stock);
               if(dat==0){
                   return 0;
               }
               else if(dat>0 && dat<=10){
                   return 1;
               }
               else{
                   return 2;
               }
           }
       }
       
       private String getReferencia(String referencia){
           int indice=referencia.indexOf(".ES2");
           if(indice!=-1){
               referencia=referencia.substring(0, indice);
           }
           return referencia;
       }
       
       private Double convertirStringFloat(String num){
            if(num.indexOf(',')!=-1){
                num=num.replace(',', '.');
            }   
            return Double.parseDouble(num);
        }
       
       private void parsearProducto(Map<String,String> datos){
           String referencia=this.getReferencia(datos.get("referencia"));
           String nombre=datos.get("nombre");
           double precio=this.convertirStringFloat(datos.get("precio"));
           int codigoSubFam=Integer.parseInt(datos.get("subfamilia"));
           int stock=this.convertirStock(datos.get("stock"));
           String fabricante=datos.get("marca");
           double peso=this.convertirStringFloat(datos.get("peso"));
           String mayorista=datos.get("mayorista");
           String descripcion=datos.get("descripcion");
           String url_imagen=datos.get("imagen");
           this.grupo.addProducto(referencia, nombre, precio, mayorista,codigoSubFam, fabricante, peso,stock,descripcion,url_imagen);
       }
       
       private void parsear(leerXML xml) throws IOException{
            List productos=xml.obtenerDatos();
            ListIterator it=productos.listIterator();
            while(it.hasNext()){
                Map<String,String> datos=(Map)it.next();
                this.parsearProducto(datos);
            }
       }
}
